package es.unizar.tmdad.lab0.dashboard;

import es.unizar.tmdad.lab0.endpoints.TwitterEndpoint;
import es.unizar.tmdad.lab0.repo.DBAccess;
import es.unizar.tmdad.lab0.repo.DBTweetTableRow;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;
import java.util.ArrayList;
import javax.net.ssl.HttpsURLConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.social.UncategorizedApiException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/**
 * Dashboard controller:
 * - Definition of uris
 * - Client-related functions
 */
@Controller
public class DashboardController {

    /**
     * After new query or user subscribed/unsubscribed, send updates
     */
    @Autowired
    private TwitterEndpoint twitter;

    /**
     * To retrieve info about queries and tweets from database
     */
    @Autowired
    private DBAccess database;

    //------------------- RequestMappings--------------------//
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String main() {
        return "index";
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UncategorizedApiException.class)
    @ResponseBody
    public String handleUncategorizedApiException() {
        return "error";
    }

    @RequestMapping(value = "/template/tweet", method = RequestMethod.GET)
    public String getTweetTemplate() {
        return "template";
    }

    @RequestMapping(value = "/template/tweets", method = RequestMethod.GET)
    public String getTweetsTemplate() {
        return "templatebd";
    }

    @RequestMapping(value = "/configuration", method = RequestMethod.GET)
    public String main_configuration(Principal principal) {
        if (database.isAdmin(principal.getName())) {
            return "admin";
        } else {
            return "no-access";
        }
    }

    @RequestMapping(value = "/database", method = RequestMethod.GET)
    public String main_database() {
        return "bdsearch";
    }

    @RequestMapping(value = "/database/tweets", method = RequestMethod.GET)
    @ResponseBody
    public ArrayList<DBTweetTableRow> getTweets() {
        return database.getSavedTweets();

    }

    //------------------- listeners------------------//
    //help from https://stackoverflow.com/questions/39677660/spring-websocket-how-to-get-number-of-sessions
    @EventListener
    private void onSessionConnectedEvent(SessionConnectedEvent event) {
        System.out.println("user subscribed");
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        twitter.subscribeUser(sha.getSessionId());
    }

    @EventListener
    private void onSessionDisconnectEvent(SessionDisconnectEvent event) {
        System.out.println("user unsubscribed");
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        twitter.unSubscribeUser(sha.getSessionId());
    }

    //loader TODO: move to other class
    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {

        //deshibernate the other machines
        String[] machines = new String[]{
            "https://carlos-abel-tmdad-trabajo-2.herokuapp.com/",
            "https://carlos-abel-tmdad-trabajo-3.herokuapp.com/",
            "https://carlos-abel-tmdad-trabajo-4.herokuapp.com/"
        };

        for (String machine : machines) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        HttpsURLConnection connection = (HttpsURLConnection) new URL(machine).openConnection();
                        connection.setRequestMethod("HEAD");
                        int responseCode = connection.getResponseCode();
                        if (responseCode != 200) {
                            System.out.println("Oh oh, couldn't akawe machine " + machine + " (" + responseCode + ")");
                        }

                    } catch (MalformedURLException ex) {
                        ex.printStackTrace();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

            }).start();
        }
    }

}
