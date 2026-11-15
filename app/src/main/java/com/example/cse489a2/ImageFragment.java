package com.example.cse489a2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

// Loads an image from the internet and lets the user zoom it with a pinch gesture.
// Drag with one finger to move a zoomed image, double tap to reset.
public class ImageFragment extends Fragment {

    // same picture as res/drawable/sample_image.jpg, served from the GitHub repo
    private static final String IMAGE_URL =
            "https://raw.githubusercontent.com/zahidhossain06/broadcast/main/app/src/main/res/drawable/sample_image.jpg";

    private final Handler handler = new Handler(Looper.getMainLooper());
    private ImageView imageView;
    private View progress;

    private ScaleGestureDetector scaleDetector;
    private GestureDetector tapDetector;
    private float scale = 1f;
    private float lastX, lastY;

    public ImageFragment() {
        super(R.layout.fragment_image);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imageView = view.findViewById(R.id.image_view);
        progress = view.findViewById(R.id.progress);

        // 1. pinch: two fingers apart = bigger, together = smaller
        scaleDetector = new ScaleGestureDetector(requireContext(),
                new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                    @Override
                    public boolean onScale(ScaleGestureDetector detector) {
                        scale = scale * detector.getScaleFactor();
                        scale = Math.max(1f, Math.min(scale, 5f)); // between 1x and 5x
                        imageView.setScaleX(scale);
                        imageView.setScaleY(scale);
                        return true;
                    }
                });

        // 2. double tap: back to normal size
        tapDetector = new GestureDetector(requireContext(),
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        resetZoom();
                        return true;
                    }
                });

        // the touch listener sits on the ImageView itself so it gets every finger event
        imageView.setOnTouchListener((v, event) -> {
            scaleDetector.onTouchEvent(event);
            tapDetector.onTouchEvent(event);

            // 3. one finger drag moves the picture when it is zoomed in
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    lastX = event.getRawX();
                    lastY = event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (event.getPointerCount() == 1 && !scaleDetector.isInProgress() && scale > 1f) {
                        imageView.setTranslationX(imageView.getTranslationX() + event.getRawX() - lastX);
                        imageView.setTranslationY(imageView.getTranslationY() + event.getRawY() - lastY);
                    }
                    lastX = event.getRawX();
                    lastY = event.getRawY();
                    break;
            }
            return true; // we handled the touch
        });

        loadImage();
    }

    private void resetZoom() {
        scale = 1f;
        imageView.setScaleX(1f);
        imageView.setScaleY(1f);
        imageView.setTranslationX(0f);
        imageView.setTranslationY(0f);
    }

    // network work must not run on the main thread, so it runs on a new Thread
    private void loadImage() {
        new Thread(() -> {
            Bitmap bitmap = download();
            handler.post(() -> {
                if (getView() == null) {
                    return; // screen was closed meanwhile
                }
                progress.setVisibility(View.GONE);
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                } else {
                    // no internet: show the copy that is inside the app
                    imageView.setImageResource(R.drawable.sample_image);
                    Toast.makeText(requireContext(), R.string.image_failed, Toast.LENGTH_LONG).show();
                }
            });
        }).start();
    }

    private Bitmap download() {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(IMAGE_URL).openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(15000);
            if (connection.getResponseCode() == 200) {
                InputStream in = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                in.close();
                return bitmap;
            }
        } catch (Exception e) {
            // no internet or bad link
        }
        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
    }
}
