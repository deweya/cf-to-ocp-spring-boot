package com.example.messagingrabbitmq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MessagingRabbitmqApplication {
	static final String topicExchangeName = "spring-boot-exchange";

	static final String queueName = "spring-boot";

	@Value("${my.rabbitmq.uri}") String connectionUri;
  
	@Bean
	Queue queue() {
	  return new Queue(queueName, false);
	}
  
	@Bean
	TopicExchange exchange() {
	  return new TopicExchange(topicExchangeName);
	}
  
	@Bean
	Binding binding(Queue queue, TopicExchange exchange) {
	  return BindingBuilder.bind(queue).to(exchange).with("foo.bar.#");
	}
  
	@Bean
	SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
	  SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
	  container.setConnectionFactory(connectionFactory);
	  container.setQueueNames(queueName);
	  container.setMessageListener(listenerAdapter);
	  return container;
	}
  
	@Bean
	MessageListenerAdapter listenerAdapter(Receiver receiver) {
	  return new MessageListenerAdapter(receiver, "receiveMessage");
	}

	/**
	 * Documentation for this:
	 * https://github.com/spring-projects/spring-boot/issues/6401
	 */
	@Bean
	public CachingConnectionFactory rabbitConnectionFactory(RabbitProperties config) throws Exception {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
		connectionFactory.getRabbitConnectionFactory().setUri(connectionUri);
		//.setUri("amqps://guest:guest@10.0.0.3:5671/virtualhost");
		return connectionFactory;
	}

	public static void main(String[] args) {
		SpringApplication.run(MessagingRabbitmqApplication.class, args);
	}

}
