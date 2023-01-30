package com.cydeo.repository;


import com.cydeo.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product,Long> {
    Product findProductById(Long id);
    List<Product> findAll();
    Optional<Product> findProductByName(String name);



    boolean existsByNameAndAndCategory_Company_Title(String name, String title);
    @Query(nativeQuery = true, value = "SELECT * FROM products WHERE category_id = :id")
    List<Product> findAllByCategoryId(@Param("id") Long id);
    @Query(nativeQuery = true, value = "SELECT quantity_in_stock FROM products WHERE id = :id")
    int getQuantityInStock(@Param("id") Long id);





}
