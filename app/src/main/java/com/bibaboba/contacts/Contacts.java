package com.bibaboba.contacts;

import android.annotation.SuppressLint;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.SplittableRandom;

// класс для работы с бд контактов системы
public class Contacts {

    private final ContentResolver contentResolver;
    public ArrayList<Contact> contacts = new ArrayList<>();

    public Contacts(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    // получение статусов всех контактов (без повторов)
    public ArrayList<String> GetStatuses(){
        ArrayList<String> statuses = new ArrayList<>();
        for (Contact contact: GetContacts()
             ) {
            if (contact.getStatus() != null){
                if(!statuses.contains(contact.getStatus())){
                    statuses.add(contact.getStatus());
                }
            }
        }

        return statuses;
    }

    @SuppressLint("Range")
    public ArrayList<Contact> GetContacts(){
        //Связываемся с контактными данными и берем с них значения id контакта, имени контакта и его номера:
        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        // имеет ли контакт номер
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

        @SuppressLint("Recycle") Cursor cursor = contentResolver.query(CONTENT_URI, null,null, null, null);

        // если контакты существуют
        if (cursor.getCount() > 0) {
            // обработка каждой строки бд
            while (cursor.moveToNext()) {
                // объект класса для сохранения в него данных
                Contact contact = new Contact();
                // получаем и записываем id контакта
                contact.setId(cursor.getString(cursor.getColumnIndex( _ID )));
                // имеет ли контакт номер/номера телефона
                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));
                // если номера телефона имееются
                if (hasPhoneNumber > 0) {
                    contact.setPhoneNumbers(GetPhoneNumbers(contact.getId()).getPhoneNumbers());
                }

                Contact NameData = GetStructuredNameContact(contact.getId());

                contact.setName(NameData.getName());
                contact.setLastName(NameData.getLastName());
                contact.setMiddleName(NameData.getMiddleName());
                contact.setDisplayName(NameData.getDisplayName());
                contact.setStatus(NameData.getStatus());

                contact.setAddress(GetStructuredAddress(contact.getId()));

                contacts.add(contact);
            }

        }
        return contacts;
    }

    // получаем все ячейки с именем для контакта по его id
    @SuppressLint("Range")
    private Contact GetStructuredNameContact(String id){

        // ФИО контакта
        String FAMILY_NAME = ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME;
        String MIDDLE_NAME = ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME;
        String GIVEN_NAME = ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME;
        String DISPLAY_NAME = ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME;
        String STATUS = ContactsContract.CommonDataKinds.StructuredName.PHONETIC_GIVEN_NAME;
        // для хранения данных
        Contact contact = new Contact();
        // параметры курсора
        String[] StructuredNameProjection = new String[] {FAMILY_NAME, GIVEN_NAME, MIDDLE_NAME, DISPLAY_NAME, STATUS};
        String NameWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
        String[] whereParameters = new String[]{ id, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE};
        // курсор для получения ФИО контакта
        @SuppressLint("Recycle") Cursor nameCursor =
                contentResolver.query(ContactsContract.Data.CONTENT_URI,
                        StructuredNameProjection,
                        NameWhere,
                        whereParameters,
                        null);

        while (nameCursor.moveToNext()){
            contact.setDisplayName(nameCursor.getString(nameCursor.getColumnIndex(DISPLAY_NAME)));
            contact.setName(nameCursor.getString(nameCursor.getColumnIndex(GIVEN_NAME)));
            contact.setMiddleName(nameCursor.getString(nameCursor.getColumnIndex(MIDDLE_NAME)));
            contact.setLastName(nameCursor.getString(nameCursor.getColumnIndex(FAMILY_NAME)));
            contact.setStatus(nameCursor.getString(nameCursor.getColumnIndex(STATUS)));
        }
        // закрываем курсор для фио
        nameCursor.close();

        return contact;
    }

    // получаем адрес контакта по его id
    @SuppressLint("Range")
    private String GetStructuredAddress(String id){
        String ADDRESS = ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS;
        // для хранения адреса
        String address = null;
        // параметры курсора
        String[] StructuredAddressProjection = new String[] {ADDRESS};
        String AddressWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
        String[] AddressWhereParams = new String[] { id, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE};
        // курсор для получения адресса
        @SuppressLint("Recycle") Cursor addressCursor =
                contentResolver.query(ContactsContract.Data.CONTENT_URI,
                        StructuredAddressProjection,
                        AddressWhere,
                        AddressWhereParams,
                        null);

        while (addressCursor.moveToNext()){
            address = (addressCursor.getString(addressCursor.getColumnIndex(ADDRESS)));
        }

        addressCursor.close();

        return address;
    }

    // получаем список номеров контакта по его id
    @SuppressLint("Range")
    private Contact GetPhoneNumbers(String id){
        // номера телефона контакта
        Uri PHONE_CONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
        String NUMBER_TYPE = ContactsContract.CommonDataKinds.Phone.TYPE;
        // список номеров контакта
        ArrayList<PhoneNumber> phoneNumbers = new ArrayList<>();

        // для хранения данных
        Contact contact = new Contact();

        // параметры курсора
        String[] PhoneProjection = new String[] {NUMBER, NUMBER_TYPE};
        String where = Phone_CONTACT_ID + " = ?";
        String[] whereParams = new String[] { id };

        // курсор для получения номеров телефона
        @SuppressLint("Recycle") Cursor phoneCursor =
                contentResolver.query(PHONE_CONTENT_URI,
                        PhoneProjection,
                        where,
                        whereParams,
                        null);

        //и соответствующий ему номер:
        while (phoneCursor.moveToNext()) {
            phoneNumbers.add(new PhoneNumber(phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER)), phoneCursor.getInt(phoneCursor.getColumnIndex(NUMBER_TYPE))));
        }
        // добавляем список номеров контакту
        contact.setPhoneNumbers(phoneNumbers);
        // закрываем курсор
        phoneCursor.close();

        return contact;
    }

    // получаем всю информацию о контакте по его id
    @SuppressLint("Range")
    public Contact GetContact(String id){
        Contact contact = new Contact();
        //Связываемся с контактными данными и берем с них значения id контакта, имени контакта и его номера:
        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        // имеет ли контакт номер
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

        String where = _ID + " = ?";
        String[] whereParams = new String[] { id };

        @SuppressLint("Recycle") Cursor cursor = contentResolver.query(CONTENT_URI, null,where, whereParams, null);

        // обработка каждой строки бд
        while (cursor.moveToNext()) {
            // получаем и записываем id контакта
            contact.setId(cursor.getString(cursor.getColumnIndex( _ID )));
            // имеет ли контакт номер/номера телефона
            int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));
            // если номера телефона имееются
            if (hasPhoneNumber > 0) {
                contact.setPhoneNumbers(GetPhoneNumbers(contact.getId()).getPhoneNumbers());
            }

            Contact NameData = GetStructuredNameContact(contact.getId());
            contact.setDisplayName(NameData.getDisplayName());
            contact.setName(NameData.getName());
            contact.setLastName(NameData.getLastName());
            contact.setMiddleName(NameData.getMiddleName());
            contact.setStatus(NameData.getStatus());

            contact.setAddress(GetStructuredAddress(contact.getId()));
        }
        return contact;
    }

    // удаляем контакт по его id
    public void DeleteContact(String id) throws RemoteException, OperationApplicationException {
        ArrayList<ContentProviderOperation> ops = new
                ArrayList<ContentProviderOperation>();
        String[] args = new String[] { id };
        ops.add(ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI).withSelection(ContactsContract.RawContacts.CONTACT_ID + "=?", args).build());
        contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
    }

    // создаем новый контакт (возвращает id нового сырого контакта)
    public long CreateContact(String AccountName, String AccountType){
        ContentValues values = new ContentValues();

        values.put(ContactsContract.RawContacts.ACCOUNT_NAME, AccountName);
        values.put(ContactsContract.RawContacts.ACCOUNT_TYPE, AccountType);
        Uri newUri = contentResolver.insert(ContactsContract.RawContacts.CONTENT_URI, values);

        long rawContactsId = ContentUris.parseId(newUri);

        values.clear();

        return rawContactsId;
    }

    // добавляем структурированное имя для контакта
    public void AddStructuredNameToContact(long RawContactId, String FirstName, String LastName, String MiddleName, String Status){
        ContentValues values = new ContentValues();
        /* Связываем наш аккаунт с данными */
        values.put(ContactsContract.Data.RAW_CONTACT_ID, RawContactId);
        /* Устанавливаем MIMETYPE для поля данных */
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        /* Имя для нашего аккаунта */
        values.put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, String.format("%s %s %s", LastName, FirstName, MiddleName));
        values.put(ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME, MiddleName);
        values.put(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, LastName);
        values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, FirstName);
        values.put(ContactsContract.CommonDataKinds.StructuredName.PHONETIC_GIVEN_NAME, Status);
        // сохраняем данные
        contentResolver.insert(ContactsContract.Data.CONTENT_URI, values);
    }

    // добавляем домашний и рабочий номера телефона для контакта
    public void AddPhoneNumberToContact(long RawContactId, String PhoneNumber, String HomePhoneNumber){
        ContentValues values = new ContentValues();
        /* Связываем наш аккаунт с данными */
        values.put(ContactsContract.Data.RAW_CONTACT_ID, RawContactId);
        /* Тип данных – номер телефона */
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        /* Номер телефона */
        values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, PhoneNumber);
        /* Тип – мобильный */
        values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
        // сохраянем мобильный телефон
        contentResolver.insert(ContactsContract.Data.CONTENT_URI, values);
        // очищаем данные для ввода
        values.clear();
        /* Связываем наш аккаунт с данными */
        values.put(ContactsContract.Data.RAW_CONTACT_ID, RawContactId);
        /* Тип данных – номер телефона */
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        /* Номер телефона */
        values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, HomePhoneNumber);
        /* Тип – домашний */
        values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_HOME);
        // сохраянем домашний телефон
        contentResolver.insert(ContactsContract.Data.CONTENT_URI, values);
    }

    // добавляем адрес для контакта
    public void AddAddressToContact(long RawContactId, String Address){
        ContentValues values = new ContentValues();
        /* Связываем наш аккаунт с данными */
        values.put(ContactsContract.Data.RAW_CONTACT_ID, RawContactId);
        /* Устанавливаем MIMETYPE для поля данных */
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE);
        /* Отображаемое имя для нашего аккаунта */
        values.put(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS, Address);
        // сохраняем данные
        contentResolver.insert(ContactsContract.Data.CONTENT_URI, values);
    }

}
