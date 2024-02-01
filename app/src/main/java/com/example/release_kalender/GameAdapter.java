package com.example.release_kalender;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.List;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameViewHolder> {

    private List<Game> gameList; // Die Datenquelle für den Adapter

    // Konstruktor des Adapters
    public GameAdapter(List<Game> gameList) {
        this.gameList = gameList;
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

        // Laden des Spielbildes mit Picasso
        Picasso.get()
                .load(game.getImageURL())
                .placeholder(R.drawable.ic_game_placeholder) // Ersatzbild, während das Bild geladen wird
                .error(R.drawable.ic_game_placeholder) // Bild für den Fehlerfall
                .into(holder.gameImage);

        // Listener für Like-Button
        holder.gameLikeButton.setOnClickListener(view -> {
            // Implementierung, was passieren soll, wenn auf Like geklickt wird
        });

        // Listener für Save-Button
        holder.gameSaveButton.setOnClickListener(view -> {
            // Implementierung, was passieren soll, wenn auf Save geklickt wird
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

        public GameViewHolder(View view) {
            super(view);
            gameImage = view.findViewById(R.id.gameImage);
            gameTitle = view.findViewById(R.id.gameTitle);
            gameGenre = view.findViewById(R.id.gameGenre);
            gameReleaseDate = view.findViewById(R.id.gameReleaseDate);
            gameLikeButton = view.findViewById(R.id.gameLikeButton); // ID für den Like-Button
            gameSaveButton = view.findViewById(R.id.gameSaveButton); // ID für den Save-Button
        }
    }
}
