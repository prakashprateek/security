package com.berriesoft.springsecurity.product;

import com.berriesoft.springsecurity.booking.Booking;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "productgallery")
@SQLDelete(sql = "UPDATE productgallery SET is_deleted = 1 WHERE product_gallery_id=?")
@Where(clause = "is_deleted=0 and is_inactive=0")
public class ProductGallery {

    @Id
    @GeneratedValue
    private int productGalleryId;
    @NotNull
    private String imagePath;
    @NotNull
    private boolean isThumbnailImage;
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
    @JoinColumn(name = "productid")
    @JsonBackReference
    private Product product;

}
