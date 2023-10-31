package com.berriesoft.springsecurity.pricing;

import com.berriesoft.springsecurity.auth.AuthorisationService;
import com.berriesoft.springsecurity.product.Product;
import com.berriesoft.springsecurity.product.ProductService;
import com.berriesoft.springsecurity.status.ErrorInfoList;
import com.berriesoft.springsecurity.status.PricingNotCreatedException;
import com.berriesoft.springsecurity.status.PricingNotFoundException;
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
@RequestMapping("/api/v1/products/pricings")
@RequiredArgsConstructor
public class PricingController {
    private final PricingService service;
    private final ProductService productService;
    private final AuthorisationService authorisationService;
    Logger logger = LoggerFactory.getLogger(getClass());
    private final String privilegeRole = "ROLE_MANAGER";

    @GetMapping
    @ResponseBody
    public List<Pricing> getAllPricing() {
        logger.debug("in getAllPricing");
        return service.getAllPricings();
    }

    @GetMapping(path = "/{id}")
    @ResponseBody
    public ResponseEntity<Object> getPricingById(@PathVariable int id) {
        logger.debug("in getPricingById for id: " + id);
        SpringStatus status = service.getPricingById(id);

        if (status.getErrorCode() != SpringStatus.SUCCESSCODE) {
            throw new PricingNotFoundException((ErrorInfoList) status.getPayload());
        } else {
            return ResponseEntity.status(SpringStatus.SUCCESSCODE).body((Pricing) status.getPayload());
        }
    }
    @PostMapping
    public ResponseEntity<Object> addPricing(@Valid @RequestBody PricingRequest pricingRequest, HttpServletRequest request) throws IllegalAccessException {
        logger.debug("in add Pricing ");

        User curUser = authorisationService.getUser();

        if (authorisationService.authorisePrivilege(request, privilegeRole)){
            SpringStatus productStatus = productService.getProductById(pricingRequest.getProductId());
            logger.debug("step 1 ");
            if(productStatus.getErrorCode() == SpringStatus.SUCCESSCODE)
            {
                Product product = (Product) productStatus.getPayload();
                logger.debug("step 2 ");
                SpringStatus pricingStatus = service.addPricing(pricingRequest, product, curUser);

                logger.debug("step 3 ");
                if (pricingStatus.getErrorCode() != SpringStatus.SUCCESSCODE) {
                    logger.debug("step 4 ");
                    throw new PricingNotCreatedException((ErrorInfoList) pricingStatus.getPayload());
                } else {
                    logger.debug("step 5 ");
                    Pricing savedPricing = (Pricing) pricingStatus.getPayload();
                    URI location = ServletUriComponentsBuilder
                            .fromCurrentRequest()
                            .path("/{id}")
                            .buildAndExpand(savedPricing.getPricingId())
                            .toUri();
                    return ResponseEntity.created(location).build();
                }
            }
        }
        return ResponseEntity.status(SpringStatus.ERRORCODE).build();
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<Object> updatePricing(@Valid @RequestBody PricingRequest pricingRequest, @PathVariable int id, HttpServletRequest request) throws IllegalAccessException {
        logger.debug("in update Pricing ");

        User curUser = authorisationService.getUser();

        if (authorisationService.authorisePrivilege(request, privilegeRole)){
            SpringStatus productStatus = productService.getProductById(pricingRequest.getProductId());
            if(productStatus.getErrorCode() == SpringStatus.SUCCESSCODE)
            {
                Product product = (Product) productStatus.getPayload();
                SpringStatus pricingStatus = service.updatePricing(id, pricingRequest, curUser);
                if (pricingStatus.getErrorCode() != SpringStatus.SUCCESSCODE) {
                    throw new PricingNotFoundException((ErrorInfoList) pricingStatus.getPayload());
                } else {
                    URI location = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
                    return ResponseEntity.created(location).build();
                }
            }
        }
        return ResponseEntity.status(SpringStatus.ERRORCODE).build();
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Object> deletePricingById(@PathVariable int id, HttpServletRequest request) throws IllegalAccessException{
        logger.debug("in deletePricingById for id: " + id);

        User curUser = authorisationService.getUser();
        if (authorisationService.authorisePrivilege(request, privilegeRole)){

            SpringStatus status = service.deletePricingByID(id, curUser);
            if (status.getErrorCode() != SpringStatus.SUCCESSCODE) {
                throw new PricingNotFoundException((ErrorInfoList) status.getPayload());
            } else {
                return ResponseEntity.status(SpringStatus.SUCCESSCODE).build();
            }
        }
        return ResponseEntity.status(SpringStatus.ERRORCODE).build();
    }

}
