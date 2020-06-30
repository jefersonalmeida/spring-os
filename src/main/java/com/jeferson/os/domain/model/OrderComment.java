package com.jeferson.os.domain.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Entity
@Table(
        name = "orders_comments",
        schema = "public",
        indexes = {@Index(name = "orders_comments_idx", columnList = "id,order_id")},
        uniqueConstraints = {@UniqueConstraint(name = "orders_comments_unique", columnNames = {"id"})}
)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderComment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(optional = false, cascade = CascadeType.ALL, targetEntity = Order.class)
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Order order;

    @Column(length = 200, nullable = false)
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    @JsonProperty(value = "created_at", access = JsonProperty.Access.READ_ONLY)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    @JsonProperty(value = "updated_at", access = JsonProperty.Access.READ_ONLY)
    private OffsetDateTime updatedAt;
}
