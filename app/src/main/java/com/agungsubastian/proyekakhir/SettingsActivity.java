package com.agungsubastian.proyekakhir;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.agungsubastian.proyekakhir.notification.MovieDailyReceiver;
import com.agungsubastian.proyekakhir.notification.MovieUpcomingReceiver;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {
        private MovieDailyReceiver movieDailyReceiver = new MovieDailyReceiver();
        private MovieUpcomingReceiver movieUpcomingReceiver = new MovieUpcomingReceiver();

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            SwitchPreferenceCompat switchReminder = findPreference(getString(R.string.key_today_reminder));
            assert switchReminder != null;
            switchReminder.setOnPreferenceChangeListener(this);
            SwitchPreferenceCompat switchToday = findPreference(getString(R.string.key_release_reminder));
            assert switchToday != null;
            switchToday.setOnPreferenceChangeListener(this);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String key = preference.getKey();
            boolean b = (boolean) newValue;
            System.out.println("Key "+key);
            System.out.println(b);

            if(key.equals(getString(R.string.key_today_reminder))){
                if(b){
                    movieDailyReceiver.setAlarm(getActivity());
                }else{
                    movieDailyReceiver.cancelAlarm(Objects.requireNonNull(getActivity()));
                }
            }else{
                if(b){
                    movieUpcomingReceiver.setAlarm(getActivity());
                }else{
                    movieUpcomingReceiver.cancelAlarm(Objects.requireNonNull(getActivity()));
                }
            }
            return true;
        }
    }
}