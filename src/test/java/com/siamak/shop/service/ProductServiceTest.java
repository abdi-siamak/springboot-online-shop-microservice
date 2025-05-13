package com.siamak.shop.service;

import com.siamak.shop.model.Product;
import com.siamak.shop.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductServiceTest {

    private ProductRepository productRepository;
    private ProductService productService;

    @BeforeEach
    public void setup() {
        productRepository = mock(ProductRepository.class); // create a mock repository
        productService = new ProductServiceImpl(productRepository);
    }

    @Test
    public void testGetAllProducts() {

        List<Product> mockProducts = Arrays.asList( // create a mock model
                new Product((long)1, "iPhone 14 pro", "14 pro", BigDecimal.valueOf(800), 25, "images/iPhone_14_pro.png"),
                new Product((long)2, "iPhone 15", "14 pro", BigDecimal.valueOf(800), 25, "images/iPhone_14_pro.png")
        );

        when(productRepository.findAll()).thenReturn(mockProducts);

        //Act
        List<Product> result = productService.getAllProducts();

        //Assert
        assertEquals(2, result.size());
        assertEquals("iPhone 14 pro", result.get(0).getName());
        assertEquals("iPhone 15", result.get(1).getName());
        verify(productRepository, times(1)).findAll();
    }
}
