package com.berriesoft.springsecurity.product;

import com.berriesoft.springsecurity.auth.AuthorisationService;
import com.berriesoft.springsecurity.status.*;
import com.berriesoft.springsecurity.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.lang.IllegalAccessException;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService service;
    private final ProductGalleryService productGalleryService;
    private final AuthorisationService authorisationService;
    private final String privilegeRole = "ROLE_MANAGER";
    Logger logger = LoggerFactory.getLogger(getClass());

    @GetMapping
    @ResponseBody
    public List<Product> getAllProducts() {
        logger.debug("in getAllProducts");
        return service.getAllProducts();
    }

    @GetMapping(path = "/{id}")
    @ResponseBody
    public ResponseEntity<Object> getProductById(@PathVariable int id) {
        logger.debug("in getProductById for id: " + id);
        SpringStatus status = service.getProductById(id);

        if (status.getErrorCode() != SpringStatus.SUCCESSCODE) {
            throw new ProductNotFoundException((ErrorInfoList) status.getPayload());
        } else {
            return ResponseEntity.status(SpringStatus.SUCCESSCODE).body((Product) status.getPayload());
        }
    }

    @PostMapping
    public ResponseEntity<Object> addProduct(@Valid @RequestBody ProductRequest productRequest, HttpServletRequest request) throws IllegalAccessException {
        logger.debug("in add Product");

        User curUser = authorisationService.getUser();
        if (authorisationService.authorisePrivilege(request, privilegeRole)){

            SpringStatus status = service.addProduct(productRequest, curUser);
            if (status.getErrorCode() != SpringStatus.SUCCESSCODE) {
                throw new ProductNotCreatedException((ErrorInfoList) status.getPayload());
            } else {
                Product savedProduct = (Product)status.getPayload();
                logger.debug("gallery: " + productRequest.getProductGalleryRequest());
                productGalleryService.addProductGallery(productRequest.getProductGalleryRequest(), savedProduct, curUser);
                URI location = ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(savedProduct.getProductID())
                        .toUri();
                return ResponseEntity.created(location).build();
            }
        }

        return ResponseEntity.status(SpringStatus.ERRORCODE).build();
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<Object> updateProduct(@RequestBody @Valid ProductRequest productRequest, @PathVariable int id, HttpServletRequest request) throws IllegalAccessException {
        logger.debug("in UpdateProduct: " + id);

        User curUser = authorisationService.getUser();
        if (authorisationService.authorisePrivilege(request, privilegeRole)){

            SpringStatus status = service.updateProduct(productRequest, curUser);
            if (status.getErrorCode() != SpringStatus.SUCCESSCODE) {
                throw new ProductNotFoundException((ErrorInfoList) status.getPayload());
            } else {
                URI location = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
                return ResponseEntity.created(location).build();
            }
        }
        return ResponseEntity.status(SpringStatus.ERRORCODE).build();
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Object> deleteProductById(@PathVariable int id, HttpServletRequest request) throws IllegalAccessException{
        logger.debug("in deleteProductById for id: " + id);

        User curUser = authorisationService.getUser();
        if (authorisationService.authorisePrivilege(request, privilegeRole)){

            SpringStatus status = service.deleteProductById(id, curUser);
            if (status.getErrorCode() != SpringStatus.SUCCESSCODE) {
                throw new ProductNotFoundException((ErrorInfoList) status.getPayload());
            } else {
                return ResponseEntity.status(SpringStatus.SUCCESSCODE).build();
            }
        }
        return ResponseEntity.status(SpringStatus.ERRORCODE).build();
    }

}
