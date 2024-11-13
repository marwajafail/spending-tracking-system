package com.example.demo.service;

import com.example.demo.dao.GenericDao;
import com.example.demo.dto.RoleDto;
import com.example.demo.model.SpRole;
import com.example.demo.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public RoleDto getById(Long roleId, Boolean details) {
        return roleRepository.findById(roleId).map(role -> new RoleDto(role, details)).orElse(null);
    }

    public RoleDto getByName(String name, Boolean details) {
        return roleRepository.findByName(name).map(role -> new RoleDto(role, details)).orElse(null);
    }

    public List<RoleDto> getAll(Boolean details) {
        return roleRepository.findAll().stream().map(role -> new RoleDto(role, details)).toList();
    }

    public GenericDao<RoleDto> createRole(RoleDto dto) {
        GenericDao<RoleDto> returnDao = new GenericDao<>();
        List<String> errors = new ArrayList<>();

        if (dto.getName() == null || dto.getName().isBlank()) {
            errors.add("Role name cannot be empty");
        }
        if (dto.getDescription() == null || dto.getDescription().isBlank()) {
            errors.add("Role description cannot be empty");
        }
        if (errors.isEmpty()) {
            Optional<SpRole> retrievedRole = roleRepository.findByName(dto.getName());

            if (retrievedRole.isEmpty()) {
                SpRole savedRole = roleRepository.save(new SpRole(dto));
                returnDao.setObject(new RoleDto(savedRole, false));
            } else {
                errors.add("Category already exists");
            }
        }
        if (!errors.isEmpty()) {
            returnDao.setErrors(errors);
        }
        return returnDao;
    }

    public GenericDao<RoleDto> editRole(RoleDto dto) {
        GenericDao<RoleDto> returnDao = new GenericDao<>();

        List<String> errors = new ArrayList<>();

        if (dto.getId() == null) {
            errors.add("Role ID cannot be empty");
        }

        if (dto.getName() == null || dto.getName().isBlank()) {
            errors.add("Role name cannot be empty");
        }

        if (dto.getDescription() == null || dto.getDescription().isBlank()) {
            errors.add("Role description cannot be empty");
        }

        if (errors.isEmpty()) {
            Optional<SpRole> retrievedRole = roleRepository.findById(dto.getId());

            if (retrievedRole.isPresent()) {
                retrievedRole.get().setName(dto.getName());
                retrievedRole.get().setDescription(dto.getDescription());

                SpRole savedRole = roleRepository.save(retrievedRole.get());

                returnDao.setObject(new RoleDto(savedRole, false));
            } else {
                errors.add("Role does not exist");
            }
        }

        if (!errors.isEmpty()) {
            returnDao.setErrors(errors);
        }

        return returnDao;
    }

    public GenericDao<Boolean> deleteRole(Long roleId) {
        Optional<SpRole> retrievedRole = roleRepository.findById(roleId);
        List<String> errors = new ArrayList<>();

        if (retrievedRole.isPresent()) {
            roleRepository.deleteById(roleId);
            return new GenericDao<>(true, errors);
        } else {
            errors.add("Role does not exist");
            return new GenericDao<>(false, errors);
        }
    }
}
