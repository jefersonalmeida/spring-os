package com.jeferson.os.domain.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jeferson.os.domain.exception.ApiInvalidArgumentException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(
        name = "orders",
        schema = "public",
        indexes = {@Index(name = "orders_idx", columnList = "id,user_id,status")},
        uniqueConstraints = {@UniqueConstraint(name = "orders_unique", columnNames = {"id"})}
)
@JsonInclude(JsonInclude.Include.NON_NULL)
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(callSuper = true)
public class Order extends Auditable {

    @ManyToOne(optional = false, cascade = CascadeType.ALL, targetEntity = User.class)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(length = 200, nullable = false)
    private String description;

    @Column(precision = 19, scale = 6, nullable = false)
    private BigDecimal price;

    @Column(name = "finished_at")
    private Date finishedAt;

    @OneToMany(mappedBy = "order")
    private List<OrderComment> comments = new ArrayList<>();

    public boolean isAllowFinalize() {
        return OrderStatus.OPEN.equals(getStatus()) && getFinishedAt() == null;
    }

    public boolean isNoAllowFinalize() {
        return !isAllowFinalize();
    }

    public void finalizeOrder() {
        if (isNoAllowFinalize()) {
            throw new ApiInvalidArgumentException("Ordem não pode ser finalizada.");
        }
        setStatus(OrderStatus.FINISHED);
        setFinishedAt(new Date());
    }
}
