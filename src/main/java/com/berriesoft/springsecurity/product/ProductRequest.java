package com.berriesoft.springsecurity.product;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class ProductRequest {
    //private int productID;
    private String productCode;
    private String productDisplayName;
    private String productCategory; // eg sailing, travel, groceries, books etc
    private String productType; // eg sailboat/yacht/catamaran, taxi/car, fruits/vegetables/dairy, textbooks/studyguides/fiction etc
    private String productDescription;
    private ProductGalleryRequest productGalleryRequest;
}
