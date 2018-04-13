package es.unizar.tmdad.lab0.rabbitmq;

import es.unizar.tmdad.lab0.service.TwitterLookupService;
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
public class RabbitMQ {
    
    static final String topicExchangeName = "tweets-exchange";
    @Bean
    TopicExchange exchange() {
        return new TopicExchange(topicExchangeName);
    }
    
    static final String inputQueueName = "processedTweets-queue";
    static final String inputTopicName = "processedTweets-topic";
    
    static final String outputQueueName = "rawTweets-queue";
    static final String outputTopicName = "rawTweets-topic";
    
    static final String settingsTopicName = "settings-topic";
    
    @Bean
    Queue queueTweets(){
        return new Queue(inputQueueName, false);
    }

    @Bean
    Binding bindingPing(TopicExchange exchange) {
        return BindingBuilder.bind(queueTweets()).to(exchange).with(inputTopicName);
    }

    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(inputQueueName);
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean
    MessageListenerAdapter listenerAdapter(RabbitMQ receiver) {
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }

    
    
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    @Autowired
    private TwitterLookupService twitterLookupService;

    public void receiveMessage(Tweet tweet) {
        System.out.println("Received processed tweet");
        twitterLookupService.onProcessedTweet(tweet);
    }
    
    public void sendTweet(Tweet tweet){
        System.out.println("Sent tweet to process");
        rabbitTemplate.convertAndSend(topicExchangeName, outputTopicName, tweet);
    }
    
    public void sendSettings(String processorName, String processorLevel){
        System.out.println("Sending settings");
        String message = processorName+"\n"+processorLevel;
        rabbitTemplate.convertAndSend(topicExchangeName, settingsTopicName, message);
    }

}
