package com.berriesoft.springsecurity.booking;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
public class BookingRequest {
    private int productid;
    private int pricingid;
    private LocalDateTime bookingStartDateTime;
    private LocalDateTime bookingEndDateTime;
    private Double bookingUnits;
    private Double bookingTotalPrice;
    private String bookingCurrency;
}
