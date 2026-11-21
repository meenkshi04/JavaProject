package com.java.attendance;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    static Scanner sc = new Scanner(System.in);

    static AttendanceService service;

    static Teacher teacher = new Teacher();

    public static void main(String[] args) {

        try {

            FileManager fm = new FileManager();
            service = new AttendanceService(fm);

            int choice;

            do {

                System.out.println("\n==============================");
                System.out.println(" STUDENT ATTENDANCE SYSTEM");
                System.out.println("==============================");

                System.out.println("1. Teacher Menu");
                System.out.println("2. Student Menu");
                System.out.println("0. Exit");

                choice = readInt("Enter your choice: ");

                switch (choice) {

                    case 1:
                        showTeacherMenu();
                        break;

                    case 2:
                        showStudentMenu();
                        break;

                    case 0:
                        System.out.println("Thank you!");
                        break;

                    default:
                        System.out.println("Invalid choice.");
                }

            } while (choice != 0);

        } catch (Exception e) {
            System.out.println("Application error: " + e.getMessage());
        }
    }

    public static void showTeacherMenu() {

        int choice;

        do {

            System.out.println("\n===== TEACHER MENU =====");
            System.out.println("1. Register Student");
            System.out.println("2. View Students");
            System.out.println("3. Mark or Correct Attendance");
            System.out.println("4. View All Attendance");
            System.out.println("5. View Reports");
            System.out.println("0. Back");

            choice = readInt("Enter choice: ");

            try {

                switch (choice) {

                    case 1:

                        System.out.print("Enter Student Name: ");
                        String name = sc.nextLine();

                        System.out.print("Enter Roll Number: ");
                        String roll = sc.nextLine();

                        System.out.print("Enter Department: ");
                        String dept = sc.nextLine();

                        service.registerStudent(name, roll, dept);
                        break;

                    case 2:
                        service.viewStudents();
                        break;

                    case 3:

                        int id = readInt("Enter Student ID: ");

                        if (service.findStudent(id) == null) {
                            System.out.println("Student not found.");
                            break;
                        }

                        int day = readInt(
                                "Enter Day Number (1-5): ");

                        System.out.println("1. Present");
                        System.out.println("2. Absent");

                        int status = readInt("Enter Status: ");

                        service.markAttendance(id, day, status);
                        break;

                    case 4:
                        service.showAllAttendance();
                        break;

                    case 5:

                        service.showReport();

                        int save = readInt("Enter choice: ");

                        if (save == 1) {
                            service.saveReport();
                        } else if (save != 0) {
                            System.out.println("Invalid choice.");
                        }

                        break;

                    case 0:
                        System.out.println("Returning to Main Menu.");
                        break;

                    default:
                        System.out.println("Invalid choice.");
                }

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }

        } while (choice != 0);
    }

    public static void showStudentMenu() throws IOException {

        int id = readInt("Enter Student ID: ");

        Student student = service.findStudent(id);

        if (student == null) {
            System.out.println("Student not found.");
            return;
        }

        int choice;

        do {

            System.out.println("\n===== STUDENT MENU =====");
            System.out.println("1. View My Profile");
            System.out.println("2. View My Attendance");
            System.out.println("0. Back");

            choice = readInt("Enter choice: ");

            switch (choice) {

                case 1:
                    student.showProfile();
                    break;

                case 2:
                    service.showAttendance(id);
                    break;

                case 0:
                    System.out.println("Returning to Main Menu.");
                    break;

                default:
                    System.out.println("Invalid choice.");
            }

        } while (choice != 0);
    }

    public static int readInt(String message) {

        while (true) {

            System.out.print(message);

            String input = sc.nextLine();

            try {
                return Integer.parseInt(input.trim());

            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}