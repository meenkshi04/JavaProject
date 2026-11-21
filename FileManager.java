package com.java.attendance;

import java.io.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class FileManager {

    private static final Path FOLDER = Paths.get("data");
    private static final Path STUDENT_FILE =
            FOLDER.resolve("students.txt");

    private static final Path REPORT_FILE =
            FOLDER.resolve("report.txt");

    public FileManager() throws IOException {
        Files.createDirectories(FOLDER);

        if (!Files.exists(STUDENT_FILE)) {
            Files.createFile(STUDENT_FILE);
        }
    }

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

                    if (id <= 0 || name.isBlank()
                            || roll.isBlank() || department.isBlank()
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

                    students.add(new Student(id, name, key,
                            department, attendance));

                } catch (RuntimeException e) {
                    throw new IOException(
                        "Invalid students.txt at line " + lineNumber);
                }
            }
        }

        return students;
    }

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

    public void saveReport(String report) throws IOException {

        Files.writeString(REPORT_FILE, report,
                StandardCharsets.UTF_8);
    }
}
