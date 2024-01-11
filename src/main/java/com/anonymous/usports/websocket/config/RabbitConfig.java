package com.anonymous.usports.websocket.config;

import com.anonymous.usports.global.constant.ChatConstant;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitConfig {

  @Value("${spring.rabbitmq.username}")
  private String rabbitUser;
  @Value("${spring.rabbitmq.password}")
  private String rabbitPwd;
  @Value("${spring.rabbitmq.host}")
  private String rabbitHost;
  @Value("${spring.rabbitmq.port}")
  private int port;


  //Queue 등록
  @Bean
  public Queue queue() {
    return new Queue(ChatConstant.CHAT_QUEUE_NAME,true);
  }

  //Exchange 등록
  @Bean
  public TopicExchange exchange() {
    return new TopicExchange(ChatConstant.CHAT_EXCHANGE_NAME,true,false);
  }

  //Exchange와 Queue의 바인딩
  @Bean
  public Binding binding(Queue queue, TopicExchange exchange) {
    return BindingBuilder.bind(queue).to(exchange).with(ChatConstant.ROUTING_KEY);
  }
  @Bean
  SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
    final SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory());
    factory.setMessageConverter(jsonMessageConverter());
    return factory;
  }

  //messageConverter를 커스터마이징 하기 위해 Bean 새로 등록
  @Bean
  public RabbitTemplate rabbitTemplate() {
    RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
    rabbitTemplate.setMessageConverter(jsonMessageConverter());
    rabbitTemplate.setRoutingKey(ChatConstant.ROUTING_KEY);
    return rabbitTemplate;
  }

  @Bean
  public ConnectionFactory connectionFactory() {
    CachingConnectionFactory factory = new CachingConnectionFactory();
    factory.setHost(rabbitHost);
    factory.setUsername(rabbitUser);
    factory.setPassword(rabbitPwd);
    factory.setVirtualHost("/");
    factory.setPort(port);
    return factory;
  }
  @Bean
  public Jackson2JsonMessageConverter jsonMessageConverter() {
    //LocalDateTIme serializable을 위해
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,false);
    objectMapper.registerModule(dateTimeModule());

    Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);
    return converter;
  }

  @Bean
  public Module dateTimeModule() {
    return new JavaTimeModule();
  }
}
