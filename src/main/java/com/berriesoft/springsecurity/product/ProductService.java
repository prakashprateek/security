package com.berriesoft.springsecurity.product;

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
public class ProductService {
    private final ProductRepository repository;
    Logger logger = LoggerFactory.getLogger(getClass());

    public SpringStatus addProduct(ProductRequest request, User user) {

        // return new SpringStatus(SpringStatus.SUCCESSCODE, SpringStatus.SUCCESS, new Product());
        Optional<Product> findProduct = repository.findByProductCode(request.getProductCode());

        if (findProduct.isPresent()) {
            logger.debug("Error: Product already exists" + request);
            List<ErrorInfo> errorInfo = new ArrayList<ErrorInfo>();
            errorInfo.add(new ErrorInfo(ErrorInfo.DUPLICATE_PRODUCT_ERRORCODE, ErrorInfo.DUPLICATE_PRODUCT_MESSAGE + ": {" + request.toString() + "}"));
            ErrorInfoList errorInfoList = new ErrorInfoList(errorInfo);
            return new SpringStatus(SpringStatus.ERRORCODE, SpringStatus.ERROR,
                    errorInfoList);
        }

        Product product = Product.builder()
                .productCategory(ProductCategory.valueOf(request.getProductCategory()))
                .productCode(request.getProductCode())
                .productDescription(request.getProductDescription())
                .productDisplayName(request.getProductDisplayName())
                .productType(ProductType.valueOf(request.getProductType()))
                .createdBy(user.getId())
                .createdOn(LocalDateTime.now())
                .modifiedBy(user.getId())
                .modifiedOn(LocalDateTime.now())
                .build();
        Product savedProduct = repository.save(product);

        if (savedProduct.getProductCode().equals(request.getProductCode())) {
            logger.debug("added product" + request);
            return new SpringStatus(SpringStatus.SUCCESSCODE, SpringStatus.SUCCESS,
                    savedProduct);
        } else {
            logger.debug("Error: couldnt add product " + request);
            List<ErrorInfo> errorInfo = new ArrayList<ErrorInfo>();
            errorInfo.add(new ErrorInfo(ErrorInfo.PRODUCT_NOT_CREATED_ERRORCODE, ErrorInfo.PRODUCT_NOT_CREATED_MESSAGE + ": {" + request.toString() + "}"));
            ErrorInfoList errorInfoList = new ErrorInfoList(errorInfo);
            return new SpringStatus(SpringStatus.ERRORCODE, SpringStatus.ERROR, errorInfoList);
        }
    }

    public List<Product> getAllProducts() {
        return repository.findAll();
    }

    public SpringStatus getProductById(int id) {

        Optional<Product> findProduct = repository.findById(id);

        if (findProduct.isPresent()) {
            logger.debug("found Product: " + id);
            Product product = findProduct.get();
            return new SpringStatus(SpringStatus.SUCCESSCODE, SpringStatus.SUCCESS,
                    product);
        } else {
            logger.debug("product not found: " + id);
            List<ErrorInfo> errorInfo = new ArrayList<ErrorInfo>();
            errorInfo.add(new ErrorInfo(ErrorInfo.PRODUCT_NOT_FOUND_ERRORCODE, "id:" + id, ErrorInfo.PRODUCT_NOT_FOUND_MESSAGE));
            ErrorInfoList errorInfoList = new ErrorInfoList(errorInfo);
            return new SpringStatus(SpringStatus.ERRORCODE, SpringStatus.ERROR,
                    errorInfoList);
        }
    }

    public SpringStatus updateProduct(ProductRequest request, User user) {

        Optional<Product> findProduct = repository.findByProductCode(request.getProductCode());
        if (findProduct.isPresent()) {
            Product existingProduct = findProduct.get();
            Product updatedProduct = Product.builder()
                    .productID(existingProduct.getProductID())
                    .productCategory(ProductCategory.valueOf(request.getProductCategory()))
                    .productCode(request.getProductCode())
                    .productDescription(request.getProductDescription())
                    .productDisplayName(request.getProductDisplayName())
                    .productType(ProductType.valueOf(request.getProductType()))
                    .createdBy(existingProduct.getCreatedBy())
                    .createdOn(existingProduct.getCreatedOn())
                    .modifiedBy(user.getId())
                    .modifiedOn(LocalDateTime.now())
                    .build();

            Product savedProduct = repository.save(updatedProduct);
            logger.debug("updated product" + request);
            return new SpringStatus(SpringStatus.SUCCESSCODE, SpringStatus.SUCCESS,
                    savedProduct);
        } else {
            List<ErrorInfo> errorInfo = new ArrayList<ErrorInfo>();
            errorInfo.add(new ErrorInfo(ErrorInfo.PRODUCT_NOT_FOUND_ERRORCODE, " Product: " + request, ErrorInfo.PRODUCT_NOT_FOUND_MESSAGE));
            ErrorInfoList errorInfoList = new ErrorInfoList(errorInfo);
            return new SpringStatus(SpringStatus.ERRORCODE, SpringStatus.ERROR,
                    errorInfoList);
        }

    }

    public SpringStatus deleteProductById(int id, User user) {
        Optional<Product> findProduct = repository.findById(id);
        if (findProduct.isPresent()) {
            Product existingProduct = findProduct.get();
            existingProduct.setModifiedBy(user.getId());
            existingProduct.setModifiedOn(LocalDateTime.now());
            repository.save(existingProduct);
            repository.deleteById(id);
            logger.debug("deleted product id: " + id);
            return new SpringStatus(SpringStatus.SUCCESSCODE, SpringStatus.SUCCESS, null);
        } else {
            List<ErrorInfo> errorInfo = new ArrayList<ErrorInfo>();
            errorInfo.add(new ErrorInfo(ErrorInfo.PRODUCT_NOT_FOUND_ERRORCODE, "id:" + id, ErrorInfo.PRODUCT_NOT_FOUND_MESSAGE));
            ErrorInfoList errorInfoList = new ErrorInfoList(errorInfo);
            return new SpringStatus(SpringStatus.ERRORCODE, SpringStatus.ERROR,
                    errorInfoList);
        }
    }



}
