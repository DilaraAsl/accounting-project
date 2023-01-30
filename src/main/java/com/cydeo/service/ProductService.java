package com.cydeo.service;


import com.cydeo.dto.ProductDto;


import java.util.List;

public interface ProductService {
    ProductDto getProductById(Long id);

    List<ProductDto> listAllProducts();

    ProductDto save(ProductDto dto);

    void update(ProductDto dto);

    void deleteById(Long id);


    boolean isNameExist(String name, Long id);

    boolean checkAnyProductExist(Long id);

    boolean isInStock(Long id);

    List<ProductDto> findProductByCategoryId(Long categoryId);


}
