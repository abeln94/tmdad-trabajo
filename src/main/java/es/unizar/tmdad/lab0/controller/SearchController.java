package es.unizar.tmdad.lab0.controller;

import es.unizar.tmdad.lab0.rabbitmq.RabbitMQ;
import es.unizar.tmdad.lab0.repo.TweetSaved;
import es.unizar.tmdad.lab0.repo.TweetAccess;
import es.unizar.tmdad.lab0.service.TwitterLookupService;
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
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.social.UncategorizedApiException;
import org.springframework.social.twitter.api.SearchResults;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Controller
public class SearchController {

    @Autowired
    private TwitterLookupService twitter;

    @Autowired
    private TweetAccess twac;

    @Autowired
    private RabbitMQ rabbitMQ;

    @RequestMapping("/")
    public String greeting() {
        return "index";
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UncategorizedApiException.class)
    @ResponseBody
    public SearchResults handleUncategorizedApiException() {
        return twitter.emptyAnswer();
    }

    @RequestMapping("/template")
    public String template() {
        return "template";
    }

    @RequestMapping("/templatebd")
    public String templatebd() {
        return "templatebd";
    }

    @RequestMapping("/admin")
    public String configuration(Principal principal) {
        if (twac.isAdmin(principal.getName())) {
            return "admin";
        } else {
            return "no-access";
        }
    }

    @RequestMapping("/bdsearch")
    public String bdconfiguration() {
        return "bdsearch";
    }

    @RequestMapping("/queries")
    @ResponseBody
    public Set<String> queries() {
        return twac.findQueries();

    }

    @RequestMapping("/bdtweets")
    @ResponseBody
    public ArrayList<TweetSaved> queries(String q) {
        return twac.findByQuery(q);

    }

    @MessageMapping(/*app*/"/settings")
    public void searchQuery(String body, @Header String query, @Header String processor, @Header String level, Principal principal) throws Exception {
        if (twac.isAdmin(principal.getName())) {
            twitter.changeQuery(query);
            twac.setQuery(query);
            rabbitMQ.sendSettings(processor, level);
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
        twitter.changeQuery(twac.getQuery());

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
