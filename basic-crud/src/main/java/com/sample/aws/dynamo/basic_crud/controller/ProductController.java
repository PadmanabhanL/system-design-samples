package com.sample.aws.dynamo.basic_crud.controller;

import com.sample.aws.dynamo.basic_crud.entity.Product;
import com.sample.aws.dynamo.basic_crud.repository.ProductRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        return productRepository.save(product);
    }

    @GetMapping("/{id}")
    public Product getProduct(@PathVariable String id) {
        return productRepository.findById(id);
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productRepository.getAll();
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable String id) {
        productRepository.delete(id);
    }
}
