package com.jeferson.os.core.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Validated
@Getter
@Setter
@Component
@ConfigurationProperties("api")
public class ConfigProperties {

    @NotBlank
    private String url;
    private Jwt jwt;
    private Pagination pagination = new Pagination();

    @Getter
    @Setter
    public static class Pagination {
        @NotNull
        private String size = "20";
    }

    @Getter
    @Setter
    public static class Jwt {
        @NotNull
        private String secret;
        @NotNull
        private String expiration;
    }
}
