package com.jeferson.os.api.controller.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class OrderCommentRequest {
    @NotBlank
    @Size(max = 200)
    private String description;
}
