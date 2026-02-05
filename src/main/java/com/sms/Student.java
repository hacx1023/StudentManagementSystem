package com.sms;

import java.util.Objects;

public class Student {
    private int id;
    private String name;
    private int age;
    private String course;
    private String phone;

    public Student(int id, String name, int age, String course, String phone) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.course = course;
        this.phone = phone;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getCourse() { return course; }
    public String getPhone() { return phone; }

    public void setName(String name) { this.name = name; }
    public void setAge(int age) { this.age = age; }
    public void setCourse(String course) { this.course = course; }
    public void setPhone(String phone) { this.phone = phone; }

    // Convert to CSV row: id,name,age,course,phone
    public String toCsv() {
        return id + "," + escape(name) + "," + age + "," + escape(course) + "," + escape(phone);
    }

    public static Student fromCsv(String line) {
        // Basic CSV split with support for quoted fields
        String[] parts = CsvUtil.smartSplit(line);
        if (parts.length < 5) throw new IllegalArgumentException("Invalid CSV row: " + line);

        int id = Integer.parseInt(parts[0].trim());
        String name = unescape(parts[1]);
        int age = Integer.parseInt(parts[2].trim());
        String course = unescape(parts[3]);
        String phone = unescape(parts[4]);

        return new Student(id, name, age, course, phone);
    }

    private static String escape(String s) {
        if (s == null) return "\"\"";
        String v = s.replace("\"", "\"\"");
        return "\"" + v + "\"";
    }

    private static String unescape(String s) {
        if (s == null) return "";
        String t = s.trim();
        if (t.startsWith("\"") && t.endsWith("\"") && t.length() >= 2) {
            t = t.substring(1, t.length() - 1);
        }
        return t.replace("\"\"", "\"");
    }

    @Override
    public String toString() {
        return String.format("ID: %d | Name: %s | Age: %d | Course: %s | Phone: %s",
                id, name, age, course, phone);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;
        Student student = (Student) o;
        return id == student.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

/**
 * Small CSV helper for splitting a line that may contain quoted commas.
 * Example: 1,"A,B",20,"Java","999"
 */
class CsvUtil {
    public static String[] smartSplit(String line) {
        if (line == null) return new String[0];
        java.util.List<String> out = new java.util.ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                // handle escaped quotes ""
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    cur.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                out.add(cur.toString());
                cur.setLength(0);
            } else {
                cur.append(c);
            }
        }
        out.add(cur.toString());
        return out.toArray(new String[0]);
    }
}
