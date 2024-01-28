package com.example.release_kalender.ui.calender;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.release_kalender.databinding.FragmentCalenderBinding;

public class CalenderFragment extends Fragment {

    private FragmentCalenderBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        CalenderViewModel calenderViewModel =
                new ViewModelProvider(this).get(CalenderViewModel.class);

        binding = FragmentCalenderBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textCalender;
        calenderViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}