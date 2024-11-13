package com.example.demo.controller;

import com.example.demo.dao.GenericDao;
import com.example.demo.dto.RoleDto;
import com.example.demo.model.SpLog;
import com.example.demo.service.LogService;
import com.example.demo.service.RoleService;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@Tag(name = "Role Controller", description = "Manage roles")
public class RoleController {
    private final RoleService roleService;
    private final LogService logService;
    private final UserService userService;

    public RoleController(RoleService roleService, LogService logService, UserService userService) {
        this.roleService = roleService;
        this.logService = logService;
        this.userService = userService;
    }

    @PostMapping
    @Operation(summary = "Create a new role", description = "Create a new role record in the system")
    public ResponseEntity<GenericDao<RoleDto>> addRole(@RequestBody RoleDto dto) {
        try {
            if (UserService.getCurrentLoggedInUser().getRole().getName().equalsIgnoreCase("Admin")) {
                GenericDao<RoleDto> genericDao = roleService.createRole(dto);
                return genericDao.getErrors().isEmpty() ?
                        new ResponseEntity<>(genericDao, HttpStatus.CREATED) :
                        new ResponseEntity<>(genericDao, HttpStatus.BAD_REQUEST);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            logService.createLog(new SpLog(null, e.getMessage(), UserService.getCurrentLoggedInUser().getId()));
            System.out.println("Exception caught and log saved.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping
    @Operation(summary = "Update an existing role", description = "Modify the details of an existing role record")
    public ResponseEntity<GenericDao<RoleDto>> editRole(@RequestBody RoleDto dto) {
        try {
            if (UserService.getCurrentLoggedInUser().getRole().getName().equalsIgnoreCase("Admin")) {
                GenericDao<RoleDto> genericDao = roleService.editRole(dto);
                return genericDao.getErrors().isEmpty() ?
                        new ResponseEntity<>(genericDao, HttpStatus.OK) :
                        new ResponseEntity<>(genericDao, HttpStatus.BAD_REQUEST);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            logService.createLog(new SpLog(null, e.getMessage(), UserService.getCurrentLoggedInUser().getId()));
            System.out.println("Exception caught and log saved.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{roleId}")
    @Operation(summary = "Delete a role", description = "Remove a role record from the system")
    public ResponseEntity<GenericDao<Boolean>> deleteRole(@PathVariable(value = "roleId") Long roleId) {
        try {
            if (UserService.getCurrentLoggedInUser().getRole().getName().equalsIgnoreCase("Admin")) {
                GenericDao<Boolean> genericDao = roleService.deleteRole(roleId);
                return genericDao.getErrors().isEmpty() ?
                        new ResponseEntity<>(genericDao, HttpStatus.OK) :
                        new ResponseEntity<>(genericDao, HttpStatus.BAD_REQUEST);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            logService.createLog(new SpLog(null, e.getMessage(), UserService.getCurrentLoggedInUser().getId()));
            System.out.println("Exception caught and log saved.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{roleId}")
    @Operation(summary = "Fetch a specific role", description = "Retrieve a role by its unique identifier")
    public ResponseEntity<GenericDao<RoleDto>> getRoleById(@PathVariable(value = "roleId") Long roleId, @RequestParam(value = "details", defaultValue = "false", required = false) Boolean details) {
        try {
            if (UserService.getCurrentLoggedInUser().getRole().getName().equalsIgnoreCase("Admin")) {
                RoleDto dto = roleService.getById(roleId, details);
                return dto != null ?
                        new ResponseEntity<>(new GenericDao<>(dto, null), HttpStatus.OK) :
                        new ResponseEntity<>(new GenericDao<>(null, List.of("Role with the id " + roleId + " not found")), HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            logService.createLog(new SpLog(null, e.getMessage(), UserService.getCurrentLoggedInUser().getId()));
            System.out.println("Exception caught and log saved.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{roleName}")
    @Operation(summary = "Fetch a specific role", description = "Retrieve a role by the role name")
    public ResponseEntity<GenericDao<RoleDto>> getRoleByName(@PathVariable(value = "roleName") String roleName, @RequestParam(value = "details", defaultValue = "false", required = false) Boolean details) {
        try {
            if (UserService.getCurrentLoggedInUser().getRole().getName().equalsIgnoreCase("Admin")) {
                RoleDto dto = roleService.getByName(roleName, details);
                return dto != null ?
                        new ResponseEntity<>(new GenericDao<>(dto, null), HttpStatus.OK) :
                        new ResponseEntity<>(new GenericDao<>(null, List.of("Role with the name " + roleName + " not found")), HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            logService.createLog(new SpLog(null, e.getMessage(), UserService.getCurrentLoggedInUser().getId()));
            System.out.println("Exception caught and log saved.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    @Operation(summary = "Fetch all roles", description = "Retrieve a list of all roles in the system")
    public ResponseEntity<GenericDao<List<RoleDto>>> getAllRoles(@RequestParam(value = "details", defaultValue = "false", required = false) Boolean details) {
        try {
            if (UserService.getCurrentLoggedInUser().getRole().getName().equalsIgnoreCase("Admin")) {
                List<RoleDto> dtos = roleService.getAll(details);
                return !dtos.isEmpty() ?
                        new ResponseEntity<>(new GenericDao<>(dtos, null), HttpStatus.OK) :
                        new ResponseEntity<>(new GenericDao<>(null, List.of("No roles found")), HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            logService.createLog(new SpLog(null, e.getMessage(), UserService.getCurrentLoggedInUser().getId()));
            System.out.println("Exception caught and log saved.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
