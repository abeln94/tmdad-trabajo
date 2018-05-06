package es.unizar.tmdad.lab0.rabbitmq;

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

/**
 * Endpoint of RabbitMQ
 * - Configures exchanges, topics and queues.
 * - Receives messages from subscribed queues
 * - Sends messages to topics
 */
@Component
public class RabbitMQEndpoint {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * After receiving a processed tweet, send to stomp
     * TODO- substitute with STOMPEndopint
     */
    @Autowired
    private TwitterLookupService twitterLookupService;

    /**
     * To get the processor name, to send to its topic
     */
    @Autowired
    private Preferences pref;

    //-------------exchanges-----------------
    static final String tweetsExchangeName = "tweets-exchange";
    static final String settingsExchangeName = "settings-exchange";

    @Bean
    TopicExchange tweetsExchange() {
        return new TopicExchange(tweetsExchangeName);
    }

    @Bean
    TopicExchange settingsExchange() {
        return new TopicExchange(settingsExchangeName);
    }

    //--------------queues---------------
    static final String inputQueueName = "processedTweets-queue";

    @Bean
    Queue queueTweets() {
        return new Queue(inputQueueName, false);
    }

    //----------------topics----------------
    static final String inputTopicName = "processedTweets-topic";
    static final String outputTopicNamePrefix = "rawTweets-topic.";
    static final String settingsTopicNamePrefix = "settings-topic.";

    //--------------bindings-----------------
    @Bean
    Binding binding() {
        return BindingBuilder.bind(queueTweets()).to(tweetsExchange()).with(inputTopicName);
    }

    //--------------redirections------------------
    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueues(queueTweets());
        container.setMessageListener(listenerAdapter());
        return container;
    }

    //-----------adapters--------------
    @Bean
    MessageListenerAdapter listenerAdapter() {
        return new MessageListenerAdapter(this, "receiveMessage");
    }

    //---------------listeners---------------
    public void receiveMessage(Tweet tweet) {
        System.out.println("Received processed tweet");
        twitterLookupService.onProcessedTweet(tweet);
    }

    //---------------senders------------------
    public void sendTweet(Tweet tweet) {
        System.out.println("Sent tweet to process to " + pref.getProcessorName());
        rabbitTemplate.convertAndSend(tweetsExchangeName, outputTopicNamePrefix + pref.getProcessorName(), tweet);
        //TODO: remove logic from here, send to logic class
    }

    public void sendSettings(String processorLevel) {
        System.out.println("Sending settings to " + pref.getProcessorName());
        rabbitTemplate.convertAndSend(settingsExchangeName, settingsTopicNamePrefix + pref.getProcessorName(), processorLevel);
    }

}
