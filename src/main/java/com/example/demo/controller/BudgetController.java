package com.example.demo.controller;

import com.example.demo.dao.GenericDao;
import com.example.demo.dto.BudgetDto;
import com.example.demo.model.SpLog;
import com.example.demo.service.BudgetService;
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
@RequestMapping("/api/budget")
@Tag(name = "Budget Controller", description = "Manage budgets")
public class BudgetController {

    private final BudgetService budgetService;
    private final LogService logService;
    private final UserService userService;

    @Autowired
    public BudgetController(BudgetService budgetService, LogService logService, UserService userService) {
        this.budgetService = budgetService;
        this.logService = logService;
        this.userService = userService;
    }

    // Get all Budgets record
    @GetMapping
    @Operation(summary = "Fetch all budgets", description = "Retrieve a list of all budgets in the system")
    public ResponseEntity<GenericDao<List<BudgetDto>>> getAllBudgets(@RequestParam(value = "details", defaultValue = "false", required = false) boolean details) {
        try {
            List<BudgetDto> budgetDto = budgetService.getAll(details);

            return !budgetDto.isEmpty() ?
                    new ResponseEntity<>(new GenericDao<>(budgetDto, null), HttpStatus.OK) :
                    new ResponseEntity<>(new GenericDao<>(null, List.of("No budgets found")), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logService.createLog(new SpLog(null, e.getMessage(), UserService.getCurrentLoggedInUser().getId()));
            System.out.println("Exception caught and log saved.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // To read all budgets record by category Id
    @GetMapping("/{budgetId}")
    @Operation(summary = "Fetch a specific budget", description = "Retrieve a budget by its unique identifier")
    public ResponseEntity<GenericDao<BudgetDto>> getBudgetById(@PathVariable(value = "budgetId") Long budgetId, @RequestParam(value = "details", defaultValue = "false", required = false) boolean details) {
        try {
            BudgetDto budgetDto = budgetService.getById(budgetId, details);

            return budgetDto != null ?
                    new ResponseEntity<>(new GenericDao<>(budgetDto, null), HttpStatus.OK) :
                    new ResponseEntity<>(new GenericDao<>(null, List.of("Budget with the id " + budgetId + " was not found")), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logService.createLog(new SpLog(null, e.getMessage(), UserService.getCurrentLoggedInUser().getId()));
            System.out.println("Exception caught and log saved.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // To create a new budget record
    @PostMapping
    @Operation(summary = "Create a new budget", description = "Create a new budget record in the system")
    public ResponseEntity<GenericDao<BudgetDto>> createBudget(@RequestBody BudgetDto budgetDto) {
        try {
            GenericDao<BudgetDto> genericDao = budgetService.createBudget(budgetDto);

            return genericDao.getErrors().isEmpty() ?
                    new ResponseEntity<>(genericDao, HttpStatus.CREATED) :
                    new ResponseEntity<>(genericDao, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logService.createLog(new SpLog(null, e.getMessage(), UserService.getCurrentLoggedInUser().getId()));
            System.out.println("Exception caught and log saved.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // To edit budget
    @PutMapping
    @Operation(summary = "Update an existing budget", description = "Modify the details of an existing budget record")
    public ResponseEntity<GenericDao<BudgetDto>> editBudget(@RequestBody BudgetDto budgetDto) {
        try {
            GenericDao<BudgetDto> genericDao = budgetService.editBudget(budgetDto);

            return genericDao.getErrors().isEmpty() ?
                    new ResponseEntity<>(genericDao, HttpStatus.CREATED) :
                    new ResponseEntity<>(genericDao, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logService.createLog(new SpLog(null, e.getMessage(), UserService.getCurrentLoggedInUser().getId()));
            System.out.println("Exception caught and log saved.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    // To delete budget
    @DeleteMapping("/{budgetId}")
    @Operation(summary = "Delete a budget", description = "Remove a budget record from the system")
    public ResponseEntity<GenericDao<Boolean>> deleteBudget(@PathVariable(value = "budgetId") Long budgetId) {
        try {
            GenericDao<Boolean> genericDao = budgetService.deleteBudget(budgetId);

            return genericDao.getErrors().isEmpty() ?
                    new ResponseEntity<>(genericDao, HttpStatus.CREATED) :
                    new ResponseEntity<>(genericDao, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logService.createLog(new SpLog(null, e.getMessage(), UserService.getCurrentLoggedInUser().getId()));
            System.out.println("Exception caught and log saved.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
