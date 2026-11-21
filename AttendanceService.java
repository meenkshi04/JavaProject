
package com.java.attendance;

import java.io.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class AttendanceService {

    private static final Path FOLDER = Paths.get("data");

    private static final Path STUDENT_FILE =
            FOLDER.resolve("students.txt");

    private static final Path REPORT_FILE =
            FOLDER.resolve("report.txt");

    public AttendanceService(FileManager fm) throws IOException {

        Files.createDirectories(FOLDER);

        if (!Files.exists(STUDENT_FILE)) {
            Files.createFile(STUDENT_FILE);
        }
    }

    // Load Students from File
    public List<Student> loadStudents() throws IOException {

        List<Student> students = new ArrayList<>();

        Set<Integer> ids = new HashSet<>();
        Set<String> rolls = new HashSet<>();

        try (BufferedReader br = Files.newBufferedReader(
                STUDENT_FILE, StandardCharsets.UTF_8)) {

            String line;
            int lineNumber = 0;

            while ((line = br.readLine()) != null) {

                lineNumber++;

                try {

                    String[] fields = line.split("\\|", -1);

                    if (fields.length != 5) {
                        throw new IllegalArgumentException();
                    }

                    int id = Integer.parseInt(fields[0]);
                    String name = fields[1];
                    String roll = fields[2];
                    String department = fields[3];

                    String[] values = fields[4].split(",", -1);

                    if (id <= 0 || name.trim().isEmpty()
                            || roll.trim().isEmpty()
                            || department.trim().isEmpty()
                            || values.length != 5) {

                        throw new IllegalArgumentException();
                    }

                    if (name.contains("|") || roll.contains("|")
                            || department.contains("|")) {

                        throw new IllegalArgumentException();
                    }

                    int[] attendance = new int[5];

                    for (int i = 0; i < 5; i++) {

                        attendance[i] = Integer.parseInt(values[i]);

                        if (attendance[i] < 0 || attendance[i] > 2) {
                            throw new IllegalArgumentException();
                        }
                    }

                    String key = roll.trim().toUpperCase(Locale.ROOT);

                    if (!ids.add(id) || !rolls.add(key)) {
                        throw new IllegalArgumentException();
                    }

                    students.add(new Student(
                            id, name, key, department, attendance
                    ));

                } catch (RuntimeException e) {

                    throw new IOException(
                            "Invalid students.txt at line " + lineNumber
                    );
                }
            }
        }

        return students;
    }

    // Save Students
    public void saveStudents(List<Student> students)
            throws IOException {

        Path temp = FOLDER.resolve("students.tmp");

        try {

            try (BufferedWriter bw = Files.newBufferedWriter(
                    temp, StandardCharsets.UTF_8)) {

                for (Student s : students) {

                    StringBuilder values = new StringBuilder();

                    int[] attendance = s.getAttendance();

                    for (int i = 0; i < 5; i++) {

                        if (i > 0) {
                            values.append(",");
                        }

                        values.append(attendance[i]);
                    }

                    bw.write(s.getId() + "|" + s.getName()
                            + "|" + s.getRollNumber()
                            + "|" + s.getDepartment()
                            + "|" + values);

                    bw.newLine();
                }
            }

            try {

                Files.move(temp, STUDENT_FILE,
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);

            } catch (AtomicMoveNotSupportedException e) {

                Files.move(temp, STUDENT_FILE,
                        StandardCopyOption.REPLACE_EXISTING);
            }

        } catch (IOException e) {

            Files.deleteIfExists(temp);
            throw e;
        }
    }

    // Register Student
    public void registerStudent(String name, String roll,
                                String department) throws IOException {

        if (name.trim().isEmpty() || roll.trim().isEmpty()
                || department.trim().isEmpty()
                || name.contains("|") || roll.contains("|")
                || department.contains("|")) {

            throw new IllegalArgumentException("Invalid student details.");
        }

        List<Student> students = loadStudents();

        String key = roll.trim().toUpperCase(Locale.ROOT);

        for (Student s : students) {

            if (s.getRollNumber().equalsIgnoreCase(key)) {

                System.out.println("Roll Number Already Exists.");
                return;
            }
        }

        int id = 1;

        for (Student s : students) {

            if (s.getId() >= id) {
                id = s.getId() + 1;
            }
        }

        int[] attendance = new int[5];

        Student student = new Student(
                id, name.trim(), key, department.trim(), attendance
        );

        students.add(student);

        saveStudents(students);

        System.out.println("Student Registered Successfully.");
        System.out.println("Student ID: " + id);
    }

    // View Students
    public void viewStudents() throws IOException {

        List<Student> students = loadStudents();

        if (students.isEmpty()) {

            System.out.println("No Students Found.");
            return;
        }

        for (Student s : students) {

            System.out.println("--------------------------");

            System.out.println("Student ID: " + s.getId());
            System.out.println("Name: " + s.getName());
            System.out.println("Roll Number: " + s.getRollNumber());
            System.out.println("Department: " + s.getDepartment());
        }
    }

    // Find Student
    public Student findStudent(int id) throws IOException {

        List<Student> students = loadStudents();

        for (Student s : students) {

            if (s.getId() == id) {
                return s;
            }
        }

        return null;
    }

    // Mark or Correct Attendance
    public void markAttendance(int id, int day, int status)
            throws IOException {

        if (day < 1 || day > 5) {

            System.out.println("Invalid Day Number.");
            return;
        }

        if (status != 1 && status != 2) {

            System.out.println("Invalid Attendance Status.");
            return;
        }

        List<Student> students = loadStudents();

        for (Student s : students) {

            if (s.getId() == id) {

                int[] attendance = s.getAttendance();

                attendance[day - 1] = status;

                saveStudents(students);

                System.out.println("Attendance Updated Successfully.");
                return;
            }
        }

        System.out.println("Student Not Found.");
    }

    // View All Attendance
    public void showAllAttendance() throws IOException {

        List<Student> students = loadStudents();

        if (students.isEmpty()) {

            System.out.println("No Students Found.");
            return;
        }

        for (Student s : students) {

            System.out.println("\nStudent: " + s.getName());

            int[] attendance = s.getAttendance();

            for (int i = 0; i < 5; i++) {

                String status;

                if (attendance[i] == 1) {
                    status = "Present";
                } else if (attendance[i] == 2) {
                    status = "Absent";
                } else {
                    status = "Not Marked";
                }

                System.out.println("Day " + (i + 1) + ": " + status);
            }
        }
    }

    // View Student Attendance
    public void showAttendance(int id) throws IOException {

        Student student = findStudent(id);

        if (student == null) {

            System.out.println("Student Not Found.");
            return;
        }

        System.out.println("\nAttendance: " + student.getName());

        int[] attendance = student.getAttendance();

        for (int i = 0; i < 5; i++) {

            String status;

            if (attendance[i] == 1) {
                status = "Present";
            } else if (attendance[i] == 2) {
                status = "Absent";
            } else {
                status = "Not Marked";
            }

            System.out.println("Day " + (i + 1) + ": " + status);
        }
    }

    // Generate Report
    public void showReport() throws IOException {

        List<Student> students = loadStudents();

        StringBuilder report = new StringBuilder();

        report.append("\n===== ATTENDANCE REPORT =====\n");

        if (students.isEmpty()) {
            report.append("No Students Found.\n");
        }

        for (Student s : students) {

            int present = 0;
            int absent = 0;

            for (int value : s.getAttendance()) {

                if (value == 1) {
                    present++;
                } else if (value == 2) {
                    absent++;
                }
            }

            int total = present + absent;

            double percentage = total == 0
                    ? 0
                    : (present * 100.0) / total;

            report.append("\nStudent ID: ").append(s.getId());
            report.append("\nName: ").append(s.getName());
            report.append("\nPresent: ").append(present);
            report.append("\nAbsent: ").append(absent);
            report.append("\nAttendance Percentage: ")
                    .append(String.format(Locale.ROOT, "%.2f", percentage))
                    .append("%\n");
        }

        System.out.println(report);
    }

    // Save Report
    public void saveReport() throws IOException {

        List<Student> students = loadStudents();

        StringBuilder report = new StringBuilder();

        report.append("===== STUDENT ATTENDANCE REPORT =====\n");

        for (Student s : students) {

            int present = 0;
            int absent = 0;

            for (int value : s.getAttendance()) {

                if (value == 1) {
                    present++;
                } else if (value == 2) {
                    absent++;
                }
            }

            int total = present + absent;

            double percentage = total == 0
                    ? 0
                    : (present * 100.0) / total;

            report.append("\nStudent ID: ").append(s.getId());
            report.append("\nName: ").append(s.getName());
            report.append("\nRoll Number: ").append(s.getRollNumber());
            report.append("\nDepartment: ").append(s.getDepartment());
            report.append("\nPresent: ").append(present);
            report.append("\nAbsent: ").append(absent);
            report.append("\nAttendance Percentage: ")
                    .append(String.format(Locale.ROOT, "%.2f", percentage))
                    .append("%\n");
        }

        Files.write(REPORT_FILE, report.toString().getBytes(StandardCharsets.UTF_8));

        System.out.println("Report saved successfully.");
        System.out.println("File Location: " + REPORT_FILE.toAbsolutePath());
    }
}