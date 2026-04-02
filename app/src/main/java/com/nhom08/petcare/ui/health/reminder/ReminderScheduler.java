package com.nhom08.petcare.ui.health.reminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.nhom08.petcare.data.model.NhacNho;

public class ReminderScheduler {

    /**
     * Đặt lịch AlarmManager cho 1 nhắc nhở.
     * Khi đến giờ, ReminderReceiver sẽ được gọi.
     */
    public static void schedule(Context context, NhacNho item, long triggerAtMillis) {
        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra(ReminderReceiver.EXTRA_ID,    item.id);
        intent.putExtra(ReminderReceiver.EXTRA_TITLE, item.loai);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                item.id.hashCode(), // unique request code theo id
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Android 12+ cần SCHEDULE_EXACT_ALARM permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
            } else {
                // Fallback nếu chưa cấp quyền — dùng inexact alarm
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
        }
    }

    /**
     * Hủy lịch đã đặt (nếu user xóa nhắc nhở thủ công).
     */
    public static void cancel(Context context, String reminderId) {
        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        Intent intent = new Intent(context, ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                reminderId.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        alarmManager.cancel(pendingIntent);
    }
}