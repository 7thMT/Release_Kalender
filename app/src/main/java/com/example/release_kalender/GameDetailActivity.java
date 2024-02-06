package com.example.release_kalender;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class GameDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Game Description");
        }

        ImageView imageViewGame = findViewById(R.id.imageViewGame);
        TextView textViewDescription = findViewById(R.id.textViewDescription);
        TextView textViewPublisher = findViewById(R.id.textViewPublisher);

        Game game = (Game) getIntent().getSerializableExtra("GameDetail");

        Picasso.get()
                .load(game.getImageURL())
                .into(imageViewGame);
        textViewDescription.setText(game.getDescription());
        textViewPublisher.setText(game.getPublisher());
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Schließt die aktuelle Activity und kehrt zur vorherigen zurück
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}