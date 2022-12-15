package com.bibaboba.contacts;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity implements
        PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    private static final String TITLE_TAG = "settingsActivityTitle";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);


        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new HeaderFragment())
                    .commit();
        } else {
            setTitle(savedInstanceState.getCharSequence(TITLE_TAG));
        }
        getSupportFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    @Override
                    public void onBackStackChanged() {
                        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                            setTitle(R.string.title_activity_settings);
                        }
                    }
                });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save current activity title so we can set it again after a configuration change
        outState.putCharSequence(TITLE_TAG, getTitle());
    }


    @Override
    public boolean onPreferenceStartFragment(PreferenceFragmentCompat caller, Preference pref) {

        // Instantiate the new Fragment
        final Bundle args = pref.getExtras();
        final Fragment fragment = getSupportFragmentManager().getFragmentFactory().instantiate(
                getClassLoader(),
                pref.getFragment());
        fragment.setArguments(args);
        fragment.setTargetFragment(caller, 0);
        // Replace the existing Fragment with the new Fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.settings, fragment)
                .addToBackStack(null)
                .commit();
        setTitle(pref.getTitle());
        return true;
    }

    public static class HeaderFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.header_preferences, rootKey);
            // получаем настройки
            SharedPreferences settings = getActivity().getSharedPreferences("settings", MODE_PRIVATE);
            // создаем редактор
            SharedPreferences.Editor settingsEditor = settings.edit();

            // получаем настройи с фрагмента
            ListPreference sortBy = findPreference("sort_by");
            ListPreference sortByName = findPreference("sort_by_name_lastname");
            ListPreference filter = findPreference("filter");

            // класс для работы с бд контактов системы
            Contacts contacts = new Contacts(getContext().getContentResolver());
            // получаем спискок статусов всех контактов без повторений
            ArrayList<String> statusesList = contacts.GetStatuses();
            // добавляем дефолтную сортировку
            statusesList.add(0, "По умолчанию");
            // массив для передачи его в выбор фильтрации
            CharSequence[] ch = new CharSequence[statusesList.size()];

            statusesList.toArray(ch);

            // если статус равен пустой строке
            for (int i = 0; i < ch.length; i++){
                if(ch[i].equals("")){
                    ch[i] = "(статус без имени)";
                }
            }
            // ставим все статусы в доступные для фитльтрации
            filter.setEntries(ch);
            filter.setEntryValues(ch);

            // сохранение новых настроек при их изменении

            sortBy.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    sortBy.setValue(newValue.toString());
                    settingsEditor.putString("sort_by", sortBy.getValue());
                    settingsEditor.commit();
                    return false;
                }
            });

            sortByName.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    sortByName.setValue(newValue.toString());
                    settingsEditor.putString("sort_by_name_lastname", sortByName.getValue());
                    settingsEditor.commit();
                    return false;
                }
            });

            filter.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    filter.setValue(newValue.toString());
                    settingsEditor.putString("filter", filter.getValue());
                    settingsEditor.commit();
                    return false;
                }
            });

        }

    }

}