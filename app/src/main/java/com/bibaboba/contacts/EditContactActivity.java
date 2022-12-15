package com.bibaboba.contacts;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditContactActivity extends AppCompatActivity {

    EditText FirstName;
    EditText LastName;
    EditText MiddleName;
    EditText Address;
    EditText StatusContact;
    EditText PhoneNumber;
    EditText HomePhoneNumber;

    Contact CurrentContact;
    Contact NewContactData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);

        FirstName = findViewById(R.id.editedTextContactFirstName);
        LastName = findViewById(R.id.editedTextContactLastName);
        MiddleName = findViewById(R.id.editedTextContactMiddleName);
        Address = findViewById(R.id.editedTextContactAddress);
        StatusContact = findViewById(R.id.editedTextContactStatus);
        PhoneNumber = findViewById(R.id.editedTextPhoneContact);
        HomePhoneNumber = findViewById(R.id.editedTextHomePhoneContact);

        Button CancelButton = findViewById(R.id.cancel_edit_contact_button);
        Button SaveButton = findViewById(R.id.save_edited_contact_button);

        Toolbar toolbar = findViewById(R.id.edit_contact_toolbar);
        // заголовок для активити
        toolbar.setTitle("Редактирование");
        // показываем тулбар
        setSupportActionBar(toolbar);
        // показываем кнопку возврата
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewContactData = GetInfoFromLayout();
                onAddContact(NewContactData.getName(),
                        NewContactData.getLastName(),
                        NewContactData.getMiddleName(),
                        NewContactData.getAddress(),
                        NewContactData.getStatus(),
                        NewContactData.getPhoneNumber(),
                        NewContactData.getHomePhoneNumber());



                finish();
            }
        });

        CancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // получаем данные с MainActivity
        Intent intent = getIntent();
        CurrentContact = (Contact) intent.getSerializableExtra("contact");

        SetInfoInContainers();

    }

    private void SetInfoInContainers(){
        // устанавливаем в них текст
        FirstName.setText(CurrentContact.getName());
        LastName.setText(CurrentContact.getLastName());
        MiddleName.setText(CurrentContact.getMiddleName());
        PhoneNumber.setText(CurrentContact.getPhoneNumber());
        HomePhoneNumber.setText(CurrentContact.getHomePhoneNumber());
        Address.setText(CurrentContact.getAddress());
    }

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

    public void onAddContact(@Nullable String FirstName, @Nullable String LastName,
                             @Nullable String MiddleName, @Nullable String Address,
                             @Nullable String Status, @Nullable String PhoneNumber,
                             @Nullable String HomePhoneNumber) {

        Contacts contacts = new Contacts(getContentResolver());

        // создаем сырой контакт и получаем его id
        long rawContactsId = Long.parseLong(CurrentContact.getId());

        // заполняем контакт данными

        // добавляем ФИО
        contacts.AddStructuredNameToContact(rawContactsId, FirstName, LastName, MiddleName, Status);
        // добавляем отображаемое имя
//        contacts.AddDisplayNameToContact(rawContactsId, String.format("%s %s %s", LastName, FirstName, MiddleName));
        // добавляем номера телефонов
        contacts.AddPhoneNumberToContact(rawContactsId, PhoneNumber, HomePhoneNumber);
        // добавляем адресс
        contacts.AddAddressToContact(rawContactsId, Address);
    }

}