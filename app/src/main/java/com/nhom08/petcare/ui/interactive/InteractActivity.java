package com.nhom08.petcare.ui.interactive;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.nhom08.petcare.databinding.ActivityInteractBinding;

public class InteractActivity extends AppCompatActivity {

    private ActivityInteractBinding binding;
    private Handler handler = new Handler();
    private int playMinutes = 0;
    private boolean isPlaying = false;
    private Runnable timerRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInteractBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        // Bắt đầu / Dừng chơi
        binding.btnStartPlay.setOnClickListener(v -> {
            if (!isPlaying) {
                isPlaying = true;
                binding.btnStartPlay.setText("Dừng");
                startTimer();
            } else {
                isPlaying = false;
                binding.btnStartPlay.setText("Bắt đầu");
                handler.removeCallbacks(timerRunnable);
            }
        });

        // Sang màn âm thanh
        binding.btnSoundList.setOnClickListener(v ->
                startActivity(new Intent(this, SoundListActivity.class)));
    }

    private void startTimer() {
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                playMinutes++;
                binding.tvTimer.setText("Thời gian chơi: " + playMinutes + " phút");
                handler.postDelayed(this, 60000); // 1 phút
            }
        };
        handler.postDelayed(timerRunnable, 60000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(timerRunnable);
    }
}