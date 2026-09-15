package com.orderplatform.catalogservice.category.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Endpoint contract only. Category persistence and business logic will be
 * added with the catalog domain implementation.
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @PostMapping
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public void create() {
    }

    @GetMapping
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public void getAll() {
    }

    @GetMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public void getById(@PathVariable UUID categoryId) {
    }

    @PutMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public void update(@PathVariable UUID categoryId) {
    }

    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public void delete(@PathVariable UUID categoryId) {
    }

    @GetMapping("/{categoryId}/products")
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public void getProducts(@PathVariable UUID categoryId) {
    }
}
