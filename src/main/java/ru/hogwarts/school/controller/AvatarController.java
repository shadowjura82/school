package ru.hogwarts.school.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.service.AvatarService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

@RestController
@RequestMapping(path = "avatar")
public class AvatarController {
    private final AvatarService avatarService;

    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @PostMapping(path = "add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> addAvatar(@RequestParam Long studentId, @RequestParam MultipartFile file) throws IOException {
        avatarService.addAvatar(studentId, file);
        return ResponseEntity.ok("Profile picture has been added successfully");
    }

    @GetMapping(path = "get")
    public ResponseEntity<byte[]> getAvatar(@RequestParam Long studentID) {
        Avatar avatar = avatarService.getAvatar(studentID);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(avatar.getMediaType()));
        headers.setContentLength(avatar.getData().length);
        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(avatar.getData());
    }

    @GetMapping(path = "getFromFolder")
    public void getAvatarFromFolder(@RequestParam Long studentID, HttpServletResponse response) throws IOException {
        Avatar avatar = avatarService.getAvatar(studentID);
        Path path = Path.of(avatar.getFilePath());
        try (InputStream inputStream = Files.newInputStream(path);
             OutputStream outputStream = response.getOutputStream()) {
            response.setStatus(200);
            response.setContentType(avatar.getMediaType());
            response.setContentLength((int) avatar.getFileSize());
            inputStream.transferTo(outputStream);
        }
    }

    @GetMapping(path = "get-page")
    public ResponseEntity<Collection<Avatar>> findAllWithPage(
            @RequestParam(required = false) int page,
            @RequestParam(required = false) int size) {
        return ResponseEntity.ok(avatarService.findAllWithPage(page, size));
    }
}