package ru.hogwarts.school.model;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;

@Entity(name = "faculty")
public class Faculty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;
    @OneToMany(mappedBy = "faculty", fetch = FetchType.EAGER)
    private Collection<Student> students;

    public Faculty(String name, String color, Collection<Student> students) {
        this.name = name;
        this.color = color;
        this.students = students;
    }

    public Faculty() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Collection<Student> getStudents() {
        return students;
    }

    public void setStudents(Collection<Student> students) {
        this.students = students;
//        tst
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Faculty faculty = (Faculty) o;
        return Objects.equals(id, faculty.id) && Objects.equals(name, faculty.name) && Objects.equals(color, faculty.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color);
    }

    @Override
    public String toString() {
//        return "{" + "\"color\"=\"" + color + ", id=" + id + ", \"name\"=\"" + name + "\", \"students\"=" + students + '}';
        return "{" + "id=" + id + ", name=" + name + ", color=" + color + ", students=" + students + '}';
    }
}
