package com.example.demo.controller;

import com.example.demo.dao.GenericDao;
import com.example.demo.dto.ExpenseDto;
import com.example.demo.model.SpLog;
import com.example.demo.service.ExpenseService;
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
@RequestMapping("/api/expense")
@CrossOrigin(origins = "http://localhost:3000")


@Tag(name = "Expense Controller", description = "Manage expenses")
public class ExpenseController {

    private final ExpenseService expenseService;
    private final LogService logService;
    private final UserService userService;

    @Autowired
    public ExpenseController(ExpenseService expenseService, LogService logService, UserService userService) {
        this.expenseService = expenseService;
        this.logService = logService;
        this.userService = userService;
    }

    // Get all expenses record
    @GetMapping("/budget/{budgetId}")
    @Operation(summary = "Fetch all expenses", description = "Retrieve a list of all expenses of a certain budget using the budget's unique identifier")
    public ResponseEntity<GenericDao<List<ExpenseDto>>> getAllExpensesByBudgetId(@PathVariable(value = "budgetId") Long budgetId) {
        try {
            GenericDao<List<ExpenseDto>> genericDao = expenseService.getByBudgetId(budgetId);

            return genericDao.getErrors().isEmpty() ?
                    new ResponseEntity<>(genericDao, HttpStatus.OK) :
                    new ResponseEntity<>(genericDao, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logService.createLog(new SpLog(null, e.getMessage(), UserService.getCurrentLoggedInUser().getId()));
            System.out.println("Exception caught and log saved.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // To read an expenses record by expense Id and budget Id
    @GetMapping("/{expenseId}/budget/{budgetId}")
    @Operation(summary = "Fetch a specific expense", description = "Retrieve an expense by its unique identifier and its budget's unique identifier")
    public ResponseEntity<GenericDao<ExpenseDto>> getExpensesByExpenseIdAndBudgetId(@PathVariable(value = "expenseId") Long expenseId, @PathVariable(value = "budgetId") Long budgetId) {
        try {
            GenericDao<ExpenseDto> genericDao = expenseService.getByExpenseIdAndBudgetId(expenseId, budgetId);

            return genericDao.getErrors().isEmpty() ?
                    new ResponseEntity<>(genericDao, HttpStatus.OK) :
                    new ResponseEntity<>(genericDao, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logService.createLog(new SpLog(null, e.getMessage(), UserService.getCurrentLoggedInUser().getId()));
            System.out.println("Exception caught and log saved.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    // To create a new expense record
    @PostMapping
    @Operation(summary = "Create a new expense", description = "Create a new expense record in the system")
    public ResponseEntity<GenericDao<ExpenseDto>> createExpense(@RequestBody ExpenseDto expenseDto) {
        try {
            GenericDao<ExpenseDto> genericDao = expenseService.createExpense(expenseDto);

            return genericDao.getErrors().isEmpty() ?
                    new ResponseEntity<>(genericDao, HttpStatus.CREATED) :
                    new ResponseEntity<>(genericDao, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logService.createLog(new SpLog(null, e.getMessage(), UserService.getCurrentLoggedInUser().getId()));
            System.out.println("Exception caught and log saved.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // To update expense
    @PutMapping
    @Operation(summary = "Update an existing expense", description = "Modify the details of an existing expense record")
    public ResponseEntity<GenericDao<ExpenseDto>> editExpense(@RequestBody ExpenseDto expenseDto) {
        try {
            GenericDao<ExpenseDto> genericDao = expenseService.editExpense(expenseDto);

            return genericDao.getErrors().isEmpty() ?
                    new ResponseEntity<>(genericDao, HttpStatus.CREATED) :
                    new ResponseEntity<>(genericDao, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logService.createLog(new SpLog(null, e.getMessage(), UserService.getCurrentLoggedInUser().getId()));
            System.out.println("Exception caught and log saved.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    // To delete expense
    @DeleteMapping("/{expenseId}/budget/{budgetId}")
    @Operation(summary = "Delete an expense", description = "Remove an expense record from the system")
    public ResponseEntity<GenericDao<Boolean>> deleteExpense(@PathVariable(value = "budgetId") Long budgetId,
                                                             @PathVariable(value = "expenseId") Long expenseId) {
        try {
            GenericDao<Boolean> genericDao = expenseService.deleteExpense(budgetId, expenseId);

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
