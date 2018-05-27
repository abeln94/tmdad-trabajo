package es.carlosabel.tmdad.trabajo.dashboard;

import es.carlosabel.tmdad.trabajo.endpoints.TwitterEndpoint;
import es.carlosabel.tmdad.trabajo.repo.DBAccess;
import es.carlosabel.tmdad.trabajo.repo.DBTweetTableRow;
import java.security.Principal;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
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
@RefreshScope
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

    @RequestMapping(value = "/database/tweets", method = RequestMethod.GET)
    @ResponseBody
    public ArrayList<DBTweetTableRow> getTweets() {
        return database.getSavedTweets();

    }

    @Value("${test}")
    String test;

    @RequestMapping(value = "/test")
    @ResponseBody
    public String test() {
        return test;
    }

    //------------------- listeners------------------//
    //help from https://stackoverflow.com/questions/39677660/spring-websocket-how-to-get-number-of-sessions
    @EventListener
    public void onSessionConnectedEvent(SessionConnectedEvent event) {
        System.out.println("user subscribed");
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        twitter.subscribeUser(sha.getSessionId());
    }

    @EventListener
    public void onSessionDisconnectEvent(SessionDisconnectEvent event) {
        System.out.println("user unsubscribed");
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        twitter.unSubscribeUser(sha.getSessionId());
    }

}
