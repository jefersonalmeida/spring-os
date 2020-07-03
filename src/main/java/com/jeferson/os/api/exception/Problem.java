package com.jeferson.os.api.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.List;

@JsonInclude(Include.NON_NULL)
@Getter
@Builder
public class Problem {

    private final Integer status;
    // @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    private final OffsetDateTime timestamp;
    private final String type;
    private final String title;
    private final String detail;
    private final String message;
    private final List<Object> objects;

    @Getter
    @Builder
    public static class Object {
        private final String name;
        private final String message;
    }

}
