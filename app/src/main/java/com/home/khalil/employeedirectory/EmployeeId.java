package com.home.khalil.employeedirectory;

/**
 * Created by khalil on 4/7/18.
 */

public class EmployeeId {
    public String employeeID;

    public <T extends EmployeeId> T withID(String id){
        this.employeeID=id;
        return (T) this;
    }
}
