package com.jeferson.os.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jeferson.os.domain.model.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderModel {
    private UUID id;

    @JsonProperty(value = "client")
    private UserSampleModel user;

    private OrderStatus status;

    private String description;

    private BigDecimal price;

    @JsonProperty(value = "opened_at")
    private OffsetDateTime openedAt;

    @JsonProperty(value = "finished_at")
    private OffsetDateTime finishedAt;
}
