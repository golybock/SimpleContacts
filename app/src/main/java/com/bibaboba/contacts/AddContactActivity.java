package com.bibaboba.contacts;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class AddContactActivity extends AppCompatActivity {

    EditText FirstName;
    EditText LastName;
    EditText MiddleName;
    EditText Address;
    EditText StatusContact;
    EditText PhoneNumber;
    EditText HomePhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_contact);

        // получаем все поля для ввода с активити
        FirstName = findViewById(R.id.editTextContactFirstName);
        LastName = findViewById(R.id.editTextContactLastName);
        MiddleName = findViewById(R.id.editTextContactMiddleName);
        Address = findViewById(R.id.editTextContactAddress);
        StatusContact = findViewById(R.id.editTextContactStatus);
        PhoneNumber = findViewById(R.id.editTextPhoneContact);
        HomePhoneNumber = findViewById(R.id.editTextHomePhoneContact);

        // получаем кнопки
        Button CancelButton = findViewById(R.id.cancel_add_contact_button);
        Button SaveButton = findViewById(R.id.save_contact_button);

        // кнопка отмены
        CancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // кнопка сохранения
        SaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // получаем информацию с контейнеров и записываем в класс
                Contact newContact = GetInfoFromLayout();
                // добавляем контакт в бд
                onAddContact(newContact.getName(),
                        newContact.getLastName(),
                        newContact.getMiddleName(),
                        newContact.getAddress(),
                        newContact.getStatus(),
                        newContact.getPhoneNumber(),
                        newContact.getHomePhoneNumber());
                finish();
            }
        });
    }

    // при нажатии кнопки назад
    @Override
    public void onBackPressed() {
        finish();
    }

    // получаем информацию с контейнеров и записываем в новый объект класса
    public Contact GetInfoFromLayout(){

        Contact contact = new Contact();

        String firstName = FirstName.getText().toString();
        String lastName = LastName.getText().toString();
        String middleName = MiddleName.getText().toString();
        String address = Address.getText().toString();
        String status = StatusContact.getText().toString();
        String phoneNum = PhoneNumber.getText().toString();
        String homePhoneNum = HomePhoneNumber.getText().toString();

        contact.setName(firstName);
        contact.setLastName(lastName);
        contact.setMiddleName(middleName);
        contact.setAddress(address);
        contact.setStatus(status);
        contact.setPhoneNumber(phoneNum);
        contact.setHomePhoneNumber(homePhoneNum);

        return contact;
    }

    // добавление контакта
    public void onAddContact(@Nullable String FirstName, @Nullable String LastName,
                             @Nullable String MiddleName, @Nullable String Address,
                             @Nullable String Status, @Nullable String PhoneNumber,
                             @Nullable String HomePhoneNumber) {

        Contacts contacts = new Contacts(getContentResolver());

        // создаем сырой контакт и получаем его id
        long rawContactsId = contacts.CreateContact(FirstName, FirstName);

        // заполняем контакт данными

        // добавляем ФИО
        contacts.AddStructuredNameToContact(rawContactsId, FirstName, LastName, MiddleName, Status);
        // добавляем номера телефонов
        contacts.AddPhoneNumberToContact(rawContactsId, PhoneNumber, HomePhoneNumber);
        // добавляем адресс
        contacts.AddAddressToContact(rawContactsId, Address);

        // уведомление о добавлении в контакты
        Toast.makeText(getApplicationContext(), FirstName + " добавлен в список контактов", Toast.LENGTH_LONG).show();
    }

}