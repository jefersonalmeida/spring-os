package com.jeferson.os.api.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
public class UserIdRequest {
    @NotNull
    private UUID id;
}
