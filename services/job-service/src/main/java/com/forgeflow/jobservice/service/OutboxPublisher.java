package com.forgeflow.jobservice.service;
import com.forgeflow.jobservice.config.RabbitConfig; import com.forgeflow.jobservice.repo.OutboxEventRepository; import org.slf4j.*; import org.springframework.amqp.rabbit.core.RabbitTemplate; import org.springframework.scheduling.annotation.Scheduled; import org.springframework.stereotype.Component; import org.springframework.transaction.annotation.Transactional; import java.time.Instant;
@Component public class OutboxPublisher {
 private static final Logger log=LoggerFactory.getLogger(OutboxPublisher.class); private final OutboxEventRepository repo; private final RabbitTemplate rabbit;
 public OutboxPublisher(OutboxEventRepository repo,RabbitTemplate rabbit){this.repo=repo;this.rabbit=rabbit;}
 @Scheduled(fixedDelayString="${forgeflow.outbox-poll-ms:500}") @Transactional public void publish(){for(var e:repo.findTop50ByPublishedAtIsNullOrderByCreatedAtAsc()){try{rabbit.convertAndSend(RabbitConfig.EXCHANGE,RabbitConfig.ROUTING_KEY,e.getAggregateId()); e.setPublishedAt(Instant.now());}catch(Exception ex){log.warn("Outbox publish failed for job {}: {}",e.getAggregateId(),ex.getMessage());break;}}}
}
