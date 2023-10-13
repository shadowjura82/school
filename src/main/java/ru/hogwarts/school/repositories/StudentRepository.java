package ru.hogwarts.school.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.hogwarts.school.model.FiveLastStudents;
import ru.hogwarts.school.model.Student;

import java.util.Collection;
import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Collection<Student> findByAgeBetween(Integer startAge, Integer endAge);

    Collection<Student> findByAge(int age);

    @Query(value = "SELECT count(*) FROM student", nativeQuery = true)
    Integer getStudentsAmount();

    @Query(value = "SELECT round(avg(age),2) FROM student", nativeQuery = true)
    Double getAverageAge();

    @Query(value = "SELECT * FROM student ORDER BY id DESC LIMIT 5", nativeQuery = true)
    List<FiveLastStudents> getFiveLastStudents();
}
