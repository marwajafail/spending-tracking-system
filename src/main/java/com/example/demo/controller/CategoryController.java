package com.example.demo.controller;

import com.example.demo.dao.GenericDao;
import com.example.demo.dto.CategoryDto;
import com.example.demo.model.SpLog;
import com.example.demo.service.CategoryService;
import com.example.demo.service.LogService;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "http://localhost:3000")


@Tag(name = "Category Controller", description = "Manage categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final LogService logService;
    private final UserService userService;

    @Autowired
    public CategoryController(CategoryService categoryService, LogService logService, UserService userService) {
        this.categoryService = categoryService;
        this.logService = logService;
        this.userService = userService;
    }

    // To read all category records
    @GetMapping
    @Operation(summary = "Fetch all categories", description = "Retrieve a list of all categories in the system")
    public ResponseEntity<GenericDao<List<CategoryDto>>> getAllCategories(@RequestParam(value = "details", defaultValue = "false", required = false) boolean details) {
        try {
            List<CategoryDto> dtos = categoryService.getAll(details);
            return !dtos.isEmpty() ?
                    new ResponseEntity<>(new GenericDao<>(dtos, null), HttpStatus.OK) :
                    new ResponseEntity<>(new GenericDao<>(null, List.of("No categories found")), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logService.createLog(new SpLog(null, e.getMessage(), UserService.getCurrentLoggedInUser().getId()));
            System.out.println("Exception caught and log saved.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    //To create a new category record
    @PostMapping
    @Operation(summary = "Create a new category", description = "Create a new category record in the system")
    public ResponseEntity<GenericDao<CategoryDto>> createCategory(@RequestBody CategoryDto categoryDto) {
        try {
            GenericDao<CategoryDto> genericDao = categoryService.createCategory(categoryDto);

            return genericDao.getErrors().isEmpty() ?
                    new ResponseEntity<>(genericDao, HttpStatus.CREATED) :
                    new ResponseEntity<>(genericDao, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logService.createLog(new SpLog(null, e.getMessage(), UserService.getCurrentLoggedInUser().getId()));
            System.out.println("Exception caught and log saved.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // To read a category record
    @GetMapping("/{categoryId}")
    @Operation(summary = "Fetch a specific category", description = "Retrieve a category by its unique identifier")
    public ResponseEntity<GenericDao<CategoryDto>> getCategoryById(@PathVariable(value = "categoryId") Long categoryId, @RequestParam(value = "details", defaultValue = "false", required = false) boolean details) {
        try {
            CategoryDto categoryDto = categoryService.getById(categoryId, details);

            return categoryDto != null ?
                    new ResponseEntity<>(new GenericDao<>(categoryDto, null), HttpStatus.OK) :
                    new ResponseEntity<>(new GenericDao<>(null, List.of("Cateogry with the id " + categoryId + " was not found")), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logService.createLog(new SpLog(null, e.getMessage(), UserService.getCurrentLoggedInUser().getId()));
            System.out.println("Exception caught and log saved.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // To read a category record by name
    @GetMapping("/name/{categoryName}")
    @Operation(summary = "Fetch a specific category", description = "Retrieve a category by its name")
    public ResponseEntity<GenericDao<CategoryDto>> getCategoryByName(@PathVariable(value = "categoryName") String categoryName, @RequestParam(value = "details", defaultValue = "false", required = false) boolean details) {
        try {
            CategoryDto categoryDto = categoryService.getByName(categoryName, details);

            return categoryDto != null ?
                    new ResponseEntity<>(new GenericDao<>(categoryDto, null), HttpStatus.OK) :
                    new ResponseEntity<>(new GenericDao<>(null, List.of("Cateogry with the name " + categoryName + " was not found")), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logService.createLog(new SpLog(null, e.getMessage(), UserService.getCurrentLoggedInUser().getId()));
            System.out.println("Exception caught and log saved.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // To update category record
    @PutMapping
    @Operation(summary = "Update an existing category", description = "Modify the details of an existing category record")
    public ResponseEntity<GenericDao<CategoryDto>> editCategory(@RequestBody CategoryDto categoryDto) {
        try {
            GenericDao<CategoryDto> genericDao = categoryService.editCategory(categoryDto);

            return genericDao.getErrors().isEmpty() ?
                    new ResponseEntity<>(genericDao, HttpStatus.OK) :
                    new ResponseEntity<>(genericDao, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logService.createLog(new SpLog(null, e.getMessage(), UserService.getCurrentLoggedInUser().getId()));
            System.out.println("Exception caught and log saved.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // To delete a category record
    @DeleteMapping("/{categoryId}")
    @Operation(summary = "Delete a category", description = "Remove a category record from the system")
    public ResponseEntity<GenericDao<Boolean>> deleteCategory(@PathVariable(value = "categoryId") Long categoryId) {
        try {
            GenericDao<Boolean> genericDao = categoryService.deleteCategory(categoryId);

            return genericDao.getErrors().isEmpty() ?
                    new ResponseEntity<>(genericDao, HttpStatus.OK) :
                    new ResponseEntity<>(genericDao, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logService.createLog(new SpLog(null, e.getMessage(), UserService.getCurrentLoggedInUser().getId()));
            System.out.println("Exception caught and log saved.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}


