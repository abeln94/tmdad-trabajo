package es.unizar.tmdad.lab0.saver;

import es.unizar.tmdad.lab0.repo.DBAccess;
import es.unizar.tmdad.lab0.service.TwitterLookupService;
import es.unizar.tmdad.lab0.settings.Preferences;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.stereotype.Component;

@Component
public class SaverRabbitMQEndpoint {
    
    //exchanges
    static final String tweetsExchangeName = "tweets-exchange";
    
    @Bean
    TopicExchange tweetsExchange() {
        return new TopicExchange(tweetsExchangeName);
    }
    
    //queues
    static final String inputQueueName = "saveTweets-queue";
    
    @Bean
    Queue saveTweetsQueue(){
        return new Queue(inputQueueName, false);
    }
    
    
    //topics
    static final String inputTopicName = "rawTweets-topic.*";
    
    
    //bindings
    @Bean
    Binding saveTweetsBinding() {
        return BindingBuilder.bind(saveTweetsQueue()).to(tweetsExchange()).with(inputTopicName);
    }

    //redirections
    @Bean
    SimpleMessageListenerContainer saveTweetsContainer(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueues(saveTweetsQueue());
        container.setMessageListener(saveTweetsAdapter());
        return container;
    }

    
    //adapters
    @Bean
    MessageListenerAdapter saveTweetsAdapter() {
        return new MessageListenerAdapter(this, "receiveMessage");
    }

    
    
    @Autowired
    private Preferences pref;
    
    
    @Autowired
    private DBAccess twac;

    //listeners
    public void receiveMessage(Tweet tweet) {
        System.out.println("Received tweet to save");
        // GUARDAR EN BD
        twac.saveTweet(tweet, pref.getQuery());
    }

}
