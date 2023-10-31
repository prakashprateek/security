package com.berriesoft.springsecurity.status;

import java.util.List;

public class ErrorInfoList {

    private List<ErrorInfo> errors;

    public List<ErrorInfo> getErrors() {
        return this.errors;
    }

    public ErrorInfoList(List<ErrorInfo> errors) {
        this.errors = errors;
    }

    @Override
    public String toString()
    {
        StringBuilder strBuilder = new StringBuilder();
        for (ErrorInfo errorInfo : errors)
        {
            strBuilder.append(errorInfo.toString());
            strBuilder.append("; ");
        }
        return strBuilder.toString();
    }


}