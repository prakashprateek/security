package com.berriesoft.springsecurity.pricing;

import com.berriesoft.springsecurity.booking.Booking;
import com.berriesoft.springsecurity.booking.BookingRequest;
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
public class PricingService {
    private final PricingRepository repository;
    private final ProductService productService;
    Logger logger = LoggerFactory.getLogger(getClass());

    public List<Pricing> getAllPricings() {
        return repository.findAll();
    }

    public SpringStatus getPricingById(int id) {
        Optional<Pricing> findPricing = repository.findById(id);
        if (findPricing.isPresent()) {
            logger.debug("found Pricing : " + id);
            Pricing pricing = findPricing.get();
            return new SpringStatus(SpringStatus.SUCCESSCODE, SpringStatus.SUCCESS,
                    pricing);
        } else {
            logger.debug("Pricing not found: " + id);
            List<ErrorInfo> errorInfo = new ArrayList<ErrorInfo>();
            errorInfo.add(new ErrorInfo(ErrorInfo.PRICING_NOT_FOUND_ERRORCODE, "id:" + id, ErrorInfo.PRICING_NOT_FOUND_MESSAGE));
            ErrorInfoList errorInfoList = new ErrorInfoList(errorInfo);
            return new SpringStatus(SpringStatus.ERRORCODE, SpringStatus.ERROR,
                    errorInfoList);
        }
    }

    public SpringStatus addPricing(PricingRequest pricingRequest, Product product, User curUser) {

        logger.debug("in addPricing for: " + pricingRequest);
        Pricing pricing = Pricing.builder()
                .product(product)
                .percentDiscount(pricingRequest.getPercentDiscount())
                .pricePerUnit(pricingRequest.getPricePerUnit())
                .pricingCategory(pricingRequest.getPricingCategory())
                .pricingType(pricingRequest.getPricingType())
                .pricingUnit(pricingRequest.getPricingUnit())
                .createdBy(curUser.getId())
                .createdOn(LocalDateTime.now())
                .modifiedBy(curUser.getId())
                .modifiedOn(LocalDateTime.now())
                .build();

        logger.debug(pricing.getCreatedBy() + ", " + pricing.getCreatedOn() + ", " + pricing.isDeleted() + ", " + pricing.isInactive() + ", " + pricing.getModifiedBy() + ", " + pricing.getModifiedOn() + ", " + pricing.getPercentDiscount() + ", " + pricing.getPricePerUnit() + ", " + pricing.getPricingCategory() + ", " + pricing.getPricingType() + ", " + pricing.getPricingUnit() + ", " + pricing.getProduct().getProductID());

        Pricing savedPricing = repository.save(pricing);

        logger.debug("SAVE COMPLETED: ");
        if (savedPricing.getPricePerUnit() == pricing.getPricePerUnit()) {
            logger.debug("added Pricing " + savedPricing);
            return new SpringStatus(SpringStatus.SUCCESSCODE, SpringStatus.SUCCESS,
                    savedPricing);
        } else {
            logger.debug("Error: couldn't add pricing " + pricingRequest);
            List<ErrorInfo> errorInfo = new ArrayList<ErrorInfo>();
            errorInfo.add(new ErrorInfo(ErrorInfo.PRICING_NOT_CREATED_ERRORCODE, ErrorInfo.PRICING_NOT_CREATED_MESSAGE + ": {" + pricingRequest.toString() + "}"));
            ErrorInfoList errorInfoList = new ErrorInfoList(errorInfo);
            return new SpringStatus(SpringStatus.ERRORCODE, SpringStatus.ERROR, errorInfoList);
        }
    }

    public SpringStatus updatePricing(int id, PricingRequest pricingRequest, User user) {
        logger.debug("in updatePricing for: " + id);
        Optional<Pricing> findPricing = repository.findById(id);
        if (findPricing.isPresent()) {
            SpringStatus productStatus = productService.getProductById(pricingRequest.getProductId());
            if (productStatus.getErrorCode() == SpringStatus.SUCCESSCODE) {
                Product product = (Product) productStatus.getPayload();
                Pricing targetPricing = findPricing.get();
                targetPricing.setProduct(product);
                targetPricing.setPercentDiscount(pricingRequest.getPercentDiscount());
                targetPricing.setPricePerUnit(pricingRequest.getPricePerUnit());
                targetPricing.setPricingCategory(pricingRequest.getPricingCategory());
                targetPricing.setPricingType(pricingRequest.getPricingType());
                targetPricing.setPricingUnit(pricingRequest.getPricingUnit());
                targetPricing.setModifiedBy(user.getId());
                targetPricing.setModifiedOn(LocalDateTime.now());
                Pricing savedPricing = repository.save(targetPricing);

                logger.debug("updated Pricing" + pricingRequest);
                return new SpringStatus(SpringStatus.SUCCESSCODE, SpringStatus.SUCCESS,
                        savedPricing);
            } else {
                List<ErrorInfo> errorInfo = new ArrayList<ErrorInfo>();
                errorInfo.add(new ErrorInfo(ErrorInfo.PRODUCT_NOT_FOUND_ERRORCODE, " product id: " + pricingRequest.getProductId(), ErrorInfo.PRODUCT_NOT_FOUND_MESSAGE));
                ErrorInfoList errorInfoList = new ErrorInfoList(errorInfo);
                return new SpringStatus(SpringStatus.ERRORCODE, SpringStatus.ERROR,
                        errorInfoList);
            }
        }
        else{
            List<ErrorInfo> errorInfo = new ArrayList<ErrorInfo>();
            errorInfo.add(new ErrorInfo(ErrorInfo.PRICING_NOT_FOUND_ERRORCODE, " Pricing: " + pricingRequest, ErrorInfo.PRICING_NOT_FOUND_MESSAGE));
            ErrorInfoList errorInfoList = new ErrorInfoList(errorInfo);
            return new SpringStatus(SpringStatus.ERRORCODE, SpringStatus.ERROR,
                    errorInfoList);
        }
    }

    public SpringStatus deletePricingByID(int id, User user) {
        logger.debug("in deletePricingByID for: " + id);

        Optional<Pricing> findPricing = repository.findById(id);
        if (findPricing.isPresent()) {
            Pricing existingPricing = findPricing.get();
            existingPricing.setModifiedBy(user.getId());
            existingPricing.setModifiedOn(LocalDateTime.now());
            repository.save(existingPricing);
            repository.deleteById(id);
            logger.debug("deleted Pricing id: " + id);
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