package com.jeferson.os.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderCommentModel {
    private UUID id;
    private String description;

    @JsonProperty(value = "created_at")
    private OffsetDateTime createdAt;

    @JsonProperty(value = "updated_at")
    private OffsetDateTime updatedAt;
}
