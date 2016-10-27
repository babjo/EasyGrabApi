package org.babjo.controller;

import org.babjo.dto.ExceptionDTO;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@ControllerAdvice
class GlobalControllerExceptionHandler {

    @ExceptionHandler(Exception.class)
    public @ResponseBody
    ExceptionDTO handleException(Exception ex) {
        ExceptionDTO.Error error = new ExceptionDTO.Error();

        if(ex instanceof MethodArgumentNotValidException){
            BindingResult bindingResult = ((MethodArgumentNotValidException) ex).getBindingResult();
            List<ObjectError> errors = bindingResult.getAllErrors();
            String defaultMessage = "";
            for(ObjectError objectError : errors)
                defaultMessage += objectError.toString() + "\n";
            error.setMessage(defaultMessage);
            error.setCode(400);
        }else{
            error.setMessage(ex.getMessage());
            error.setCode(400);
        }

        ExceptionDTO exceptionDTO = new ExceptionDTO();
        exceptionDTO.setError(error);
        return exceptionDTO;

    }
}