package com.java.attendance;

public class Student extends User {

    private String rollNumber;
    private String department;
    private int[] attendance;

    public static final String[] DAYS = {
        "Monday", "Tuesday", "Wednesday",
        "Thursday", "Friday"
    };

    public Student(int id, String name,
                   String rollNumber, String department,
                   int[] attendance) {

        super(id, name);
        this.rollNumber = rollNumber;
        this.department = department;
        this.attendance = attendance.clone();
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public String getDepartment() {
        return department;
    }

    public int[] getAttendance() {
        return attendance.clone();
    }

    public void markDay(int index, int status) {
        attendance[index] = status;
    }

    @Override
    public void showProfile() {
        System.out.println("Student ID: " + getId());
        System.out.println("Name: " + getName());
        System.out.println("Roll Number: " + rollNumber);
        System.out.println("Department: " + department);
    }
}
