package com.berriesoft.springsecurity.status;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code= HttpStatus.FORBIDDEN)
public class IllegalAccessException extends RuntimeException
{
    public IllegalAccessException(ErrorInfoList errorInfoList)
    {
        super(errorInfoList.toString());
    }
}