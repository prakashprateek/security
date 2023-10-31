package com.berriesoft.springsecurity.status;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code= HttpStatus.BAD_REQUEST)
public class BookingNotCreatedException extends RuntimeException {
    public BookingNotCreatedException(ErrorInfoList errorInfoList)
    {
        super(errorInfoList.toString());
    }
}