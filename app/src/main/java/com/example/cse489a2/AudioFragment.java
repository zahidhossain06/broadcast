package com.example.cse489a2;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

// Plays one audio file stored inside the app (res/raw/sample_audio.mp3).
public class AudioFragment extends Fragment {

    private MediaPlayer player;
    private TextView status;

    public AudioFragment() {
        super(R.layout.fragment_audio);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        status = view.findViewById(R.id.text_status);

        player = MediaPlayer.create(requireContext(), R.raw.sample_audio);
        if (player == null) {
            status.setText(R.string.status_error);
            return;
        }
        player.setOnCompletionListener(mp -> status.setText(R.string.status_finished));

        view.findViewById(R.id.btn_play).setOnClickListener(v -> {
            if (!player.isPlaying()) {
                player.start();
                status.setText(R.string.status_playing);
            }
        });

        view.findViewById(R.id.btn_pause).setOnClickListener(v -> {
            if (player.isPlaying()) {
                player.pause();
                status.setText(R.string.status_paused);
            }
        });

        view.findViewById(R.id.btn_stop).setOnClickListener(v -> {
            player.pause();
            player.seekTo(0);
            status.setText(R.string.status_stopped);
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (player != null && player.isPlaying()) {
            player.pause();
            status.setText(R.string.status_paused);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (player != null) {
            player.release();
            player = null;
        }
    }
}
