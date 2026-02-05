package com.sms;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class StudentRepository {

    private final List<Student> students = new ArrayList<>();
    private final Path filePath;

    public StudentRepository(String fileName) {
        this.filePath = Paths.get(fileName);
        load();
    }

    public List<Student> getAll() {
        // Return a copy so caller can’t modify internal list
        return new ArrayList<>(students);
    }

    public Student findById(int id) {
        for (Student s : students) {
            if (s.getId() == id) return s;
        }
        return null;
    }

    public List<Student> searchByName(String keyword) {
        String k = keyword.toLowerCase(Locale.ROOT).trim();
        List<Student> result = new ArrayList<>();
        for (Student s : students) {
            if (s.getName().toLowerCase(Locale.ROOT).contains(k)) {
                result.add(s);
            }
        }
        return result;
    }

    public boolean add(Student s) {
        if (findById(s.getId()) != null) return false;
        students.add(s);
        save();
        return true;
    }

    public boolean update(int id, String name, Integer age, String course, String phone) {
        Student s = findById(id);
        if (s == null) return false;

        if (name != null && !name.isBlank()) s.setName(name.trim());
        if (age != null) s.setAge(age);
        if (course != null && !course.isBlank()) s.setCourse(course.trim());
        if (phone != null && !phone.isBlank()) s.setPhone(phone.trim());

        save();
        return true;
    }

    public boolean delete(int id) {
        Student s = findById(id);
        if (s == null) return false;
        students.remove(s);
        save();
        return true;
    }

    private void load() {
        students.clear();
        if (!Files.exists(filePath)) {
            // create file with header
            try {
                Files.writeString(filePath, "id,name,age,course,phone\n", StandardOpenOption.CREATE);
            } catch (IOException e) {
                System.out.println("Warning: Could not create data file: " + e.getMessage());
            }
            return;
        }

        try (BufferedReader br = Files.newBufferedReader(filePath)) {
            String line;
            boolean first = true;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                if (first) { // skip header
                    first = false;
                    if (line.toLowerCase(Locale.ROOT).startsWith("id,")) continue;
                }
                try {
                    students.add(Student.fromCsv(line));
                } catch (Exception ex) {
                    System.out.println("Skipping bad row: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    private void save() {
        try (BufferedWriter bw = Files.newBufferedWriter(filePath,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {

            bw.write("id,name,age,course,phone");
            bw.newLine();

            // Sort by ID for neat file
            students.sort(Comparator.comparingInt(Student::getId));

            for (Student s : students) {
                bw.write(s.toCsv());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }
}
