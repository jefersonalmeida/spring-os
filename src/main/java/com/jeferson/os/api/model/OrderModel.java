package com.jeferson.os.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jeferson.os.domain.model.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
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

    @JsonProperty("created_by")
    private UserSampleModel createdBy;

    @JsonProperty("updated_by")
    private UserSampleModel updatedBy;

    @JsonProperty("created_at")
    private Date createdAt;

    @JsonProperty("updated_at")
    private Date updatedAt;

    @JsonProperty(value = "finished_at")
    private Date finishedAt;
}
