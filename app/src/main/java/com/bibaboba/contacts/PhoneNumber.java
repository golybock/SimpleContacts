package com.bibaboba.contacts;

import java.io.Serializable;

public class PhoneNumber implements Serializable {

    private String Number;
    private int Type;

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public String getNumber() {
        return Number;
    }

    public void setNumber(String number) {
        Number = number;
    }

    public PhoneNumber() {
    }

    public PhoneNumber(String number, int type) {
        Number = number;
        Type = type;
    }
}
