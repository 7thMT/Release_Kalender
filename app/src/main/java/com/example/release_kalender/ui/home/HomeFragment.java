package com.example.release_kalender.ui.home;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.release_kalender.Game;
import com.example.release_kalender.GameAdapter;
import com.example.release_kalender.R;
import com.example.release_kalender.databinding.FragmentHomeBinding;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;

public class HomeFragment extends Fragment implements GameAdapter.GameAdapterListener {
    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private GameAdapter adapter;
    private List<Game> gameList = new ArrayList<>();  // Initialize an empty list
    private Chip lastCheckedChip = null;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        Toast.makeText(getContext(), "Berechtigung erteilt", Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(getContext(), "Berechtigung erforderlich, um Spiele im Kalender zu speichern.", Toast.LENGTH_LONG).show();
                    }
                });
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ChipGroup chipGroup = view.findViewById(R.id.chip_group);

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        homeViewModel.getGames().observe(getViewLifecycleOwner(), games -> {
            // Update UI with the loaded games
            gameList.clear();
            gameList.addAll(games);
            adapter.notifyDataSetChanged();
        });

        for(int i = 0; i < chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) chipGroup.getChildAt(i);
            chip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (lastCheckedChip != null && lastCheckedChip.getId() == chip.getId()) {
                        chip.setChecked(false);
                        lastCheckedChip = null;
                        adapter.setGames(gameList);
                    } else {
                            lastCheckedChip = chip;
                            filterGames(chip.getText().toString());
                        }
                adapter.notifyDataSetChanged();
                }
            });
        }
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Pass the initially empty list to the GameAdapter
        adapter = new GameAdapter(gameList, this);
        recyclerView.setAdapter(adapter);

        homeViewModel.getGames().observe(getViewLifecycleOwner(), games -> {
            // Update UI with the loaded games
            gameList.clear();  // Clear the current list
            gameList.addAll(games);  // Update it with the new data
            adapter.notifyDataSetChanged();  // Notify the adapter of the data change
        });

        return root;
    }
    @Override
    public void onResume() {
        super.onResume();
        homeViewModel.refreshGames();
    }

    @Override
    public void onRequestCalendarPermission(Game game) {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            createCalendarEvent(game);
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_CALENDAR)) {
            showRationaleDialog();
        } else {
            openAppSettings();
        }
    }

    @Override
    public void onCreateCalendarEvent(Game game) {
        createCalendarEvent(game);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void createCalendarEvent(Game game) {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setData(CalendarContract.Events.CONTENT_URI);
        intent.putExtra(CalendarContract.Events.TITLE, game.getName());
        intent.putExtra(CalendarContract.Events.DESCRIPTION, game.getDescription());
        intent.putExtra(CalendarContract.Events.ALL_DAY, true);

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        try {
            Date releaseDate = sdf.parse(game.getReleaseDate());
            if(releaseDate != null){
                long startTime = releaseDate.getTime();
                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime);

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(releaseDate);
                calendar.add(Calendar.DATE, 1);
                long endTime = calendar.getTimeInMillis();
                intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        startActivity(intent);

    }

    private void showRationaleDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Berechtigung erforderlich")
                .setMessage("Zugriff auf den Kalender ist notwendig, um Spiele hinzuzufügen. Bitte erlaube den Zugriff.")
                .setPositiveButton("Erlauben", (dialog, which) -> requestPermissionLauncher.launch(Manifest.permission.WRITE_CALENDAR))
                .setNegativeButton("Ablehnen", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void openAppSettings() {
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", requireActivity().getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }
    private void filterGames(String filter) {
        List<Game> filteredList = new ArrayList<>();
        for (Game game : gameList) { // gameList sollte deine ursprüngliche Liste der Spiele sein
            if (game.getGenre().contains(filter)) {
                filteredList.add(game);
            }
        }
        // Setze die gefilterte Liste und benachrichtige den Adapter
        adapter.setGames(filteredList);
    }
}
