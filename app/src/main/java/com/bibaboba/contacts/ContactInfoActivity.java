package com.bibaboba.contacts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.app.ActionBar;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


import java.io.Serializable;
import java.util.ArrayList;

public class ContactInfoActivity extends AppCompatActivity {

    Contact CurrentContact;

    TextView nameTextView;
    TextView lastNameTextView;
    TextView middleNameTextView;
    TextView phoneNumberTextView;
    TextView homePhoneTextView;
    TextView addressTextView;
    TextView statusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_contact_info);
        Toolbar toolbar = findViewById(R.id.contact_toolbar);
        // заголовок для активити
        toolbar.setTitle("Контакт");
        // показываем тулбар
        setSupportActionBar(toolbar);
        // показываем кнопку возврата
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // получаем данные с MainActivity
        Intent intent = getIntent();
        CurrentContact = (Contact) intent.getSerializableExtra("contact");

        // берем с экрана все контейнеры
        nameTextView = findViewById(R.id.name_text_view);
        lastNameTextView = findViewById(R.id.lastname_text_view);
        middleNameTextView = findViewById(R.id.middlename_text_view);
        phoneNumberTextView = findViewById(R.id.phone_text_view);
        homePhoneTextView = findViewById(R.id.home_phone_textView);
        addressTextView = findViewById(R.id.address_textView);
        statusTextView = findViewById(R.id.status_textView);

        SetInfoInContainers();

    }

    // выводим информацию в контейнеры
    private void SetInfoInContainers(){
        // устанавливаем в них текст
        nameTextView.setText(CurrentContact.getName());
        lastNameTextView.setText(CurrentContact.getLastName());
        middleNameTextView.setText(CurrentContact.getMiddleName());
        phoneNumberTextView.setText(CurrentContact.getPhoneNumber());
        homePhoneTextView.setText(CurrentContact.getHomePhoneNumber());
        addressTextView.setText(CurrentContact.getAddress());
        statusTextView.setText(CurrentContact.getStatus());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contact_menu, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // загружаем данные контакта (работает при возврате с активити редактирования)
        Contacts contacts = new Contacts(getContentResolver());
        CurrentContact = contacts.GetContact(CurrentContact.getId());
        SetInfoInContainers();
    }

    // нажатия на ддействия в тулбаре
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        // переходим на страницу редактирования контакта
        if (id == R.id.edit) {
            Intent intent = new Intent(ContactInfoActivity.this, EditContactActivity.class);
            intent.putExtra("contact", CurrentContact);
            startActivity(intent);
        }
        // удаляем контакт
        else if (id == R.id.delete) {
            try {
                Contacts contacts = new Contacts(getContentResolver());
                contacts.DeleteContact(CurrentContact.getId());
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (OperationApplicationException e) {
                e.printStackTrace();
            }
            finish();
            Toast.makeText(getApplicationContext(), CurrentContact.getName() + " удален из списка контактов", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }



}