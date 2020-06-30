package com.jeferson.os.api.controller.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.math.BigDecimal;

@Getter
@Setter
public class OrderRequest {
    @NotBlank
    @Size(max = 200)
    private String description;

    @DecimalMin(value = "0.1", inclusive = false)
    @Digits(integer = 19, fraction = 6)
    private BigDecimal price;

    @Valid
    @NotNull
    private UserIdRequest user;
}
