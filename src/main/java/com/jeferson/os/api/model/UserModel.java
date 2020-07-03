package com.jeferson.os.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jeferson.os.domain.model.Role;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserModel {
    private UUID id;
    private Boolean active;

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotBlank
    @Email
    @Size(max = 100)
    private String email;

    @Size(max = 20)
    private String phone;

    private String img;

    @JsonProperty(value = "activation_token")
    private String activationToken;

    @JsonProperty(value = "email_verified_at")
    private OffsetDateTime emailVerifiedAt;

    private List<Role> roles;

    @JsonProperty(value = "created_at")
    private OffsetDateTime createdAt;

    @JsonProperty(value = "updated_at")
    private OffsetDateTime updatedAt;
}
