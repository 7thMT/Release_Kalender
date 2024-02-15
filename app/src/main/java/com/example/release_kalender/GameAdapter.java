package com.example.release_kalender;

import android.annotation.SuppressLint;
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
import java.util.Objects;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameViewHolder> {

    private List<Game> gameList;
    private final GameAdapterListener listener;

    public interface GameAdapterListener{
        void onRequestCalendarPermission(Game game);
        void onCreateCalendarEvent(Game game);
    }

    public GameAdapter(List<Game> gameList, GameAdapterListener listener) {
        this.gameList = gameList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game, parent, false);
        return new GameViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {
        Game game = gameList.get(position);
        holder.gameTitle.setText(game.getName());
        holder.gameGenre.setText(game.getGenre());
        holder.gameReleaseDate.setText(game.getReleaseDate());

        int likeCount = game.getLikeCount();
        holder.gameLikes.setText(String.valueOf(likeCount));

        Picasso.get()
                .load(game.getImageURL())
                .placeholder(R.drawable.ic_game_placeholder)
                .error(R.drawable.ic_game_placeholder)
                .into(holder.gameImage);

        holder.gameImage.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), GameDetailActivity.class);
            intent.putExtra("GameDetail", game);
            view.getContext().startActivity(intent);
        });
        // Listener für Like-Button
        holder.gameLikeButton.setOnClickListener(view -> pressedGameLikeButton(game, position));

        holder.gameSaveButton.setOnClickListener(view -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                Game currentGame = gameList.get(currentPosition);
                if (ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                    listener.onRequestCalendarPermission(currentGame);
                } else {
                    listener.onCreateCalendarEvent(currentGame);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return gameList.size();
    }

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
            gameLikeButton = view.findViewById(R.id.gameLikeButton);
            gameSaveButton = view.findViewById(R.id.gameSaveButton);
            gameLikes = view.findViewById(R.id.gameLikes);
        }
    }

    private void pressedGameLikeButton(Game game, int position){
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        String likeDocId = userId + "_" + game.getId();

        DocumentReference likeRef = FirebaseFirestore.getInstance()
                .collection("likes")
                .document(likeDocId);

        likeRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    likeRef.delete();
                    game.setLikeCount(game.getLikeCount() - 1);
                    notifyItemChanged(position);
                } else {
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

    @SuppressLint("NotifyDataSetChanged")
    public void setGames(List<Game> games) {
        this.gameList = games;
        notifyDataSetChanged();
    }
}
