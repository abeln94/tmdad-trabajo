package es.unizar.tmdad.lab0.service;

import es.unizar.tmdad.lab0.rabbitmq.RabbitMQEndpoint;
import es.unizar.tmdad.lab0.rabbitmq.RabbitMQEndpoint;
import es.unizar.tmdad.lab0.repo.DBAccess;
import es.unizar.tmdad.lab0.service.TwitterLookupService;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.stereotype.Controller;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

/**
 * STOMP Endpoint
 * Configures and connects to the stomp client
 */
@Configuration
@EnableWebSocketMessageBroker
@Controller
public class STOMPEndpoint extends AbstractWebSocketMessageBrokerConfigurer {

    /**
     * After new query, notify
     */
    @Autowired
    private TwitterLookupService twitter;

    /**
     * To retrieve info about admins
     */
    @Autowired
    private DBAccess twac;

    /**
     * After new processor or level, send updates
     */
    @Autowired
    private RabbitMQEndpoint rabbitMQ;

    @Autowired
    private SimpMessageSendingOperations smso;

    //-------------------register and configure---------------
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/twitter").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @MessageMapping(/*app*/"/settings")
    public void changeSettings(String body, @Header String query, @Header String processor, @Header String level, Principal principal) throws Exception {
        if (twac.isAdmin(principal.getName())) {
            twitter.changeQuery(query);
            rabbitMQ.setProcessorName(processor);
            rabbitMQ.sendProcessorLevel(level);
        }
    }

    //--------------listeners-------------------
    public void onProcessedTweet(Tweet tweet) {
        Map<String, Object> map = new HashMap<>();
        map.put(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON);

        smso.convertAndSend("/topic/search", tweet, map);
    }
}
