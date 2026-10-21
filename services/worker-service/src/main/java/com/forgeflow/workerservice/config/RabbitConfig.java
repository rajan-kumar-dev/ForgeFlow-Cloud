package com.forgeflow.workerservice.config;
import org.springframework.amqp.core.*; import org.springframework.context.annotation.Bean; import org.springframework.context.annotation.Configuration;
@Configuration public class RabbitConfig {@Bean Queue jobsQueue(){return QueueBuilder.durable("forgeflow.jobs").build();}}
