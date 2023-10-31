package com.berriesoft.springsecurity.booking;

import com.berriesoft.springsecurity.auth.AuthorisationService;
import com.berriesoft.springsecurity.pricing.Pricing;
import com.berriesoft.springsecurity.pricing.PricingService;
import com.berriesoft.springsecurity.product.Product;
import com.berriesoft.springsecurity.product.ProductService;
import com.berriesoft.springsecurity.status.BookingNotCreatedException;
import com.berriesoft.springsecurity.status.BookingNotFoundException;
import com.berriesoft.springsecurity.status.ErrorInfoList;
import com.berriesoft.springsecurity.status.SpringStatus;
import com.berriesoft.springsecurity.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService service;
    private final ProductService productService;
    private final PricingService pricingService;
    private final AuthorisationService authorisationService;
    Logger logger = LoggerFactory.getLogger(getClass());
    private final String privilegeRole = "ROLE_MANAGER";


    @GetMapping(path = "/{id}")
    @ResponseBody
    public ResponseEntity<Object> getBookingById(@PathVariable int id) {
        logger.debug("in getBookingById for id: " + id);
        SpringStatus status = service.getBookingById(id);

        if (status.getErrorCode() != SpringStatus.SUCCESSCODE) {
            throw new BookingNotFoundException((ErrorInfoList) status.getPayload());
        } else {
            return ResponseEntity.status(SpringStatus.SUCCESSCODE).body((Booking) status.getPayload());
        }
    }
    @GetMapping
    @ResponseBody
    public List<Booking> getAllBookings() {
        logger.debug("in getAllBookings");
        return service.getAllBookings();
    }

    @PostMapping
    public ResponseEntity<Object> addBooking(@Valid @RequestBody BookingRequest bookingRequest, HttpServletRequest request) throws IllegalAccessException {
        logger.debug("in add Booking ");

        User curUser = authorisationService.getUser();

        if (authorisationService.authorisePrivilege(request, privilegeRole)){
            SpringStatus productStatus = productService.getProductById(bookingRequest.getProductid());
            SpringStatus pricingStatus = pricingService.getPricingById(bookingRequest.getPricingid());
            if((productStatus.getErrorCode() == SpringStatus.SUCCESSCODE) && (pricingStatus.getErrorCode() == SpringStatus.SUCCESSCODE))
            {
                Product product = (Product) productStatus.getPayload();
                Pricing pricing = (Pricing) pricingStatus.getPayload();
                SpringStatus bookingStatus = service.addBooking(bookingRequest, product, pricing, curUser);
                if (bookingStatus.getErrorCode() != SpringStatus.SUCCESSCODE) {
                    throw new BookingNotCreatedException((ErrorInfoList) bookingStatus.getPayload());
                } else {
                    Booking savedBooking = (Booking) bookingStatus.getPayload();
                    URI location = ServletUriComponentsBuilder
                            .fromCurrentRequest()
                            .path("/{id}")
                            .buildAndExpand(savedBooking.getBookingId())
                            .toUri();
                    return ResponseEntity.created(location).build();
                }
            }
        }
        return ResponseEntity.status(SpringStatus.ERRORCODE).build();
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<Object> updateBooking(@Valid @RequestBody BookingRequest bookingRequest, @PathVariable int id, HttpServletRequest request) throws IllegalAccessException {
        logger.debug("in update Booking ");

        User curUser = authorisationService.getUser();

        if (authorisationService.authorisePrivilege(request, privilegeRole)){
            SpringStatus productStatus = productService.getProductById(bookingRequest.getProductid());
            SpringStatus pricingStatus = pricingService.getPricingById(bookingRequest.getPricingid());

            if((productStatus.getErrorCode() == SpringStatus.SUCCESSCODE) && (pricingStatus.getErrorCode() == SpringStatus.SUCCESSCODE))
            {
                Product product = (Product) productStatus.getPayload();
                Pricing pricing = (Pricing) pricingStatus.getPayload();
                SpringStatus bookingStatus = service.updateBooking(id, bookingRequest, product, pricing, curUser);
                if (bookingStatus.getErrorCode() != SpringStatus.SUCCESSCODE) {
                    throw new BookingNotFoundException((ErrorInfoList) bookingStatus.getPayload());
                } else {
                    URI location = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
                    return ResponseEntity.created(location).build();
                }
            }
        }
        return ResponseEntity.status(SpringStatus.ERRORCODE).build();
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Object> deleteBookingById(@PathVariable int id, HttpServletRequest request) throws IllegalAccessException{
        logger.debug("in deleteBookingById for id: " + id);

        User curUser = authorisationService.getUser();
        if (authorisationService.authorisePrivilege(request, privilegeRole)){

            SpringStatus status = service.deleteBookingByID(id, curUser);
            if (status.getErrorCode() != SpringStatus.SUCCESSCODE) {
                throw new BookingNotFoundException((ErrorInfoList) status.getPayload());
            } else {
                return ResponseEntity.status(SpringStatus.SUCCESSCODE).build();
            }
        }
        return ResponseEntity.status(SpringStatus.ERRORCODE).build();
    }

}
