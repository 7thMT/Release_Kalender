package com.example.release_kalender.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.release_kalender.Game;
import com.example.release_kalender.GameAdapter;
import com.example.release_kalender.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private GameAdapter adapter;
    private List<Game> gameList = new ArrayList<>();  // Initialize an empty list

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Pass the initially empty list to the GameAdapter
        adapter = new GameAdapter(gameList);
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
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
