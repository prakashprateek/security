package com.berriesoft.springsecurity.booking;

import com.berriesoft.springsecurity.pricing.Pricing;
import com.berriesoft.springsecurity.product.Product;
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
@Table(name = "booking")
@SQLDelete(sql = "UPDATE booking SET is_deleted = 1 WHERE booking_id=?")
@Where(clause = "is_deleted=0 and is_inactive=0")
public class Booking {
    @Id
    @GeneratedValue
    private int bookingId;
    @NotNull
    private LocalDateTime bookingStartDateTime;
    @NotNull
    private LocalDateTime bookingEndDateTime;
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

    //@OneToMany(mappedBy = "booking", fetch = FetchType.EAGER)
    //@JsonManagedReference
    //private List<Pricing> pricing;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(columnDefinition = "integer", nullable = true, name = "pricing_id")
    @JsonBackReference
    private Pricing pricing;

    @NotNull
    private Double bookingUnits;
    @NotNull
    private Double bookingTotalPrice;
    @NotNull
    private String bookingCurrency;

}
