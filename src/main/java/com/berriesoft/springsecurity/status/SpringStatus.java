package com.berriesoft.springsecurity.status;

public class SpringStatus
{
    private int statusCode;
    private String statusMessage;
    Object payload;

    public static final int SUCCESSCODE = 200;
    public static final String SUCCESS = "Success";

    public static final int ERRORCODE = 400;
    public static final String ERROR = "Error";
    public static final int ERRORCODE_USERNOTFOUND = 401;
    public static final String ERROR_USERNOTFOUND = "Error: User not found";

    public SpringStatus( int errorCode, String errorMessage, Object payload)
    {
        super();
        this.statusCode = errorCode;
        this.statusMessage = errorMessage;
        this.payload = payload;
    }
    public int getErrorCode()
    {
        return statusCode;
    }
    public void setErrorCode(int errorCode)
    {
        this.statusCode = errorCode;
    }
    public String getErrorMessage()
    {
        return statusMessage;
    }
    public void setErrorMessage(String errorMessage)
    {
        this.statusMessage = errorMessage;
    }

    public Object getPayload()
    {
        return payload;
    }
    public void setPayload(Object payload)
    {
        this.payload = payload;
    }
    @Override
    public String toString()
    {
        return "SpringStatus [errorCode=" + statusCode + ", errorMessage=" + statusMessage + "]";
    }


}
