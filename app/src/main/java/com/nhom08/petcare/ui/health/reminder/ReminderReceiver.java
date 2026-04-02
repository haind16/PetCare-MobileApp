package com.nhom08.petcare.ui.health.reminder;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.nhom08.petcare.R;
import com.nhom08.petcare.data.repository.NhacNhoRepository;
import com.nhom08.petcare.ui.main.MainActivity;

public class ReminderReceiver extends BroadcastReceiver {

    public static final String CHANNEL_ID    = "petcare_reminder";
    public static final String EXTRA_ID      = "reminder_id";
    public static final String EXTRA_TITLE   = "reminder_title";

    @Override
    public void onReceive(Context context, Intent intent) {
        String id    = intent.getStringExtra(EXTRA_ID);
        String title = intent.getStringExtra(EXTRA_TITLE);
        if (title == null) title = "Nhắc nhở thú cưng";

        // Hiện notification
        showNotification(context, id, title);

        // Xóa nhắc nhở khỏi Room (đã qua)
        if (id != null) {
            NhacNhoRepository repo = new NhacNhoRepository(context);
            repo.delete(id, r -> {});
        }
    }

    private void showNotification(Context context, String id, String title) {
        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Tạo channel (Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Nhắc nhở PetCare",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Thông báo lịch chăm sóc thú cưng");
            manager.createNotificationChannel(channel);
        }

        // Intent mở app khi bấm notification
        Intent openIntent = new Intent(context, MainActivity.class);
        openIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        int notifId = id != null ? id.hashCode() : (int) System.currentTimeMillis();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_bell)
                .setContentTitle("🐾 PetCare — Nhắc nhở")
                .setContentText(title)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        manager.notify(notifId, builder.build());
    }
}