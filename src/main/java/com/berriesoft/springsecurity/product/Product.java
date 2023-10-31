package com.berriesoft.springsecurity.product;

import com.berriesoft.springsecurity.booking.Booking;
import com.berriesoft.springsecurity.pricing.Pricing;
import com.berriesoft.springsecurity.pricing.PricingCategory;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product")
@SQLDelete(sql = "UPDATE product SET is_deleted = 1 WHERE productid=?")
@Where(clause = "is_deleted=0 and is_inactive=0")
public class Product {
    @Id
    @GeneratedValue
    private int productID;
    @NotNull
    private String productCode;
    @NotNull
    private String productDisplayName;

    @Enumerated(EnumType.STRING)
    public ProductCategory productCategory;

    @Enumerated(EnumType.STRING)
    public ProductType productType;

    @NotNull
    private String productDescription;

    @NotNull
    private int isDeleted;
    @NotNull
    private int isInactive;
    @NotNull
    private LocalDateTime createdOn;
    @NotNull
    private int createdBy;
    @NotNull
    private LocalDateTime modifiedOn;
    @NotNull
    private int modifiedBy;

    @OneToMany(mappedBy = "product", fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Booking> bookings;

    @OneToMany(mappedBy = "product", fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Pricing> pricings;

    @OneToMany(mappedBy = "product", fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<ProductGallery> productGallery;
}
