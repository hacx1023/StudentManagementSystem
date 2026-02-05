package com.sms;

import java.util.List;
import java.util.Scanner;

public class StudentManagementApp {

    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        StudentRepository repo = new StudentRepository("students.csv");

        while (true) {
            printMenu();
            int choice = readInt("Choose option: ");

            switch (choice) {
                case 1 -> addStudent(repo);
                case 2 -> viewAll(repo);
                case 3 -> searchById(repo);
                case 4 -> searchByName(repo);
                case 5 -> updateStudent(repo);
                case 6 -> deleteStudent(repo);
                case 0 -> {
                    System.out.println("Bye 👋");
                    return;
                }
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n==============================");
        System.out.println("   Student Management System  ");
        System.out.println("==============================");
        System.out.println("1. Add Student");
        System.out.println("2. View All Students");
        System.out.println("3. Search Student by ID");
        System.out.println("4. Search Student by Name");
        System.out.println("5. Update Student");
        System.out.println("6. Delete Student");
        System.out.println("0. Exit");
        System.out.println("------------------------------");
    }

    private static void addStudent(StudentRepository repo) {
        System.out.println("\n--- Add Student ---");
        int id = readInt("Enter ID (number): ");

        if (repo.findById(id) != null) {
            System.out.println("Student with this ID already exists.");
            return;
        }

        String name = readNonEmpty("Enter Name: ");
        int age = readIntRange("Enter Age (1-120): ", 1, 120);
        String course = readNonEmpty("Enter Course: ");
        String phone = readNonEmpty("Enter Phone: ");

        boolean ok = repo.add(new Student(id, name, age, course, phone));
        System.out.println(ok ? "✅ Student added." : "❌ Could not add student.");
    }

    private static void viewAll(StudentRepository repo) {
        System.out.println("\n--- All Students ---");
        List<Student> list = repo.getAll();
        if (list.isEmpty()) {
            System.out.println("No students found.");
            return;
        }
        for (Student s : list) System.out.println(s);
    }

    private static void searchById(StudentRepository repo) {
        System.out.println("\n--- Search by ID ---");
        int id = readInt("Enter ID: ");
        Student s = repo.findById(id);
        if (s == null) System.out.println("Not found.");
        else System.out.println(s);
    }

    private static void searchByName(StudentRepository repo) {
        System.out.println("\n--- Search by Name ---");
        System.out.print("Enter name keyword: ");
        String keyword = sc.nextLine().trim();

        List<Student> found = repo.searchByName(keyword);
        if (found.isEmpty()) {
            System.out.println("No matching students.");
            return;
        }
        for (Student s : found) System.out.println(s);
    }

    private static void updateStudent(StudentRepository repo) {
        System.out.println("\n--- Update Student ---");
        int id = readInt("Enter ID to update: ");
        Student s = repo.findById(id);
        if (s == null) {
            System.out.println("Student not found.");
            return;
        }

        System.out.println("Current: " + s);
        System.out.println("Press ENTER to skip any field.");

        System.out.print("New Name: ");
        String name = sc.nextLine();

        Integer age = null;
        System.out.print("New Age: ");
        String ageStr = sc.nextLine().trim();
        if (!ageStr.isEmpty()) {
            try {
                int a = Integer.parseInt(ageStr);
                if (a < 1 || a > 120) {
                    System.out.println("Invalid age range. Update cancelled.");
                    return;
                }
                age = a;
            } catch (NumberFormatException e) {
                System.out.println("Invalid age. Update cancelled.");
                return;
            }
        }

        System.out.print("New Course: ");
        String course = sc.nextLine();

        System.out.print("New Phone: ");
        String phone = sc.nextLine();

        boolean ok = repo.update(id, name, age, course, phone);
        System.out.println(ok ? "✅ Updated successfully." : "❌ Update failed.");
    }

    private static void deleteStudent(StudentRepository repo) {
        System.out.println("\n--- Delete Student ---");
        int id = readInt("Enter ID to delete: ");
        Student s = repo.findById(id);
        if (s == null) {
            System.out.println("Student not found.");
            return;
        }

        System.out.println("Delete: " + s);
        System.out.print("Type YES to confirm: ");
        String confirm = sc.nextLine().trim();

        if (!confirm.equalsIgnoreCase("YES")) {
            System.out.println("Cancelled.");
            return;
        }

        boolean ok = repo.delete(id);
        System.out.println(ok ? "✅ Deleted." : "❌ Delete failed.");
    }

    // ---------- Input Helpers ----------
    private static int readInt(String msg) {
        while (true) {
            System.out.print(msg);
            String in = sc.nextLine().trim();
            try {
                return Integer.parseInt(in);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static int readIntRange(String msg, int min, int max) {
        while (true) {
            int val = readInt(msg);
            if (val >= min && val <= max) return val;
            System.out.println("Value must be between " + min + " and " + max + ".");
        }
    }

    private static String readNonEmpty(String msg) {
        while (true) {
            System.out.print(msg);
            String s = sc.nextLine().trim();
            if (!s.isEmpty()) return s;
            System.out.println("This field cannot be empty.");
        }
    }
}
