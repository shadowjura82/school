package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.FiveLastStudents;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repositories.StudentRepository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class StudentService {
    private final StudentRepository studentRepository;
    private final Logger logger = LoggerFactory.getLogger(StudentService.class);

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
        logger.debug("Constructor has been invoked successfully. StudentService bin has been created in Spring context");
    }

    public Student createStudent(Student student) {
        logger.info("Method createStudent has been invoked successfully");
        return studentRepository.save(student);
    }

    public Student readStudent(Long id) {
        logger.info("Method readStudent has been invoked successfully");
        return studentRepository.findById(id).orElse(null);
    }

    public Student updateStudent(Student student) {
        logger.info("Method updateStudent has been invoked successfully");
        Student upfatedStudent = readStudent(student.getId());
        if (upfatedStudent == null) {
            logger.error("student with ID " + student.getId() + " was not found");
            return null;
        }
        return studentRepository.save(student);
    }

    public Student deleteStudent(Long id) {
        logger.info("Method deleteStudent has been invoked successfully");
        Student student = readStudent(id);
        if (student == null) {
            logger.error("student with ID " + id + " was not found");
            return null;
        }
        studentRepository.delete(student);
        return student;
    }

    public Collection<Student> filterByAge(int age) {
        logger.info("Method filterByAge has been invoked successfully");
        return studentRepository.findByAge(age);
    }

    public Collection<Student> printAll() {
        logger.info("Method printAll has been invoked successfully");
        return studentRepository.findAll();
    }

    public Collection<Student> findByAgeBetween(Integer startAge, Integer endAge) {
        logger.info("Method findByAgeBetween has been invoked successfully");
        return studentRepository.findByAgeBetween(startAge, endAge);
    }

    public Faculty getFaculty(Long id) {
        logger.info("Method getFaculty has been invoked successfully");
        return studentRepository.findById(id)
                .map(Student::getFaculty)
                .orElse(null);
    }

    public Integer getStudentsAmount() {
        logger.info("Method getStudentsAmount has been invoked successfully");
        return studentRepository.getStudentsAmount();
    }

    public Double getAverageAge() {
        logger.info("Method getAverageAge has been invoked successfully");
        return studentRepository.getAverageAge();
    }

    public List<FiveLastStudents> getFiveLastStudents() {
        logger.info("Method getFiveLastStudents has been invoked successfully");
        return studentRepository.getFiveLastStudents();
    }

    public List<String> findByNameSorted() {
        return studentRepository.findAll().parallelStream()
                .map(e -> e.getName().toUpperCase())
                .filter(e -> e.startsWith("A"))
                .sorted()
                .collect(Collectors.toList());
    }

    public Double findAverageAge() {
        return studentRepository.findAll().parallelStream()
                .mapToInt(Student::getAge)
                .average().orElseThrow();
    }

    public Integer getIntNumber() {
        return Stream.iterate(1, a -> a + 1)
                .limit(1_000_000)
                .reduce(0, Integer::sum);
    }

    public void printStudentsToConsole() {
        List<Student> students = studentRepository.findAll();

        System.out.println(students.get(0).getName());
        System.out.println(students.get(1).getName());

        new Thread(() -> {
            System.out.println(students.get(2).getName());
            System.out.println(students.get(3).getName());
        }).start();

        new Thread(() -> {
            System.out.println(students.get(4).getName());
            System.out.println(students.get(5).getName());
        }).start();
    }

    public void printStudentsToConsoleSynchronized() {
        List<Student> students = studentRepository.findAll();

        printToConsole(students, 0);
        printToConsole(students, 1);

        new Thread(() -> {
            printToConsole(students, 2);
            printToConsole(students, 3);
        }).start();

        new Thread(() -> {
            printToConsole(students, 4);
            printToConsole(students, 5);
        }).start();
    }

    private synchronized void printToConsole(List<Student> students, Integer index) {
        System.out.println(students.get(index).getName());
    }
}