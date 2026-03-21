package com.nhom08.petcare.ui.interactive;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.nhom08.petcare.R;
import com.nhom08.petcare.databinding.ActivitySoundListBinding;

public class SoundListActivity extends AppCompatActivity {

    private ActivitySoundListBinding binding;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySoundListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnDogSound.setOnClickListener(v -> playSound(R.raw.sound_dog));
        binding.btnCatSound.setOnClickListener(v -> playSound(R.raw.sound_cat));
        binding.btnBirdSound.setOnClickListener(v -> playSound(R.raw.sound_bird));
        binding.btnWhistleSound.setOnClickListener(v -> playSound(R.raw.sound_whistle));
    }

    private void playSound(int soundRes) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(this, soundRes);
        mediaPlayer.start();
        Toast.makeText(this, "Đang phát âm thanh...", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}