package ru.hogwarts.school.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repositories.AvatarRepository;
import ru.hogwarts.school.repositories.StudentRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

@Service
public class AvatarService {
    private final AvatarRepository avatarRepository;
    private final StudentRepository studentRepository;
    @Value("${path.to.avatars.folder}")
    private String filePath;

    public AvatarService(AvatarRepository avatarRepository, StudentRepository studentRepository) {
        this.avatarRepository = avatarRepository;
        this.studentRepository = studentRepository;
    }

    public void addAvatar(Long studentId, MultipartFile file) throws IOException {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student with ID " + studentId + " hasn't been found in database"));
        Path path = Path.of(filePath, studentId + getExtension(file.getOriginalFilename()));
        if (Files.notExists(path.getParent())) Files.createDirectory(path.getParent());
//        Files.deleteIfExists(path);
//        try (
//                InputStream inputStream = file.getInputStream();
//                OutputStream outputStream = Files.newOutputStream(path, CREATE_NEW);
//                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream, 1024);
//                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream, 1024);
//        ) {
//            bufferedInputStream.transferTo(bufferedOutputStream);
//        }
        file.transferTo(path);
        Avatar avatar = findAvatar(studentId);
        avatar.setStudent(student);
        avatar.setData(file.getBytes());
        avatar.setMediaType(file.getContentType());
        avatar.setFileSize(file.getSize());
        avatar.setFilePath(path.toString());
        avatarRepository.save(avatar);
    }

    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf('.'));
    }

    private Avatar findAvatar(Long studentId) {
        Avatar avatar = avatarRepository.findByStudentId(studentId);
        if (avatar == null) {
            avatar = new Avatar();
        }
        return avatar;
    }

    public Avatar getAvatar(Long studentId) {
        return avatarRepository.findByStudentId(studentId);
    }

    public Collection<Avatar> findAllWithPage(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<Avatar> pageAvatar = avatarRepository.findAll(pageRequest);
        return pageAvatar.getContent();
    }
}