package com.example.demo.service;

import com.example.demo.dao.GenericDao;
import com.example.demo.dto.BudgetDto;
import com.example.demo.model.SpBudget;
import com.example.demo.model.SpCategory;
import com.example.demo.repository.BudgetRepository;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BudgetService {
    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;


    @Autowired
    public BudgetService(BudgetRepository budgetRepository, CategoryRepository categoryRepository, UserRepository userRepository) {
        this.budgetRepository = budgetRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    public BudgetDto getById(Long budgetId, Boolean details) {
        return budgetRepository.findByIdAndUserId(budgetId, UserService.getCurrentLoggedInUser().getId()).map(budget -> new BudgetDto(budget, details)).orElse(null);
    }

    public List<BudgetDto> getAll(Boolean details) {
        return budgetRepository.findAllByUserId(UserService.getCurrentLoggedInUser().getId()).stream().map(budget -> new BudgetDto(budget, details)).toList();
    }

    public GenericDao<BudgetDto> createBudget(BudgetDto dto) {
        GenericDao<BudgetDto> returnDao = new GenericDao<>();
        List<String> errors = new ArrayList<>();

        if (dto.getName() == null) {
            errors.add("Budget name cannot be empty");
        }

        if (dto.getAmount() == null) {
            errors.add("Amount cannot be empty");
        }
        if (dto.getStartDate() == null) {
            errors.add("Start date cannot be empty");
        }
        if (dto.getEndDate() == null) {
            errors.add("End date cannot be empty");
        }

        Optional<SpCategory> category = Optional.empty();
        if (dto.getCategory().getId() == null) {
            errors.add("Category ID cannot be empty");
        } else {
            category = categoryRepository.findByIdAndUserId(dto.getCategory().getId(), UserService.getCurrentLoggedInUser().getId());
            if (category.isEmpty()) {
                errors.add("Category does not exists");
            }
        }

        if (errors.isEmpty()) {
            Long uid = UserService.getCurrentLoggedInUser().getId();
            Optional<SpBudget> retrievedBudget = budgetRepository.findByNameAndUserId(dto.getName(), uid);
            if (retrievedBudget.isEmpty() || (retrievedBudget.get().getName().equals(dto.getName()) && retrievedBudget.get().getStartDate() != dto.getStartDate())) {
                SpBudget budget = new SpBudget(dto);
                budget.setCategory(category.get());
                budget.setUser(userRepository.findById(uid).get());
                SpBudget savedBudget = budgetRepository.save(budget);
                returnDao.setObject(new BudgetDto(savedBudget, false));
            } else {
                errors.add("Category already exists");
            }
        }
        if (!errors.isEmpty()) {
            returnDao.setErrors(errors);
        }
        return returnDao;
    }

    public GenericDao<BudgetDto> editBudget(BudgetDto dto) {
        GenericDao<BudgetDto> returnDao = new GenericDao<>();

        List<String> errors = new ArrayList<>();

        if (dto.getId() == null) {
            errors.add("Budget ID cannot be empty");
        }

        if (dto.getName() == null) {
            errors.add("Budget name cannot be empty");
        }

        if (dto.getName() == null || dto.getName().isBlank()) {
            errors.add("Budget name cannot be empty");
        }

        if (dto.getAmount() == null) {
            errors.add("Amount cannot be empty");
        }

        if (dto.getStartDate() == null) {
            errors.add("Start date cannot be empty");
        }

        if (dto.getEndDate() == null) {
            errors.add("End date cannot be empty");
        }

        if (errors.isEmpty()) {
            Optional<SpBudget> retrievedBudget = budgetRepository.findByIdAndUserId(dto.getId(), UserService.getCurrentLoggedInUser().getId());

            if (retrievedBudget.isPresent()) {
                if (retrievedBudget.get().getUser().getId().equals(UserService.getCurrentLoggedInUser().getId())) {
                    retrievedBudget.get().setName(dto.getName());
                    retrievedBudget.get().setAmount(dto.getAmount());
                    retrievedBudget.get().setStartDate(dto.getStartDate());
                    retrievedBudget.get().setEndDate(dto.getEndDate());

                    SpBudget savedBudget = budgetRepository.save(retrievedBudget.get());

                    returnDao.setObject(new BudgetDto(savedBudget, false));
                } else {
                    errors.add("Cannot edit budget of another user");
                }
            } else {
                errors.add("Budget does not exist");
            }
        }

        if (!errors.isEmpty()) {
            returnDao.setErrors(errors);
        }

        return returnDao;
    }

    public GenericDao<Boolean> deleteBudget(Long budgetId) {
        Optional<SpBudget> retrievedBudget = budgetRepository.findByIdAndUserId(budgetId, UserService.getCurrentLoggedInUser().getId());
        List<String> errors = new ArrayList<>();
        if (retrievedBudget.isPresent()) {
            if (retrievedBudget.get().getUser().getId().equals(UserService.getCurrentLoggedInUser().getId())) {
                budgetRepository.deleteById(budgetId);
            } else {
                errors.add("Cannot delete budget of another user");
            }
            return errors.isEmpty() ?
                    new GenericDao<>(true, errors) :
                    new GenericDao<>(false, errors);
        } else {
            errors.add("Budget does not exist");
            return new GenericDao<>(false, errors);
        }
    }
}
