package com.example.cse489a2;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

// Plays one video stored inside the app (res/raw/sample_video.mp4).
public class VideoFragment extends Fragment {

    private VideoView videoView;

    public VideoFragment() {
        super(R.layout.fragment_video);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        videoView = view.findViewById(R.id.video_view);

        MediaController controller = new MediaController(requireContext());
        controller.setAnchorView(videoView);
        videoView.setMediaController(controller);

        Uri uri = Uri.parse("android.resource://" + requireContext().getPackageName()
                + "/" + R.raw.sample_video);
        videoView.setVideoURI(uri);
        videoView.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (videoView != null && videoView.isPlaying()) {
            videoView.pause();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (videoView != null) {
            videoView.stopPlayback();
            videoView = null;
        }
    }
}
