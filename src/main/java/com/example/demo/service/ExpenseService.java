package com.example.demo.service;

import com.example.demo.dao.GenericDao;
import com.example.demo.dto.BudgetDto;
import com.example.demo.dto.ExpenseDto;
import com.example.demo.model.SpBudget;
import com.example.demo.model.SpExpense;
import com.example.demo.repository.BudgetRepository;
import com.example.demo.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final BudgetRepository budgetRepository;

    @Autowired
    public ExpenseService(ExpenseRepository expenseRepository, BudgetRepository budgetRepository) {
        this.expenseRepository = expenseRepository;
        this.budgetRepository = budgetRepository;
    }

    public GenericDao<ExpenseDto> getByExpenseIdAndBudgetId(Long expenseId, Long budgetId) {
        GenericDao<ExpenseDto> returnDao = new GenericDao<>();
        List<String> errors = new ArrayList<>();
        Optional<SpBudget> budget = budgetRepository.findByIdAndUserId(budgetId, UserService.getCurrentLoggedInUser().getId());
        if (budget.isEmpty()) {
            errors.add("Budget not found");
        }

        if (errors.isEmpty()) {
            Optional<SpExpense> retrievedExpense = expenseRepository.findByIdAndBudgetId(expenseId, budgetId);
            if (retrievedExpense.isEmpty()) {
                errors.add("Expense not found");
            } else {
                returnDao.setObject(new ExpenseDto(retrievedExpense.get()));
            }
        }
        returnDao.setErrors(errors);
        return returnDao;
    }

    public GenericDao<List<ExpenseDto>> getByBudgetId(Long budgetId) {
        GenericDao<List<ExpenseDto>> returnDao = new GenericDao<>();
        List<String> errors = new ArrayList<>();
        Optional<SpBudget> budget = budgetRepository.findByIdAndUserId(budgetId, UserService.getCurrentLoggedInUser().getId());

        if (budget.isEmpty()) {
            errors.add("Budget not found");
        }

        if (errors.isEmpty()) {
            List<SpExpense> retrievedExpense = expenseRepository.findAllByBudgetId(budgetId);
            returnDao.setObject(retrievedExpense.stream().map(ExpenseDto::new).toList());
        }
        return returnDao;
    }

    public GenericDao<ExpenseDto> createExpense(ExpenseDto dto) {
        GenericDao<ExpenseDto> returnDao = new GenericDao<>();
        List<String> errors = new ArrayList<>();

        if (dto.getName() == null || dto.getName().isBlank()) {
            errors.add("Expense name cannot be empty");
        }
        if (dto.getAmount() == null) {
            errors.add("Amount cannot be empty");
        }
        if (dto.getDate() == null) {
            errors.add("Date cannot be empty");
        }
        Optional<SpBudget> budget = Optional.empty();
        if (dto.getBudget().getId() == null) {
            errors.add("Budget ID cannot be empty");
        } else {
            budget = budgetRepository.findByIdAndUserId(dto.getBudget().getId(), UserService.getCurrentLoggedInUser().getId());
            if (budget.isEmpty()) {
                errors.add("Budget does not exists");
            }
        }

        if (errors.isEmpty()) {
            dto.setBudget(new BudgetDto(budget.get(), false));
            SpExpense savedExpense = expenseRepository.save(new SpExpense(dto));
            returnDao.setObject(new ExpenseDto(savedExpense));
        }
        if (!errors.isEmpty()) {
            returnDao.setErrors(errors);
        }
        return returnDao;
    }

    public GenericDao<ExpenseDto> editExpense(ExpenseDto dto) {
        GenericDao<ExpenseDto> returnDao = new GenericDao<>();

        List<String> errors = new ArrayList<>();

        if (dto.getId() == null) {
            errors.add("Expense ID cannot be empty");
        }

        if (dto.getName() == null || dto.getName().isBlank()) {
            errors.add("Expense name cannot be empty");
        }

        if (dto.getAmount() == null) {
            errors.add("Amount cannot be empty");
        }

        if (dto.getDate() == null) {
            errors.add("Date cannot be empty");
        }

        if (errors.isEmpty()) {
            Optional<SpExpense> retrievedExpense = expenseRepository.findById(dto.getId());

            if (retrievedExpense.isPresent()) {
                if (retrievedExpense.get().getBudget().getUser().getId().equals(UserService.getCurrentLoggedInUser().getId())) {
                    retrievedExpense.get().setName(dto.getName());
                    retrievedExpense.get().setDescription(dto.getDescription());
                    retrievedExpense.get().setAmount(dto.getAmount());
                    retrievedExpense.get().setDate(dto.getDate());

                    SpExpense savedExpense = expenseRepository.save(retrievedExpense.get());
                    returnDao.setObject(new ExpenseDto(savedExpense));
                } else {
                    errors.add("Cannot edit expense of another user");
                }
            } else {
                errors.add("Expense does not exist");
            }
        }

        if (!errors.isEmpty()) {
            returnDao.setErrors(errors);
        }

        return returnDao;
    }

    public GenericDao<Boolean> deleteExpense(Long expenseId, Long budgetId) {
        GenericDao<Boolean> returnDao = new GenericDao<>();
        List<String> errors = new ArrayList<>();
        Optional<SpBudget> budget;
        if (budgetId == null) {
            errors.add("Budget ID cannot be empty");
        } else {
            budget = budgetRepository.findByIdAndUserId(budgetId, UserService.getCurrentLoggedInUser().getId());
            if (budget.isEmpty()) {
                errors.add("Budget does not exists");
            }
        }

        Optional<SpExpense> retrievedExpense = expenseRepository.findByIdAndBudgetId(expenseId, budgetId);
        if (retrievedExpense.isPresent()) {
            if (retrievedExpense.get().getBudget().getUser().getId().equals(UserService.getCurrentLoggedInUser().getId())) {
                expenseRepository.deleteById(expenseId);
                returnDao.setObject(true);
            } else {
                errors.add("Cannot delete expense of another user");
                returnDao.setObject(false);
            }
        } else {
            returnDao.setObject(false);
            returnDao.setErrors(errors);
        }
        return returnDao;
    }
}
