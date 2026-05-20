package com.nhom08.petcare.ui.health.reminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.nhom08.petcare.data.model.NhacNho;

/**
 * Lớp hỗ trợ lập lịch nhắc nhở sử dụng AlarmManager.
 * Cho phép đặt lịch báo thức chính xác cho các hoạt động chăm sóc thú cưng.
 */
public class ReminderScheduler {

    /**
     * Đặt lịch AlarmManager cho một mục nhắc nhở.
     * Khi đến thời gian quy định, hệ thống sẽ gửi một Broadcast đến ReminderReceiver.
     * @param context Context của ứng dụng
     * @param item Đối tượng nhắc nhở cần đặt lịch
     * @param triggerAtMillis Thời điểm thực hiện tính bằng milliseconds
     */
    public static void schedule(Context context, NhacNho item, long triggerAtMillis) {
        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra(ReminderReceiver.EXTRA_ID,    item.id);
        intent.putExtra(ReminderReceiver.EXTRA_TITLE, item.loai);

        // Tạo PendingIntent để gửi Broadcast, sử dụng hashCode của ID làm request code để phân biệt các nhắc nhở
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                item.id.hashCode(), 
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Xử lý lập lịch chính xác dựa trên phiên bản Android
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Android 12+ yêu cầu quyền SCHEDULE_EXACT_ALARM để đặt lịch chính xác
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
            } else {
                // Fallback nếu chưa cấp quyền — sử dụng phương thức đặt lịch không chính xác
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
        }
    }

    /**
     * Hủy lịch nhắc nhở đã đặt.
     * Thường dùng khi người dùng xóa nhắc nhở hoặc đã hoàn thành sớm.
     * @param context Context của ứng dụng
     * @param reminderId ID của nhắc nhở cần hủy
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