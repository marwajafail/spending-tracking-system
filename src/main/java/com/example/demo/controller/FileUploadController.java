package com.example.demo.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.example.demo.model.SpLog;
import com.example.demo.service.LogService;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.example.demo.storage.StorageFileNotFoundException;
import com.example.demo.storage.StorageService;

@Controller
@RequestMapping("/files")
@CrossOrigin(origins = "http://localhost:3000")

@Tag(name = "File Management", description = "Endpoints for managing uploaded files")
public class FileUploadController {

    private final StorageService storageService;
    private final LogService logService;
    private final UserService userService;

    @Autowired
    public FileUploadController(StorageService storageService, LogService logService, UserService userService) {
        this.storageService = storageService;
        this.logService = logService;
        this.userService = userService;
    }

    @GetMapping("/")
    @Operation(summary = "List Uploaded Files", description = "Retrieves a list of all uploaded files")
    public ResponseEntity<?> listUploadedFiles() {
        try {
            List<String> files = storageService.loadAll().map(
                            path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                                    "serveFile", path.getFileName().toString()).build().toUri().toString())
                    .collect(Collectors.toList());

            return ResponseEntity.status(HttpStatus.OK).body(files);
        } catch (Exception e) {
            logService.createLog(new SpLog(null, e.getMessage(), UserService.getCurrentLoggedInUser().getId()));
            System.out.println("Exception caught and log saved.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/{filename:.+}")
    @ResponseBody
    @Operation(summary = "Serve File", description = "Downloads the file with the specified filename")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            Resource file = storageService.loadAsResource(filename);

            if (file == null)
                return ResponseEntity.notFound().build();

            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + file.getFilename() + "\"").body(file);
        } catch (Exception e) {
            logService.createLog(new SpLog(null, e.getMessage(), UserService.getCurrentLoggedInUser().getId()));
            System.out.println("Exception caught and log saved.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/")
    @Operation(summary = "Upload File", description = "Uploads a new file to the server")
    public ResponseEntity<?> handleFileUpload(@RequestParam("file") MultipartFile file) {
        try {
            storageService.store(file);
            return ResponseEntity.status(HttpStatus.OK).body("You successfully uploaded " + file.getOriginalFilename() + "!");
        } catch (Exception e) {
            logService.createLog(new SpLog(null, e.getMessage(), UserService.getCurrentLoggedInUser().getId()));
            System.out.println("Exception caught and log saved.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

}
