package es.unizar.tmdad.lab0.controller;

import es.unizar.tmdad.lab0.rabbitmq.RabbitMQEndpoint;
import es.unizar.tmdad.lab0.repo.DBAccess;
import es.unizar.tmdad.lab0.repo.DBTweetTableRow;
import es.unizar.tmdad.lab0.service.TwitterLookupService;
import es.unizar.tmdad.lab0.settings.Preferences;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Set;
import javax.net.ssl.HttpsURLConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.social.UncategorizedApiException;
import org.springframework.social.twitter.api.SearchResults;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Controller
public class DashboardController {

    @Autowired
    private TwitterLookupService twitter;

    @Autowired
    private DBAccess twac;

    @Autowired
    private RabbitMQEndpoint rabbitMQ;
    
    @Autowired
    private Preferences pref;
    
    @RequestMapping(value="/", method=RequestMethod.GET)
    public String main() {
        return "index";
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UncategorizedApiException.class)
    @ResponseBody
    public String handleUncategorizedApiException() {
        return "error";
    }

    @RequestMapping(value="/template/tweet", method=RequestMethod.GET)
    public String getTweetTemplate() {
        return "template";
    }

    @RequestMapping(value="/template/tweets", method=RequestMethod.GET)
    public String getTweetsTemplate() {
        return "templatebd";
    }

    @RequestMapping(value="/configuration", method=RequestMethod.GET)
    public String main_configuration(Principal principal) {
        if (twac.isAdmin(principal.getName())) {
            return "admin";
        } else {
            return "no-access";
        }
    }

    @RequestMapping(value="/database", method=RequestMethod.GET)
    public String main_database() {
        return "bdsearch";
    }

    @RequestMapping(value="/database/queries", method=RequestMethod.GET)
    @ResponseBody
    public Set<String> getQueries() {
        return twac.findQueries();

    }

    @RequestMapping(value="/database/tweets", method=RequestMethod.GET)
    @ResponseBody
    public ArrayList<DBTweetTableRow> getTweets(String q) {
        return twac.findByQuery(q);

    }

    @MessageMapping(/*app*/"/settings")
    public void changeSettings(String body, @Header String query, @Header String processor, @Header String level, Principal principal) throws Exception {
        if (twac.isAdmin(principal.getName())) {
            twitter.changeQuery(query);
            pref.setConfiguration(processor);
            rabbitMQ.sendSettings(level);
        }
    }

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

    //loader
    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {

        //deshibernate the other machine
        try {
            HttpsURLConnection connection = (HttpsURLConnection) new URL("https://carlos-abel-tmdad-trabajo-2.herokuapp.com/").openConnection();
            connection.setRequestMethod("HEAD");
            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                System.out.println("Oh oh, couldn't akawe machine ("+responseCode+")");
            }

        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
