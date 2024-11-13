package com.example.demo.service;

import com.example.demo.dao.GenericDao;
import com.example.demo.dto.CategoryDto;
import com.example.demo.model.SpCategory;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    public CategoryDto getById(Long catId, Boolean details) {
        return categoryRepository.findByIdAndUserIdOrUserId(catId, UserService.getCurrentLoggedInUser().getId(), 1L).map(category -> new CategoryDto(category, details)).orElse(null);
    }

    public CategoryDto getByName(String name, Boolean details) {
        return categoryRepository.findByNameAndUserIdOrUserId(name, UserService.getCurrentLoggedInUser().getId(), 1L).map(category -> new CategoryDto(category, details)).orElse(null);
    }

    public List<CategoryDto> getAll(Boolean details) {
        return categoryRepository.findAllByUserIdOrUserId(UserService.getCurrentLoggedInUser().getId(), 1L).stream().map(category -> new CategoryDto(category, details)).toList();
    }

    public GenericDao<CategoryDto> createCategory(CategoryDto dto) {
        GenericDao<CategoryDto> returnDao = new GenericDao<>();
        List<String> errors = new ArrayList<>();

        if (dto.getName() == null || dto.getName().isBlank()) {
            errors.add("Category name cannot be empty");
        }

        if (errors.isEmpty()) {
            Long uid = UserService.getCurrentLoggedInUser().getId();
            Optional<SpCategory> retrievedCategory = categoryRepository.findByNameAndUserId(dto.getName(), uid);

            if (retrievedCategory.isEmpty()) {
                SpCategory category = new SpCategory(dto);
                category.setUser(userRepository.findById(uid).get());
                category = categoryRepository.save(category);
                returnDao.setObject(new CategoryDto(category, false));
            } else {
                errors.add("Category already exists");
            }
        }

        if (!errors.isEmpty()) {
            returnDao.setErrors(errors);
        }

        return returnDao;
    }


    public GenericDao<CategoryDto> editCategory(CategoryDto dto) {
        GenericDao<CategoryDto> returnDao = new GenericDao<>();

        List<String> errors = new ArrayList<>();

        if (dto.getId() == null) {
            errors.add("Category ID cannot be empty");
        }

        if (dto.getName() == null || dto.getName().isBlank()) {
            errors.add("Category name cannot be empty");
        }

        if (errors.isEmpty()) {
            Optional<SpCategory> retrievedCategory = categoryRepository.findByIdAndUserId(dto.getId(), UserService.getCurrentLoggedInUser().getId());

            if (retrievedCategory.isPresent()) {
                if (retrievedCategory.get().getUser().getId().equals(UserService.getCurrentLoggedInUser().getId())) {

                    retrievedCategory.get().setName(dto.getName());
                    SpCategory savedCategory = categoryRepository.save(retrievedCategory.get());

                    returnDao.setObject(new CategoryDto(savedCategory, false));
                } else {
                    errors.add("Cannot edit category of another user");
                }
            } else {
                errors.add("Category does not exist");
            }
        }

        if (!errors.isEmpty()) {
            returnDao.setErrors(errors);
        }

        return returnDao;
    }

    public GenericDao<Boolean> deleteCategory(Long catId) {
        List<String> errors = new ArrayList<>();
        Optional<SpCategory> retrievedCategory = categoryRepository.findByIdAndUserId(catId, UserService.getCurrentLoggedInUser().getId());
        if (retrievedCategory.isPresent()) {
            if (retrievedCategory.get().getUser().getId().equals(UserService.getCurrentLoggedInUser().getId())) {
                categoryRepository.deleteById(catId);
            } else {
                errors.add("Cannot delete category of another user");
            }
            return errors.isEmpty() ?
                    new GenericDao<>(true, errors) :
                    new GenericDao<>(false, errors);
        } else {
            errors.add("Category does not exist");
            return new GenericDao<>(false, errors);
        }
    }
}
