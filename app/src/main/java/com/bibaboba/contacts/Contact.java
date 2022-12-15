package com.bibaboba.contacts;

import android.provider.ContactsContract;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;


// модель данных для контакта
public class Contact implements Serializable {

    private String Id;
    private String DisplayName;
    private String Name;
    private String LastName;
    private String MiddleName;
    private String PhoneNumber;
    private String HomePhoneNumber;
    private String Address;
    private String Status;
    private String Group;
    private ArrayList<PhoneNumber> phoneNumbers;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getMiddleName() {
        return MiddleName;
    }

    public void setMiddleName(String middleName) {
        MiddleName = middleName;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        if (phoneNumber == null){
            PhoneNumber = "-";
        }
        else{
            PhoneNumber = phoneNumber;
        }

    }

    public String getHomePhoneNumber() {
        return HomePhoneNumber;
    }

    public void setHomePhoneNumber(String homePhoneNumber) {
        HomePhoneNumber = homePhoneNumber;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public ArrayList<com.bibaboba.contacts.PhoneNumber> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(ArrayList<com.bibaboba.contacts.PhoneNumber> phoneNumbers) {
        if (phoneNumbers.isEmpty()){
            PhoneNumber = "(Нет номера)";
            HomePhoneNumber = "(Нет номера)";
        }
        else{
            for (PhoneNumber phone: phoneNumbers
            ) {
                if(phone.getType() == ContactsContract.CommonDataKinds.Phone.TYPE_HOME){
                    HomePhoneNumber = phone.getNumber();
                }
                else{
                    PhoneNumber = phone.getNumber();
                }
            }
            this.phoneNumbers = phoneNumbers;
        }

    }

    public String getGroup() {
        return Group;
    }

    public void setGroup(String group) {
        Group = group;
    }

    public String getDisplayName() {
        return DisplayName;
    }

    public void setDisplayName(String displayName) {
        DisplayName = displayName;
    }
}
