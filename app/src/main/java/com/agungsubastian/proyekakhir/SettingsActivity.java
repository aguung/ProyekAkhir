package com.agungsubastian.proyekakhir;

import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.agungsubastian.proyekakhir.helper.ApiClient;
import com.agungsubastian.proyekakhir.model.MoviesModel;
import com.agungsubastian.proyekakhir.notification.MovieDailyReceiver;
import com.agungsubastian.proyekakhir.notification.MovieUpcomingReceiver;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        private ApiClient apiClient = new ApiClient();

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
                    setReleaseAlarm();
                }else{
                    movieUpcomingReceiver.cancelAlarm(Objects.requireNonNull(getActivity()));
                }
            }
            return true;
        }

        private void setReleaseAlarm(){
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = new Date();
            String now = dateFormat.format(date);
            Call<MoviesModel> apiCall = apiClient.getService().getReleaseToday(now,now);
            apiCall.enqueue(new Callback<MoviesModel>() {
                @Override
                public void onResponse(@NonNull Call<MoviesModel> call, @NonNull Response<MoviesModel> response) {
                    System.out.println(response);
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        movieUpcomingReceiver.setAlarm(getActivity(), response.body().getResults());
                    } else {
                        Toast.makeText(getContext(), R.string.error_load, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<MoviesModel> call, @NonNull Throwable t) {
                    if (t instanceof SocketTimeoutException) {
                        Toast.makeText(getContext(), R.string.time_out, Toast.LENGTH_SHORT).show();
                    } else if (t instanceof UnknownHostException) {
                        Toast.makeText(getContext(), R.string.no_connection, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}