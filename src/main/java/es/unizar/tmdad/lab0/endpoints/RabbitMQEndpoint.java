package es.unizar.tmdad.lab0.endpoints;

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
     */
    @Autowired
    private STOMPEndpoint stomp;

    /**
     * To get the processor name, to send to its topic
     */
    @Autowired
    private Preferences prefs;

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
    Queue tweetsQueue() {
        return new Queue(inputQueueName, false);
    }

    //----------------topics----------------
    static final String inputTopicName = "processedTweets-topic";
    static final String outputTopicNamePrefix = "rawTweets-topic.";
    static final String settingsTopicNamePrefix = "settings-topic.";

    //--------------bindings-----------------
    @Bean
    Binding tweetsBinding() {
        return BindingBuilder.bind(tweetsQueue()).to(tweetsExchange()).with(inputTopicName);
    }

    //--------------redirections------------------
    @Bean
    SimpleMessageListenerContainer tweetsContainer(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueues(tweetsQueue());
        container.setMessageListener(tweetsListener());
        return container;
    }

    //-----------adapters--------------
    @Bean
    MessageListenerAdapter tweetsListener() {
        return new MessageListenerAdapter(this, "receiveMessage");
    }

    //---------------listeners---------------
    public void receiveMessage(Tweet tweet) {
        System.out.println("Received processed tweet");
        stomp.onProcessedTweet(tweet);
    }

    //----------------setters-----------
    public void setProcessorName(String processorName) {
        prefs.setProcessorName(processorName);
    }

    //---------------senders------------------
    public void sendTweet(Tweet tweet) {
        System.out.println("Sent tweet to process to " + prefs.getProcessorName());
        rabbitTemplate.convertAndSend(tweetsExchangeName, outputTopicNamePrefix + prefs.getProcessorName(), tweet);
    }

    public void sendProcessorLevel(String processorLevel) {
        System.out.println("Sending settings to " + prefs.getProcessorName());
        rabbitTemplate.convertAndSend(settingsExchangeName, settingsTopicNamePrefix + prefs.getProcessorName(), processorLevel);
    }

}
