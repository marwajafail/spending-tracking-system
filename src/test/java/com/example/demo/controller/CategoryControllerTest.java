package com.example.demo.controller;
import com.example.demo.dao.GenericDao;
import com.example.demo.dto.CategoryDto;
import com.example.demo.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllCategories() {
        CategoryDto categoryDto = new CategoryDto();
        List<CategoryDto> categories = List.of(categoryDto);
        when(categoryService.getAll(false)).thenReturn(categories);

        ResponseEntity<GenericDao<List<CategoryDto>>> response = categoryController.getAllCategories(false);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(categories, response.getBody().getObject());
    }

    @Test
    void testGetCategoryById() {
        CategoryDto categoryDto = new CategoryDto();
        when(categoryService.getById(1L, false)).thenReturn(categoryDto);

        ResponseEntity<GenericDao<CategoryDto>> response = categoryController.getCategoryById(1L, false);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(categoryDto, response.getBody().getObject());
    }

    @Test
    void testCreateCategory() {
        CategoryDto categoryDto = new CategoryDto();
        GenericDao<CategoryDto> genericDao = new GenericDao<>(categoryDto, Collections.emptyList());
        when(categoryService.createCategory(categoryDto)).thenReturn(genericDao);

        ResponseEntity<GenericDao<CategoryDto>> response = categoryController.createCategory(categoryDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(genericDao, response.getBody());
    }

    @Test
    void testEditCategory() {
        CategoryDto categoryDto = new CategoryDto();
        GenericDao<CategoryDto> genericDao = new GenericDao<>(categoryDto, Collections.emptyList());
        when(categoryService.editCategory(categoryDto)).thenReturn(genericDao);

        ResponseEntity<GenericDao<CategoryDto>> response = categoryController.editCategory(categoryDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(genericDao, response.getBody());
    }

    @Test
    void testDeleteCategory() {
        GenericDao<Boolean> genericDao = new GenericDao<>(true, Collections.emptyList());
        when(categoryService.deleteCategory(1L)).thenReturn(genericDao);

        ResponseEntity<GenericDao<Boolean>> response = categoryController.deleteCategory(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(genericDao, response.getBody());
    }

    @Test
    void testGetCategoryByName() {
        CategoryDto categoryDto = new CategoryDto();
        when(categoryService.getByName("test_category", false)).thenReturn(categoryDto);

        ResponseEntity<GenericDao<CategoryDto>> response = categoryController.getCategoryByName("test_category", false);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(categoryDto, response.getBody().getObject());
    }
}
