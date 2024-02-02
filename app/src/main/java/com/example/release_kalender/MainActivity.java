package com.example.release_kalender;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.release_kalender.databinding.ActivityMainBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GameAdapter adapter;
    private List<Game> gameList = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GameAdapter(gameList);
        recyclerView.setAdapter(adapter);

        loadGamesFromFirestore();
    }

    private void loadGamesFromFirestore() {
        db.collection("games").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                final int[] loadedLikes ={0};
                int gameCount = task.getResult().size();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Game game = document.toObject(Game.class);
                    game.setId(document.getId());
                    gameList.add(game);
                    loadLikesCount(game, gameCount, loadedLikes);
                }
                adapter.notifyDataSetChanged(); // Benachrichtigen Sie den Adapter, dass sich die Daten geändert haben
            } else {
            }
        });
    }
    private void loadLikesCount(Game game, int gameCount, final int[] loadedLikes) {
        db.collection("likes")
                .whereEqualTo("gameId", game.getId())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int likeCount = queryDocumentSnapshots.size(); // Anzahl der Likes
                    game.setLikeCount(likeCount); // Setze die Anzahl der Likes im Game-Objekt

                    // Zähler für geladene Likes erhöhen
                    loadedLikes[0]++;
                    // Wenn alle Likes geladen wurden, benachrichtige den Adapter
                    if (loadedLikes[0] == gameCount) {
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}
