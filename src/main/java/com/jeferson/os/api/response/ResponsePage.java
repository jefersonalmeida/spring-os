package com.jeferson.os.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponsePage<T> {

    private T data;
    private long number = 0;
    private long size = 0;
    private long totalElements = 0;
    private long totalPages = 0;
}
