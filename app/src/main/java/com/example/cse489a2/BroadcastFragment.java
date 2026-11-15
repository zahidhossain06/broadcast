package com.example.cse489a2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

// First screen: pick the broadcast type, then go to the second activity.
public class BroadcastFragment extends Fragment {

    public BroadcastFragment() {
        super(R.layout.fragment_broadcast);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Spinner spinner = view.findViewById(R.id.spinner_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(), R.array.broadcast_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        view.findViewById(R.id.btn_proceed).setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), SecondActivity.class);
            // 0 = custom broadcast, 1 = system battery
            intent.putExtra(SecondActivity.EXTRA_TYPE, spinner.getSelectedItemPosition());
            startActivity(intent);
        });
    }
}
