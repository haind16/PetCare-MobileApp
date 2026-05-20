package com.nhom08.petcare.utils;

import android.content.Context;
import android.net.Uri;
import org.json.JSONObject;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Utility class hỗ trợ tải hình ảnh lên dịch vụ lưu trữ đám mây Cloudinary.
 * Sử dụng HttpURLConnection để thực hiện request POST multipart/form-data.
 */
public class CloudinaryUploader {

    // Thông tin cấu hình Cloudinary (Cloud Name và Upload Preset)
    private static final String CLOUD_NAME    = "dt9slcin9";
    private static final String UPLOAD_PRESET = "ml_default";
    private static final String UPLOAD_URL    =
            "https://api.cloudinary.com/v1_1/" + CLOUD_NAME + "/image/upload";

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    /**
     * Interface callback để nhận kết quả trả về sau khi upload.
     */
    public interface UploadCallback {
        void onSuccess(String imageUrl);
        void onFailure(String error);
    }

    /**
     * Upload ảnh từ Uri (local) lên Cloudinary.
     * @param context Context ứng dụng
     * @param imageUri Uri của ảnh cần upload
     * @param callback Callback xử lý kết quả
     */
    public static void uploadImage(Context context, Uri imageUri, UploadCallback callback) {
        executor.execute(() -> {
            try {
                // Mở InputStream từ Uri
                InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
                if (inputStream == null) {
                    runOnMain(callback, null, "Không thể đọc ảnh");
                    return;
                }

                // Thiết lập kết nối HTTP
                String boundary = "----FormBoundary" + System.currentTimeMillis();
                URL url = new URL(UPLOAD_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type",
                        "multipart/form-data; boundary=" + boundary);
                conn.setConnectTimeout(30000);
                conn.setReadTimeout(30000);

                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

                // Gửi field upload_preset (bắt buộc đối với Unsigned Upload)
                dos.writeBytes("--" + boundary + "\r\n");
                dos.writeBytes("Content-Disposition: form-data; name=\"upload_preset\"\r\n\r\n");
                dos.writeBytes(UPLOAD_PRESET + "\r\n");

                // Gửi file ảnh dưới dạng stream
                dos.writeBytes("--" + boundary + "\r\n");
                dos.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"pet.jpg\"\r\n");
                dos.writeBytes("Content-Type: image/jpeg\r\n\r\n");

                byte[] buffer = new byte[4096];
                int len;
                while ((len = inputStream.read(buffer)) > 0) {
                    dos.write(buffer, 0, len);
                }
                inputStream.close();

                dos.writeBytes("\r\n--" + boundary + "--\r\n");
                dos.flush();
                dos.close();

                // Kiểm tra mã phản hồi từ server
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream responseStream = conn.getInputStream();
                    String responseStr = new String(responseStream.readAllBytes());
                    responseStream.close();

                    // Parse JSON để lấy URL ảnh an toàn (https)
                    JSONObject json = new JSONObject(responseStr);
                    String secureUrl = json.getString("secure_url");
                    runOnMain(callback, secureUrl, null);
                } else {
                    runOnMain(callback, null, "Upload thất bại: HTTP " + responseCode);
                }
                conn.disconnect();

            } catch (Exception e) {
                runOnMain(callback, null, e.getMessage());
            }
        });
    }

    /**
     * Helper đưa kết quả về Main Thread để cập nhật UI.
     */
    private static void runOnMain(UploadCallback callback, String url, String error) {
        new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
            if (url != null) callback.onSuccess(url);
            else callback.onFailure(error != null ? error : "Lỗi không xác định");
        });
    }
}