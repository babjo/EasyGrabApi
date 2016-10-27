package org.babjo.dto;

import lombok.Data;

/**
 * Created by LCH on 2016. 10. 9..
 */
@Data
public class ExceptionDTO {
    private Error error;

    @Data
    public static class Error{
        private int code;
        private String message;
    }
}
