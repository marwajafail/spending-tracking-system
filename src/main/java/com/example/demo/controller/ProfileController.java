package com.example.demo.controller;

import com.example.demo.dao.GenericDao;
import com.example.demo.dto.ProfileDto;
import com.example.demo.model.SpLog;
import com.example.demo.service.LogService;
import com.example.demo.service.ProfileService;
import com.example.demo.service.UserService;
import com.example.demo.storage.StorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/profiles")
@CrossOrigin(origins = "http://localhost:3000")

@Tag(name = "Profile Controller", description = "Manage budgets")
public class ProfileController {

    private final ProfileService profileService;
    private final StorageService storageService;
    private final LogService logService;
    private final UserService userService;

    @Autowired
    public ProfileController(ProfileService profileService, StorageService storageService, LogService logService, UserService userService) {
        this.profileService = profileService;
        this.storageService = storageService;
        this.logService = logService;
        this.userService = userService;
    }

    @PutMapping
    @Operation(summary = "Update an existing profile", description = "Modify the details of an existing profile record")
    public ResponseEntity<GenericDao<ProfileDto>> editProfile(@RequestPart("profile") ProfileDto dto,
                                                              @RequestPart(value = "file", required = false) MultipartFile file) {
        try {
            GenericDao<ProfileDto> genericDao = profileService.editProfile(dto, file);
            return genericDao.getErrors().isEmpty() ?
                    new ResponseEntity<>(genericDao, HttpStatus.OK) :
                    new ResponseEntity<>(genericDao, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logService.createLog(new SpLog(null, e.getMessage(), UserService.getCurrentLoggedInUser().getId()));
            System.out.println("Exception caught and log saved.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{profileId}")
    @Operation(summary = "Fetch a specific profile", description = "Retrieve a profile by its unique identifier")
    public ResponseEntity<GenericDao<ProfileDto>> getProfileById(@PathVariable Long profileId) {
        try {
            ProfileDto dto = profileService.getById(profileId);
            return dto != null ?
                    new ResponseEntity<>(new GenericDao<>(dto, null), HttpStatus.OK) :
                    new ResponseEntity<>(new GenericDao<>(null, List.of("Profile with the id " + profileId + " not found")), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logService.createLog(new SpLog(null, e.getMessage(), UserService.getCurrentLoggedInUser().getId()));
            System.out.println("Exception caught and log saved.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    @Operation(summary = "Fetch all profiles", description = "Retrieve a list of all profiles in the system")
    public ResponseEntity<GenericDao<List<ProfileDto>>> getAllProfiles() {
        try {
            if (UserService.getCurrentLoggedInUser().getRole().getName().equalsIgnoreCase("Admin")) {
                List<ProfileDto> dtos = profileService.getAll();
                return !dtos.isEmpty() ?
                        new ResponseEntity<>(new GenericDao<>(dtos, null), HttpStatus.OK) :
                        new ResponseEntity<>(new GenericDao<>(null, List.of("No profiles found")), HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            logService.createLog(new SpLog(null, e.getMessage(), UserService.getCurrentLoggedInUser().getId()));
            System.out.println("Exception caught and log saved.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{id}/uploadProfilePic")
    @Operation(summary = "Upload profile picture", description = "Uploads a new profile picture for the user with the specified ID")
    public ResponseEntity<?> handleProfilePicUpload(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            // Store the file and get its filename
            String filename = storageService.store(file);

            // Format the file path as 'upload-dir/filename'
            String fileUrl = "upload-dir/" + filename;

            // Update the profile with the new profile picture URL
            GenericDao<Boolean> result = profileService.updateProfilePic(id, fileUrl);

            // Check for errors and respond accordingly
            if (result.getErrors() != null && !result.getErrors().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result.getErrors());
            }

            // Return a success response
            return ResponseEntity.status(HttpStatus.OK).body("Profile picture updated successfully");
        } catch (Exception e) {
            logService.createLog(new SpLog(null, e.getMessage(), UserService.getCurrentLoggedInUser().getId()));
            System.out.println("Exception caught and log saved.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    @Operation(summary = "Serve File", description = "Serves the file with the specified filename")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            Resource file = storageService.loadAsResource(filename);
            if (file == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + file.getFilename() + "\"").body(file);
        } catch (Exception e) {
            logService.createLog(new SpLog(null, e.getMessage(), UserService.getCurrentLoggedInUser().getId()));
            System.out.println("Exception caught and log saved.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
