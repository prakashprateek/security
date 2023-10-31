package com.berriesoft.springsecurity.booking;


import com.berriesoft.springsecurity.pricing.Pricing;
import com.berriesoft.springsecurity.product.Product;
import com.berriesoft.springsecurity.product.ProductService;
import com.berriesoft.springsecurity.status.ErrorInfo;
import com.berriesoft.springsecurity.status.ErrorInfoList;
import com.berriesoft.springsecurity.status.SpringStatus;
import com.berriesoft.springsecurity.user.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository repository;
    private final ProductService productService;
    Logger logger = LoggerFactory.getLogger(getClass());

    public List<Booking> getAllBookings() {
        return repository.findAll();
    }

    public SpringStatus getBookingById(int id) {

        Optional<Booking> findBooking = repository.findById(id);

        if (findBooking.isPresent()) {
            logger.debug("found Booking : " + id);
            Booking booking = findBooking.get();
            return new SpringStatus(SpringStatus.SUCCESSCODE, SpringStatus.SUCCESS,
                    booking);
        } else {
            logger.debug("Booking not found: " + id);
            List<ErrorInfo> errorInfo = new ArrayList<ErrorInfo>();
            errorInfo.add(new ErrorInfo(ErrorInfo.BOOKING_NOT_FOUND_ERRORCODE, "id:" + id, ErrorInfo.BOOKING_NOT_FOUND_MESSAGE));
            ErrorInfoList errorInfoList = new ErrorInfoList(errorInfo);
            return new SpringStatus(SpringStatus.ERRORCODE, SpringStatus.ERROR,
                    errorInfoList);
        }
    }

    public SpringStatus addBooking(BookingRequest bookingRequest, Product product, Pricing pricing, User curUser) {

        logger.debug("in addBooking for: " + bookingRequest);
        Booking booking = Booking.builder()
                .product(product)
                .pricing(pricing)
                .bookingCurrency(bookingRequest.getBookingCurrency())
                .bookingUnits(bookingRequest.getBookingUnits())
                .bookingStartDateTime(bookingRequest.getBookingStartDateTime())
                .bookingEndDateTime(bookingRequest.getBookingEndDateTime())
                .bookingTotalPrice(bookingRequest.getBookingTotalPrice())
                .createdBy(curUser.getId())
                .createdOn(LocalDateTime.now())
                .modifiedBy(curUser.getId())
                .modifiedOn(LocalDateTime.now())
                .build();

        Booking savedBooking = repository.save(booking);
        if (savedBooking.getProduct().equals(booking.getProduct())) {
            logger.debug("added Booking " + bookingRequest);
            return new SpringStatus(SpringStatus.SUCCESSCODE, SpringStatus.SUCCESS,
                    savedBooking);
        } else {
            logger.debug("Error: couldn't add booking " + bookingRequest);
            List<ErrorInfo> errorInfo = new ArrayList<ErrorInfo>();
            errorInfo.add(new ErrorInfo(ErrorInfo.BOOKING_NOT_CREATED_ERRORCODE, ErrorInfo.BOOKING_NOT_CREATED_MESSAGE + ": {" + bookingRequest.toString() + "}"));
            ErrorInfoList errorInfoList = new ErrorInfoList(errorInfo);
            return new SpringStatus(SpringStatus.ERRORCODE, SpringStatus.ERROR, errorInfoList);
        }
    }

    public SpringStatus updateBooking(int id, BookingRequest bookingRequest, Product product, Pricing pricing, User user) {
        logger.debug("in updateBooking for: " + id);
        Optional<Booking> findBooking = repository.findById(id);
        if (findBooking.isPresent()) {
            Booking targetBooking = findBooking.get();
            targetBooking.setProduct(product);
            targetBooking.setPricing(pricing);
            targetBooking.setBookingCurrency(bookingRequest.getBookingCurrency());
            targetBooking.setBookingUnits(bookingRequest.getBookingUnits());
            targetBooking.setBookingTotalPrice(bookingRequest.getBookingTotalPrice());
            targetBooking.setBookingStartDateTime(bookingRequest.getBookingStartDateTime());
            targetBooking.setBookingEndDateTime(bookingRequest.getBookingEndDateTime());
            targetBooking.setModifiedBy(user.getId());
            targetBooking.setModifiedOn(LocalDateTime.now());
            Booking savedBooking = repository.save(targetBooking);
            logger.debug("updated Booking" + bookingRequest);
            return new SpringStatus(SpringStatus.SUCCESSCODE, SpringStatus.SUCCESS,
                    savedBooking);
        } else {
            List<ErrorInfo> errorInfo = new ArrayList<ErrorInfo>();
            errorInfo.add(new ErrorInfo(ErrorInfo.BOOKING_NOT_FOUND_ERRORCODE, " Product Gallery: " + bookingRequest, ErrorInfo.BOOKING_NOT_FOUND_MESSAGE));
            ErrorInfoList errorInfoList = new ErrorInfoList(errorInfo);
            return new SpringStatus(SpringStatus.ERRORCODE, SpringStatus.ERROR,
                    errorInfoList);
        }
    }


    public SpringStatus deleteBookingByID(int id, User user) {
        logger.debug("in deleteBookingByID for: " + id);

        Optional<Booking> findBooking = repository.findById(id);
        if (findBooking.isPresent()) {
            Booking existingBooking = findBooking.get();
            existingBooking.setModifiedBy(user.getId());
            existingBooking.setModifiedOn(LocalDateTime.now());
            repository.save(existingBooking);
            repository.deleteById(id);
            logger.debug("deleted Booking id: " + id);
            return new SpringStatus(SpringStatus.SUCCESSCODE, SpringStatus.SUCCESS, null);
        } else {
            List<ErrorInfo> errorInfo = new ArrayList<ErrorInfo>();
            errorInfo.add(new ErrorInfo(ErrorInfo.BOOKING_NOT_FOUND_ERRORCODE, "id:" + id, ErrorInfo.BOOKING_NOT_FOUND_MESSAGE));
            ErrorInfoList errorInfoList = new ErrorInfoList(errorInfo);
            return new SpringStatus(SpringStatus.ERRORCODE, SpringStatus.ERROR,
                    errorInfoList);
        }
    }
}
