package com.example.release_kalender;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.Manifest;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameViewHolder> {

    private List<Game> gameList; // Die Datenquelle für den Adapter
    private GameAdapterListener listener;

    public interface GameAdapterListener{
        void onRequestCalendarPermission(Game game);
        void onCreateCalendarEvent(Game game);
    }
    // Konstruktor des Adapters
    public GameAdapter(List<Game> gameList, GameAdapterListener listener) {
        this.gameList = gameList;
        this.listener = listener;
    }

    // Erstellen neuer Views (wird vom Layout-Manager aufgerufen)
    @NonNull
    @Override
    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Erstellen eines neuen Views
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game, parent, false);
        return new GameViewHolder(itemView);
    }

    // Ersetzen des Inhalts eines Views (wird vom Layout-Manager aufgerufen)
    @Override
    public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {
        Game game = gameList.get(position); // Das aktuelle Spiel-Objekt

        // Befüllen der Views mit Inhalten
        holder.gameTitle.setText(game.getName());
        holder.gameGenre.setText(game.getGenre());
        holder.gameReleaseDate.setText(game.getReleaseDate());

        int likeCount = game.getLikeCount();
        holder.gameLikes.setText(String.valueOf(likeCount));

        // Laden des Spielbildes mit Picasso
        Picasso.get()
                .load(game.getImageURL())
                .placeholder(R.drawable.ic_game_placeholder) // Ersatzbild, während das Bild geladen wird
                .error(R.drawable.ic_game_placeholder) // Bild für den Fehlerfall
                .into(holder.gameImage);

        holder.gameImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), GameDetailActivity.class);
                intent.putExtra("GameDetail", game);
                view.getContext().startActivity(intent);
            }
        });
        // Listener für Like-Button
        holder.gameLikeButton.setOnClickListener(view -> {
            // Implementierung, was passieren soll, wenn auf Like geklickt wird
            pressedGameLikeButton(game, position);
        });

        // Listener für Save-Button
        holder.gameSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    Game currentGame = gameList.get(currentPosition);
                    if (ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                        // Rufen Sie die Methode der Schnittstelle auf, um die Berechtigung anzufordern
                        listener.onRequestCalendarPermission(currentGame);
                    } else {
                        // Rufen Sie die Methode der Schnittstelle auf, um den Kalendereintrag zu erstellen
                        listener.onCreateCalendarEvent(currentGame);
                    }
                }
            }
        });
    }

    // Rückgabe der Größe der Datenquelle (wird vom Layout-Manager aufgerufen)
    @Override
    public int getItemCount() {
        return gameList.size();
    }

    // Stellt die Referenzen für die Views innerhalb eines Daten-Elements bereit
    public static class GameViewHolder extends RecyclerView.ViewHolder {
        public ImageView gameImage;
        public TextView gameTitle;
        public TextView gameGenre;
        public TextView gameReleaseDate;
        public ImageView gameLikeButton;
        public ImageView gameSaveButton;
        public TextView gameLikes;
        public GameViewHolder(View view) {
            super(view);
            gameImage = view.findViewById(R.id.gameImage);
            gameTitle = view.findViewById(R.id.gameTitle);
            gameGenre = view.findViewById(R.id.gameGenre);
            gameReleaseDate = view.findViewById(R.id.gameReleaseDate);
            gameLikeButton = view.findViewById(R.id.gameLikeButton); // ID für den Like-Button
            gameSaveButton = view.findViewById(R.id.gameSaveButton); // ID für den Save-Button
            gameLikes = view.findViewById(R.id.gameLikes);
        }
    }

    private void pressedGameLikeButton(Game game, int position){
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Aktuelle UserID
        String likeDocId = userId + "_" + game.getId();

        DocumentReference likeRef = FirebaseFirestore.getInstance()
                .collection("likes")
                .document(likeDocId);

        likeRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Like existiert, also entfernen wir ihn
                    likeRef.delete();
                    game.setLikeCount(game.getLikeCount() - 1);
                    notifyItemChanged(position);
                } else {
                    // Like existiert nicht, also fügen wir ihn hinzu
                    Map<String, Object> like = new HashMap<>();
                    like.put("userId", userId);
                    like.put("gameId", game.getId());
                    likeRef.set(like);
                    game.setLikeCount(game.getLikeCount() + 1);
                    notifyItemChanged(position);
                }
            }
        });
    }
}
