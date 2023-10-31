package com.berriesoft.springsecurity.pricing;

import com.berriesoft.springsecurity.booking.Booking;
import com.berriesoft.springsecurity.product.Product;
import com.berriesoft.springsecurity.token.TokenType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pricing")
@SQLDelete(sql = "UPDATE pricing SET is_deleted = 1 WHERE pricing_id=?")
@Where(clause = "is_deleted=0 and is_inactive=0")
public class Pricing {

    @Id
    @GeneratedValue
    private int pricingId;

    @Enumerated(EnumType.STRING)
    public PricingCategory pricingCategory = PricingCategory.TIMEBLOCKS;

    @Enumerated(EnumType.STRING)
    public PricingType pricingType = PricingType.RETAIL;

    @Enumerated(EnumType.STRING)
    public PricingUnit pricingUnit = PricingUnit.HOURLY;

    private Double percentDiscount;
    private Double pricePerUnit;

    @NotNull
    private boolean isInactive;
    @NotNull
    private boolean isDeleted;
    @NotNull
    private LocalDateTime createdOn;
    @NotNull
    private int createdBy;
    @NotNull
    private LocalDateTime modifiedOn;
    @NotNull
    private int modifiedBy;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @JsonBackReference
    private Product product;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(columnDefinition="integer", nullable = true, name = "booking_id")
//    @JsonBackReference
//    private Booking booking;

    @ToString.Exclude
    @OneToMany(mappedBy = "pricing", fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Booking> booking;


}
