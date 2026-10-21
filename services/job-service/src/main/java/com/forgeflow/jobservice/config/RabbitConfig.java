package com.forgeflow.jobservice.config;
import org.springframework.amqp.core.*; import org.springframework.context.annotation.Bean; import org.springframework.context.annotation.Configuration;
@Configuration public class RabbitConfig {
 public static final String EXCHANGE="forgeflow.jobs.exchange", QUEUE="forgeflow.jobs", ROUTING_KEY="job.run";
 @Bean DirectExchange jobsExchange(){return new DirectExchange(EXCHANGE,true,false);}
 @Bean Queue jobsQueue(){return QueueBuilder.durable(QUEUE).build();}
 @Bean Binding jobsBinding(Queue jobsQueue,DirectExchange jobsExchange){return BindingBuilder.bind(jobsQueue).to(jobsExchange).with(ROUTING_KEY);}
}
