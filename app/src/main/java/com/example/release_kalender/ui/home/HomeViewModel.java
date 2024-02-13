package com.example.release_kalender.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.release_kalender.Game;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<List<Game>> gamesLiveData;
    private final MutableLiveData<Boolean> loadingLiveData;

    private final List<Game> gameList;
    private final FirebaseFirestore db;

    public HomeViewModel() {
        gamesLiveData = new MutableLiveData<>();
        loadingLiveData = new MutableLiveData<>();
        gameList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

    }

    public LiveData<List<Game>> getGames() {
        return gamesLiveData;
    }

    public LiveData<Boolean> isLoading() {
        return loadingLiveData;
    }

    public void refreshGames(){
        loadGamesFromFirestore();
    }

    private void loadGamesFromFirestore() {
        loadingLiveData.setValue(true);
        gameList.clear();

        db.collection("games").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                final int[] loadedLikes = {0};
                int gameCount = task.getResult().size();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Game game = document.toObject(Game.class);
                    game.setId(document.getId());
                    gameList.add(game);
                    loadLikesCount(game, gameCount, loadedLikes);
                }
                gamesLiveData.setValue(gameList);
            }
        });
    }

    private void loadLikesCount(Game game, int gameCount, final int[] loadedLikes) {
        db.collection("likes")
                .whereEqualTo("gameId", game.getId())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int likeCount = queryDocumentSnapshots.size();
                    game.setLikeCount(likeCount);

                    loadedLikes[0]++;
                    if (loadedLikes[0] == gameCount) {
                        gamesLiveData.setValue(gameList);
                        loadingLiveData.setValue(false);
                    }
                });
    }
}
