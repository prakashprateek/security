package com.berriesoft.springsecurity.status;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "code", "field", "message" })
public class ErrorInfo {

    public static final int USER_NOT_FOUND_ERRORCODE = 10001;
    public static final String USER_NOT_FOUND_MESSAGE = "User not Found";
    public static final int USER_NOT_CREATED_ERRORCODE = 10002;
    public static final String USER_NOT_CREATED_MESSAGE = "Error - could not create user";
    public static final int DUPLICATE_USERNAME_ERRORCODE = 10003;
    public static final String DUPLICATE_USERNAME_MESSAGE = "Error - username(email) already exists";
    public static final int ILLEGAL_ACCESS_CODE = 10005;
    public static final String ILLEGAL_ACCESS_MESSAGE = "Error - illegal access attempt";
    public static final int DUPLICATE_PRODUCT_ERRORCODE = 10006;
    public static final String DUPLICATE_PRODUCT_MESSAGE = "Error - product code already exists";
    public static final int PRODUCT_NOT_FOUND_ERRORCODE = 10007;
    public static final String PRODUCT_NOT_FOUND_MESSAGE = "Product not found";
    public static final int BOOKING_NOT_CREATED_ERRORCODE = 10012;
    public static final String BOOKING_NOT_CREATED_MESSAGE = "Error - could not create booking";
    public static final int PRICING_NOT_FOUND_ERRORCODE = 10013;
    public static final String PRICING_NOT_FOUND_MESSAGE = "Pricing not found";
    public static final int PRICING_NOT_CREATED_ERRORCODE = 10014;
    public static final String PRICING_NOT_CREATED_MESSAGE = "Error - could not create pricing";
    public static int PRODUCT_NOT_CREATED_ERRORCODE = 10008;
    public static final String PRODUCT_NOT_CREATED_MESSAGE = "Error - could not create product";
    public static final int PRODUCTGALLERY_NOT_CREATED_ERRORCODE = 10009;
    public static final String PRODUCTGALLERY_NOT_CREATED_MESSAGE = "Error - could not create product gallery";
    public static final int PRODUCTGALLERY_NOT_FOUND_ERRORCODE = 10010;
    public static final String PRODUCTGALLERY_NOT_FOUND_MESSAGE = "Product Gallery not found";
    public static final int BOOKING_NOT_FOUND_ERRORCODE = 10011;
    public static final String BOOKING_NOT_FOUND_MESSAGE = "Booking not found";



    private int code;
    private String field;
    private String message;

    public ErrorInfo(int code, String field, String message)
    {
        super();
        this.code = code;
        this.field = field;
        this.message = message;
    }
    public ErrorInfo(int code, String message)
    {
        super();
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    @Override
    public String toString()
    {
        return "ErrorInfo [code=" + code + ", field=" + field + ", message=" + message + "]";
    }

}