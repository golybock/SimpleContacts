package com.bibaboba.contacts;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bibaboba.contacts.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    // список контактов
    private ArrayList<Contact> contacts = new ArrayList<>();
    // адаптер для контактов
    private ContactsAdapter contactsAdapter;
    // запрос на доступ к контактам
    private static final int REQUEST_CODE_READ_CONTACTS = 1;
    private static boolean READ_CONTACTS_GRANTED = false;
    // настройки приложения
    private SharedPreferences settings;
    // параметры сортировки и фильтрации(берем из настроек)
    private String SortBy;
    private String SortByName;
    private String Filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // выводим toolbar
        setSupportActionBar(binding.toolbar);

        // получаем разрешения
        int hasReadContactPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        // если устройство до API 23, устанавливаем разрешение
        if(hasReadContactPermission == PackageManager.PERMISSION_GRANTED){
            READ_CONTACTS_GRANTED = true;
        }
        else{
            // вызываем диалоговое окно для установки разрешений
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS}, REQUEST_CODE_READ_CONTACTS);
        }
        // если разрешение установлено, загружаем контакты
        if (READ_CONTACTS_GRANTED){
            loadContacts();
        }

        // нажатие на кнопку добавления
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoToAddContact();
            }
        });

        // поиск и его работа
        SearchView searchView = findViewById(R.id.searchview_contact);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // подтверждение ввода текста
            @Override
            public boolean onQueryTextSubmit(String query) {
                // ничего не делаем
                return false;
            }
            // введенный текст изменился
            @Override
            public boolean onQueryTextChange(String newText) {
                // фильтрация
                if(TextUtils.isEmpty(searchView.getQuery().toString())){
                    loadContacts();
                }
                filterContacts(searchView.getQuery().toString());
                return false;
            }
        });

    }

    // переход к активити добавления контакта
    private void GoToAddContact(){
        Intent intent = new Intent(this, AddContactActivity.class);
        startActivity(intent);
    }

    // загружаем контакты при возвращении на активити
    @Override
    protected void onRestart() {
        super.onRestart();
        loadContacts();
    }

    // загружаем контакты при возвращении на активити
    @Override
    protected void onResume() {
        super.onResume();
        loadContacts();
    }

    // задаем элементы для настроек тулбара при его инициализации
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // выбран элемент в настрйоках
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        // переход к настройкам приложения
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void initAdapter(ArrayList<Contact> contacts) {
        contactsAdapter = new ContactsAdapter(this);

        contactsAdapter.setContacts(contacts);
        RecyclerView recyclerView = findViewById(R.id.contacts_recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(contactsAdapter);
    }

    private void loadContacts(){
        contacts.clear();

        // получаем из настроек приложения настройки сортировки и фильтрации
        settings = getSharedPreferences("settings", MODE_PRIVATE);
        SortBy = settings.getString("sort_by", "by_date");
        SortByName = settings.getString("sort_by_name_lastname", "last_name");
        Filter = settings.getString("filter", "По умолчанию");

        Contacts contactsClass = new Contacts(getContentResolver());

        // получаем список контактов (надо оптимизировать и получать не во всех сценариях)
        contacts = contactsClass.GetContacts();

        // сортировка по имени
        if(Objects.equals(SortByName, "name")){
            Collections.sort(contacts, new Comparator<Contact>() {
                @Override
                public int compare(Contact o1, Contact o2) {
                    if(o1.getName() == null){
                        o1.setName("(Нет имени)");
                    }
                    if(o2.getName() == null){
                        o2.setName("(Нет имени)");
                    }
                    return o1.getName().compareTo(o2.getName());
                }
            });
        }

        // сортировка по фамилии
        else if(Objects.equals(SortByName, "last_name")){
            Collections.sort(contacts, new Comparator<Contact>() {
                @Override
                public int compare(Contact o1, Contact o2) {
                    if(o1.getLastName() == null){
                        o1.setLastName("(Нет имени)");
                    }
                    if(o2.getLastName() == null){
                        o2.setLastName("(Нет имени)");
                    }
                    return o1.getLastName().compareTo(o2.getLastName());
                }
            });
        }

        // сортировка по статусу
        else if(Objects.equals(SortByName, "status")) {
            Collections.sort(contacts, new Comparator<Contact>() {
                @Override
                public int compare(Contact o1, Contact o2) {
                    if (o1.getStatus() == null) {
                        o1.setStatus("(Нет группы)");
                    }
                    if (o2.getStatus() == null) {
                        o2.setStatus("(Нет группы)");
                    }
                    return o1.getStatus().compareTo(o2.getStatus());
                }
            });
        }


        // сортировка от большего к меньшему
        if(Objects.equals(SortBy, "by_to_lower")){
            Collections.reverse(contacts);
        }

        // фильтрация по статусу
        if(!Filter.equals("По умолчанию")){
            contacts = contacts.stream().filter(c -> Objects.equals(c.getStatus(), Filter)) .collect(Collectors
                    .toCollection(ArrayList::new));
        }

        // создаем адаптер
        initAdapter(contacts);

        // отображаем список контактов с помощью layout
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    // фильтрация контактов (надо оптимизирвоать)
    private void filterContacts(String filter){
        ArrayList<Contact> newContacts = new ArrayList<>();
        for (Contact contact: contacts
             ) {
            try {
                if(contact.getDisplayName().toLowerCase(Locale.ROOT).contains(filter)
                        || contact.getHomePhoneNumber().toLowerCase(Locale.ROOT).contains(filter)
                        || contact.getPhoneNumber().toLowerCase(Locale.ROOT).contains(filter)
                ){
                    newContacts.add(contact);
                }
            }
            catch (Exception ex){
                continue;
            }
        }
        contactsAdapter = new ContactsAdapter(this);
        initAdapter(newContacts);
    }


}