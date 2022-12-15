package com.bibaboba.contacts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder> {

    // список всех контактов
    private List<Contact> contactsList = new ArrayList<>();
    private final Context context;

    // получаем контекст для выполнения операций с бд
    public ContactsAdapter(Context context) {
        this.context = context;
    }

    // задаем новый список контактов
    @SuppressLint("NotifyDataSetChanged")
    public void setContacts(ArrayList<Contact> contacts){
        contactsList = contacts;
        notifyDataSetChanged();
    }

    // очищаем список контактов адаптера (пока не использовалось)
    @SuppressLint("NotifyDataSetChanged")
    public void clearContacts(){
        contactsList.clear();
        notifyDataSetChanged();
    }

    // биндим к структуре отображения контакта
    @NonNull
    @Override
    public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_item, parent, false);
        return new ContactsViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsViewHolder holder, int position) {
        holder.bind(contactsList.get(position));
    }

    // получаем колличество элементов в списке контактов
    @Override
    public int getItemCount() {
        return contactsList.size();
    }

    // задаем отображение для каждого контакта
    static class ContactsViewHolder extends RecyclerView.ViewHolder{

        // кликабельная часть (весь layout)
        private final LinearLayout ContactBlock;

        // отображение имени (DisplayName)
        private final TextView NameTextView;

        // контекст выполнения оперций
        private final Context context;

        // передаем контекст и фрейм
        public ContactsViewHolder(View itemView, Context appContext){
            super(itemView);
            // все поле для клика
            ContactBlock = itemView.findViewById(R.id.contact_block);
            // данные контакта
            NameTextView = itemView.findViewById(R.id.name_text_view);
            context = appContext;
        }

        // биндим все данные контакта к layout
        @SuppressLint("SetTextI18n")
        public void bind(Contact contact){
            // если у контакта нет DisplayName
            if(contact.getDisplayName() == null){
                contact.setDisplayName("(Нет имени)");
            }

            // выводим отображаемое имя в текствью
            NameTextView.setText(contact.getDisplayName());

            // обработчик нажатий на layout
            ContactBlock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ContactInfoActivity.class);
                    intent.putExtra("contact", contact);
                    context.startActivity(intent);
                }
            });

            }
        }

    }


