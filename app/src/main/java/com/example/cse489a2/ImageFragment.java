package com.example.cse489a2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
public class ImageFragment extends Fragment {

    // the second link is only a backup if the first one fails
    private static final String[] IMAGE_URLS = {
            "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3f/Fronalpstock_big.jpg/960px-Fronalpstock_big.jpg",
            "https://picsum.photos/id/1015/960/640.jpg"
    };

    private final Handler handler = new Handler(Looper.getMainLooper());
    private ImageView imageView;
    private View progress;
    private ScaleGestureDetector scaleDetector;
    private float scale = 1f;

    public ImageFragment() {
        super(R.layout.fragment_image);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imageView = view.findViewById(R.id.image_view);
        progress = view.findViewById(R.id.progress);

        // pinch gesture: change the scale of the image view
        scaleDetector = new ScaleGestureDetector(requireContext(),
                new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                    @Override
                    public boolean onScale(ScaleGestureDetector detector) {
                        scale = scale * detector.getScaleFactor();
                        scale = Math.max(0.5f, Math.min(scale, 5f));
                        imageView.setScaleX(scale);
                        imageView.setScaleY(scale);
                        return true;
                    }
                });
        view.setOnTouchListener((v, event) -> {
            scaleDetector.onTouchEvent(event);
            return true;
        });

        loadImage();
    }

    // network work must not run on the main thread
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
                    Toast.makeText(requireContext(), R.string.image_failed, Toast.LENGTH_LONG).show();
                }
            });
        }).start();
    }

    private Bitmap download() {
        for (String address : IMAGE_URLS) {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(address).openConnection();
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(15000);
                connection.setRequestProperty("User-Agent", "CSE489Assignment2/1.0 (student project)");
                if (connection.getResponseCode() == 200) {
                    InputStream in = connection.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(in);
                    in.close();
                    if (bitmap != null) {
                        return bitmap;
                    }
                }
            } catch (Exception e) {
                // try the next link
            }
        }
        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
    }
}
