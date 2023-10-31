package com.berriesoft.springsecurity.pricing;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class PricingRequest {
    private int productId;
    private PricingCategory pricingCategory;
    private PricingType pricingType;
    private PricingUnit pricingUnit;
    private Double percentDiscount;
    private Double pricePerUnit;
}
