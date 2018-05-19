package com.home.khalil.employeedirectory;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by khalil on 4/4/18.
 */

public class Employee extends EmployeeId {
    String department;
    ArrayList<String> directReportEmp;

    String email;
    String imageUri;
    String location;
    String manager;
    String name;
    String phoneNumber;
    String role;




    public Employee(){

    }

    public Employee(String department,ArrayList<String> directReportEmp ,String email,String imageUri, String location,String manager, String name, String phoneNumber, String role){
        this.department=department;
        this.directReportEmp=directReportEmp;
        this.email=email;
        this.imageUri=imageUri;
        this.location=location;
        this.manager=manager;
        this.name=name;
        this.phoneNumber=phoneNumber;
        this.role=role;

    }




    public String getName(){
        return name;
    }

    public String getRole(){
        return role;
    }

    public String getEmail(){
        return email;
    }

    public String getPhoneNumber(){
        return phoneNumber;
    }

    public String getDepartment(){
        return department;
    }

    public String getImageUri(){
        return imageUri;
    }

    public String getLocation(){
        return location;
    }

    public String getManager(){
        return manager;
    }

    public ArrayList<String> getDirectReportEmp(){
        return directReportEmp;
    }

    public String print(){
        return name+", "+role;
    }



    public void setName(String name) {
        this.name = name;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
