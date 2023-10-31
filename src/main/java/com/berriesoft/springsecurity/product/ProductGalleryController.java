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
@RequestMapping("/api/v1/products/productgallerys")
@RequiredArgsConstructor
public class ProductGalleryController {

    private final ProductGalleryService service;
    private final ProductService productService;
    private final AuthorisationService authorisationService;
    Logger logger = LoggerFactory.getLogger(getClass());
    private final String privilegeRole = "ROLE_MANAGER";
    @PostMapping
    public ResponseEntity<Object> addProductGallery(@Valid @RequestBody ProductGalleryRequest productGalleryRequest, HttpServletRequest request) throws IllegalAccessException {
        logger.debug("in add Product Gallery");

        User curUser = authorisationService.getUser();

        if (authorisationService.authorisePrivilege(request, privilegeRole)){
            SpringStatus productStatus = productService.getProductById(productGalleryRequest.getProductid());

            if(productStatus.getErrorCode() == SpringStatus.SUCCESSCODE)
            {
                Product product = (Product) productStatus.getPayload();
                SpringStatus galleryStatus = service.addProductGallery(productGalleryRequest, product, curUser);
                if (galleryStatus.getErrorCode() != SpringStatus.SUCCESSCODE) {
                    throw new ProductGalleryNotCreatedException((ErrorInfoList) galleryStatus.getPayload());
                } else {
                    ProductGallery savedProductGallery = (ProductGallery) galleryStatus.getPayload();
                    URI location = ServletUriComponentsBuilder
                            .fromCurrentRequest()
                            .path("/{id}")
                            .buildAndExpand(savedProductGallery.getProductGalleryId())
                            .toUri();
                    return ResponseEntity.created(location).build();
                }
            }
            }
        return ResponseEntity.status(SpringStatus.ERRORCODE).build();
    }

    @GetMapping(path = "/{id}")
    @ResponseBody
    public ResponseEntity<Object> getProductGalleryById(@PathVariable int id) {
        logger.debug("in getProductGalleryById for id: " + id);
        SpringStatus status = service.getProductGalleryById(id);

        if (status.getErrorCode() != SpringStatus.SUCCESSCODE) {
            throw new ProductGalleryNotFoundException((ErrorInfoList) status.getPayload());
        } else {
            return ResponseEntity.status(SpringStatus.SUCCESSCODE).body((ProductGallery) status.getPayload());
        }
    }

    @GetMapping
    @ResponseBody
    public List<ProductGallery> getAllProductGalleries() {
        logger.debug("in getAllProductGalleries");
        return service.getAllProductGalleries();
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<Object> updateProductGallery(@RequestBody @Valid ProductGalleryRequest productGalleryRequest, @PathVariable int id, HttpServletRequest request) throws IllegalAccessException {
        logger.debug("in updateProductGallery: " + id);

        User curUser = authorisationService.getUser();
        if (authorisationService.authorisePrivilege(request, privilegeRole)){
            SpringStatus status = service.updateProductGallery(id, productGalleryRequest, curUser);
            if (status.getErrorCode() != SpringStatus.SUCCESSCODE) {
                throw new ProductGalleryNotFoundException((ErrorInfoList) status.getPayload());
            } else {
                URI location = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
                return ResponseEntity.created(location).build();
            }
        }
        return ResponseEntity.status(SpringStatus.ERRORCODE).build();
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Object> deleteProductGalleryById(@PathVariable int id, HttpServletRequest request) throws IllegalAccessException{
        logger.debug("in deleteProductGalleryById for id: " + id);

        User curUser = authorisationService.getUser();
        if (authorisationService.authorisePrivilege(request, privilegeRole)){

            SpringStatus status = service.deleteProductGalleryById(id, curUser);
            if (status.getErrorCode() != SpringStatus.SUCCESSCODE) {
                throw new ProductGalleryNotFoundException((ErrorInfoList) status.getPayload());
            } else {
                return ResponseEntity.status(SpringStatus.SUCCESSCODE).build();
            }
        }
        return ResponseEntity.status(SpringStatus.ERRORCODE).build();
    }

}
