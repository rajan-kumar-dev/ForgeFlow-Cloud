package com.forgeflow.jobservice.model;
import jakarta.persistence.*; import java.time.Instant; import java.util.UUID;
@Entity @Table(name="outbox_events", indexes=@Index(name="idx_outbox_unpublished",columnList="published_at,created_at"))
public class OutboxEvent {
 @Id @Column(length=36) private String id;
 @Column(name="aggregate_id",nullable=false,length=36) private String aggregateId;
 @Column(name="event_type",nullable=false,length=80) private String eventType;
 @Column(name="created_at",nullable=false) private Instant createdAt;
 @Column(name="published_at") private Instant publishedAt;
 protected OutboxEvent(){}
 public OutboxEvent(String aggregateId,String eventType){this.id=UUID.randomUUID().toString();this.aggregateId=aggregateId;this.eventType=eventType;this.createdAt=Instant.now();}
 public String getId(){return id;} public String getAggregateId(){return aggregateId;} public String getEventType(){return eventType;} public Instant getCreatedAt(){return createdAt;} public Instant getPublishedAt(){return publishedAt;} public void setPublishedAt(Instant v){publishedAt=v;}
}
