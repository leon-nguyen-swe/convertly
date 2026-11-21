package dev.convertly.api.config;

import dev.convertly.common.Queues;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {

  @Bean
  public Queue jobQueue() {
    return new Queue(Queues.JOBS, true);
  }

  @Bean
  public Queue eventQueue() {
    return new Queue(Queues.EVENTS, true);
  }

  @Bean
  public MessageConverter messageConverter() {
    return new JacksonJsonMessageConverter();
  }
}
