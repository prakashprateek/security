package com.berriesoft.springsecurity.product;

import com.berriesoft.springsecurity.status.ErrorInfo;
import com.berriesoft.springsecurity.status.ErrorInfoList;
import com.berriesoft.springsecurity.status.SpringStatus;
import com.berriesoft.springsecurity.user.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductGalleryService {
    private final ProductGalleryRepository repository;
    private final ProductService productService;
    Logger logger = LoggerFactory.getLogger(getClass());

    public SpringStatus addProductGallery(ProductGalleryRequest productGalleryRequest, Product product, User user) {
        logger.debug("in addProductGallery for: " + productGalleryRequest);
        ProductGallery productGallery = ProductGallery.builder()
                .product(product)
                .imagePath(productGalleryRequest.getImagePath())
                .isThumbnailImage(productGalleryRequest.isThumbnailImage())
                .createdBy(user.getId())
                .createdOn(LocalDateTime.now())
                .modifiedBy(user.getId())
                .modifiedOn(LocalDateTime.now())
                .build();

        ProductGallery savedProductGallery = repository.save(productGallery);
        if (savedProductGallery.getProduct().equals(productGallery.getProduct())) {
            logger.debug("added product gallery" + productGalleryRequest);
            return new SpringStatus(SpringStatus.SUCCESSCODE, SpringStatus.SUCCESS,
                    savedProductGallery);
        } else {
            logger.debug("Error: couldn't add product gallery " + productGalleryRequest);
            List<ErrorInfo> errorInfo = new ArrayList<ErrorInfo>();
            errorInfo.add(new ErrorInfo(ErrorInfo.PRODUCTGALLERY_NOT_CREATED_ERRORCODE, ErrorInfo.PRODUCTGALLERY_NOT_CREATED_MESSAGE + ": {" + productGalleryRequest.toString() + "}"));
            ErrorInfoList errorInfoList = new ErrorInfoList(errorInfo);
            return new SpringStatus(SpringStatus.ERRORCODE, SpringStatus.ERROR, errorInfoList);
        }

    }

    public SpringStatus updateProductGallery(int id, ProductGalleryRequest productGalleryRequest, User user) {
        logger.debug("in updateProductGallery for: " + id);
        Optional<ProductGallery> findProductGallery = repository.findById(id);
        if (findProductGallery.isPresent()) {
            SpringStatus productStatus = productService.getProductById(productGalleryRequest.getProductid());
            if (productStatus.getErrorCode() == SpringStatus.SUCCESSCODE) {
                Product product = (Product) productStatus.getPayload();
                ProductGallery targetProductGallery = findProductGallery.get();
                targetProductGallery.setProduct(product);
                targetProductGallery.setImagePath(productGalleryRequest.getImagePath());
                targetProductGallery.setThumbnailImage(productGalleryRequest.isThumbnailImage());
                targetProductGallery.setModifiedBy(user.getId());
                targetProductGallery.setModifiedOn(LocalDateTime.now());
                ProductGallery savedProductGallery = repository.save(targetProductGallery);
                logger.debug("updated product gallery" + productGalleryRequest);
                return new SpringStatus(SpringStatus.SUCCESSCODE, SpringStatus.SUCCESS,
                        savedProductGallery);
            } else {
                List<ErrorInfo> errorInfo = new ArrayList<ErrorInfo>();
                errorInfo.add(new ErrorInfo(ErrorInfo.PRODUCT_NOT_FOUND_ERRORCODE, " Product id: " + productGalleryRequest.getProductid(), ErrorInfo.PRODUCT_NOT_FOUND_MESSAGE));
                ErrorInfoList errorInfoList = new ErrorInfoList(errorInfo);
                return new SpringStatus(SpringStatus.ERRORCODE, SpringStatus.ERROR,
                        errorInfoList);
            }
        }
        else{
                List<ErrorInfo> errorInfo = new ArrayList<ErrorInfo>();
                errorInfo.add(new ErrorInfo(ErrorInfo.PRODUCTGALLERY_NOT_FOUND_ERRORCODE, " Product Gallery: " + productGalleryRequest, ErrorInfo.PRODUCTGALLERY_NOT_FOUND_MESSAGE));
                ErrorInfoList errorInfoList = new ErrorInfoList(errorInfo);
                return new SpringStatus(SpringStatus.ERRORCODE, SpringStatus.ERROR,
                        errorInfoList);
            }
        }

        public void deleteProductGallery ( int id, User user){
            logger.debug("in deleteProductGallery for: " + id);

            Optional<ProductGallery> findProductGallery = repository.findById(id);
            if (findProductGallery.isPresent()) {
                ProductGallery targetProductGallery = findProductGallery.get();
                targetProductGallery.setModifiedBy(user.getId());
                targetProductGallery.setModifiedOn(LocalDateTime.now());
                repository.save(targetProductGallery);
            }
            repository.deleteById(id);
        }

        public List<ProductGallery> getAllProductGalleriesByProduct (Product product){
            logger.debug("in getAllProductGalleriesByProduct for: " + product);
            return repository.findAll();
        }

        public SpringStatus getProductGalleryById ( int id){
            Optional<ProductGallery> findProductGallery = repository.findById(id);

            if (findProductGallery.isPresent()) {
                logger.debug("found Product Gallery: " + id);
                ProductGallery productGallery = findProductGallery.get();
                return new SpringStatus(SpringStatus.SUCCESSCODE, SpringStatus.SUCCESS,
                        productGallery);
            } else {
                logger.debug("product gallery not found: " + id);
                List<ErrorInfo> errorInfo = new ArrayList<ErrorInfo>();
                errorInfo.add(new ErrorInfo(ErrorInfo.PRODUCTGALLERY_NOT_FOUND_ERRORCODE, "id:" + id, ErrorInfo.PRODUCTGALLERY_NOT_FOUND_MESSAGE));
                ErrorInfoList errorInfoList = new ErrorInfoList(errorInfo);
                return new SpringStatus(SpringStatus.ERRORCODE, SpringStatus.ERROR,
                        errorInfoList);
            }
        }

        public List<ProductGallery> getAllProductGalleries () {
            return repository.findAll();
        }

        public SpringStatus deleteProductGalleryById ( int id, User curUser){
            Optional<ProductGallery> findProductGallery = repository.findById(id);
            if (findProductGallery.isPresent()) {
                ProductGallery existingProductGallery = findProductGallery.get();
                existingProductGallery.setModifiedBy(curUser.getId());
                existingProductGallery.setModifiedOn(LocalDateTime.now());
                repository.save(existingProductGallery);
                repository.deleteById(id);
                logger.debug("deleted product gallery id: " + id);
                return new SpringStatus(SpringStatus.SUCCESSCODE, SpringStatus.SUCCESS, null);
            } else {
                List<ErrorInfo> errorInfo = new ArrayList<ErrorInfo>();
                errorInfo.add(new ErrorInfo(ErrorInfo.PRODUCTGALLERY_NOT_FOUND_ERRORCODE, "id:" + id, ErrorInfo.PRODUCTGALLERY_NOT_FOUND_MESSAGE));
                ErrorInfoList errorInfoList = new ErrorInfoList(errorInfo);
                return new SpringStatus(SpringStatus.ERRORCODE, SpringStatus.ERROR,
                        errorInfoList);
            }
        }
    }
