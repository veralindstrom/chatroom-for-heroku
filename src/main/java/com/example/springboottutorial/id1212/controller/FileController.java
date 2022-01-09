package com.example.springboottutorial.id1212.controller;

import com.example.springboottutorial.id1212.entities.bridges.ChatroomCategory.ChatroomCategoryRepository;
import com.example.springboottutorial.id1212.entities.bridges.ChatroomUser.ChatroomUserRepository;
import com.example.springboottutorial.id1212.entities.bridges.DBFileUser.DBFileUser;
import com.example.springboottutorial.id1212.entities.bridges.DBFileUser.DBFileUserRepository;
import com.example.springboottutorial.id1212.entities.category.CategoryRepository;
import com.example.springboottutorial.id1212.entities.chat.ChatroomRepository;
import com.example.springboottutorial.id1212.entities.chat.MessageRepository;
import com.example.springboottutorial.id1212.entities.chat.RoleRepository;
import com.example.springboottutorial.id1212.entities.file.DBFile;
import com.example.springboottutorial.id1212.entities.file.DBFileRepository;
import com.example.springboottutorial.id1212.entities.user.User;
import com.example.springboottutorial.id1212.entities.user.UserRepository;
import com.example.springboottutorial.id1212.file.UploadFileResponse;
import com.example.springboottutorial.id1212.filestorage.DBFileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;

@RestController
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private DBFileStorageService dbFileStorageService;
    @Autowired
    private DBFileRepository dbFileRepository;
    @Autowired
    private DBFileUserRepository dbFileUserRepository;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/uploadFile")
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("userId") Integer userId) {
        DBFile dbFile = dbFileStorageService.storeFile(file);
        /*User user = userRepository.findUserByUserId(userId);
        DBFileUser dbFileUser = new DBFileUser();
        System.out.println("FILE ID: " + dbFile.getId());
        dbFileUser.setFileId(dbFile.getId());
        dbFileUser.setUserId(user.getUserId());
        dbFileUserRepository.save(dbFileUser);*/

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile/")
                .path(dbFile.getId())
                .toUriString();

        return new UploadFileResponse(dbFile.getFileName(), fileDownloadUri, dbFile.getId(),
                file.getContentType(), file.getSize());
    }

    @GetMapping("/downloadFile/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileId) {
        // Load file from database
        DBFile dbFile = dbFileStorageService.getFile(fileId);
        if(dbFile == null)
            dbFile = dbFileRepository.findDBFileByFileId(fileId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(dbFile.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + dbFile.getFileName() + "\"")
                .body(new ByteArrayResource(dbFile.getData()));
    }

}