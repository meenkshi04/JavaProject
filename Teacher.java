package com.java.attendance;

public class Teacher extends User {

    public Teacher() {
        super(0, "Class Teacher");
    }

    @Override
    public void showProfile() {
        System.out.println("Teacher ID: " + getId());
        System.out.println("Teacher Name: " + getName());
    }
}
