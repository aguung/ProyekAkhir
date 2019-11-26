package com.agungsubastian.proyekakhir.notification;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.agungsubastian.proyekakhir.DetailMovieActivity;
import com.agungsubastian.proyekakhir.R;
import com.agungsubastian.proyekakhir.helper.ApiClient;
import com.agungsubastian.proyekakhir.model.MoviesModel;
import com.agungsubastian.proyekakhir.model.ResultItemMovies;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieUpcomingReceiver extends BroadcastReceiver {
    private static int notifId = 1000;
    private ApiClient apiClient = new ApiClient();

    @Override
    public void onReceive(Context context, Intent intent) {
        setReleaseAlarm(context);
    }

    private void sendNotification(Context context, String title, String desc, int id, ResultItemMovies movieResult) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(
                Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(context, DetailMovieActivity.class);
        intent.putExtra(DetailMovieActivity.EXTRA_DATA, movieResult);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, id, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Uri uriTone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"102")
                .setSmallIcon(R.drawable.ic_movie)
                .setContentTitle(title)
                .setContentText(desc)
                .setContentIntent(pendingIntent)
                .setColor(ContextCompat.getColor(context, android.R.color.transparent))
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setSound(uriTone);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("102",
                    "NOTIFICATION_CHANNEL_NAME", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

            builder.setChannelId("102");
            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);
        }
        assert notificationManager != null;
        notificationManager.notify(id, builder.build());
    }

    public void setAlarm(Context context) {
        cancelAlarm(context);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            assert alarmManager != null;
            alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    getPendingIntent(context)
            );
        } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            assert alarmManager != null;
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(), getPendingIntent(context));
        }
    }
    public void cancelAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;
        alarmManager.cancel(getPendingIntent(context));
    }
    private static PendingIntent getPendingIntent(Context context) {
        Intent intent = new Intent(context, MovieUpcomingReceiver.class);
        return PendingIntent.getBroadcast(context, 101, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void setReleaseAlarm(final Context context){
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
                    List<ResultItemMovies> movieResults;
                    movieResults = response.body().getResults();
                    for(ResultItemMovies movie : movieResults){
                        String poster = movie.getPosterPath();
                        int movieId = movie.getId();
                        String movieTitle = movie.getOriginalTitle();
                        float rate = movie.getVoteAverage();
                        String ovr = movie.getOverview();
                        String date = movie.getReleaseDate();
                        ResultItemMovies movieResult = new ResultItemMovies(poster,movieId,movieTitle,rate,ovr,date);
                        String desc = String.format(context.getString(R.string.release_today_msg), movieTitle);
                        sendNotification(context, context.getString(R.string.app_name), desc, notifId, movieResult);
                        notifId++;
                    }
                } else {
                    Toast.makeText(context, R.string.error_load, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MoviesModel> call, @NonNull Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    Toast.makeText(context, R.string.time_out, Toast.LENGTH_SHORT).show();
                } else if (t instanceof UnknownHostException) {
                    Toast.makeText(context, R.string.no_connection, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
