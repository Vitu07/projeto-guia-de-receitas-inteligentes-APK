package br.edu.cruzeirodosul.guiadereceitasinteligente.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import br.edu.cruzeirodosul.guiadereceitasinteligente.R;
import br.edu.cruzeirodosul.guiadereceitasinteligente.adapter.RecipeAdapter;
import br.edu.cruzeirodosul.guiadereceitasinteligente.model.Recipe;

public class FavoritesActivity extends AppCompatActivity implements RecipeAdapter.OnRecipeClickListener {

    private RecyclerView rvFavorites;
    private TextView tvEmptyFavorites;
    private RecipeAdapter adapter;
    private ArrayList<Recipe> favoriteList;

    private ActivityResultLauncher<Intent> detailsLauncher;
    private int clickedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Receitas Favoritas");
        }

        // --- Encontra as Views ---
        rvFavorites = findViewById(R.id.rvFavorites);
        tvEmptyFavorites = findViewById(R.id.tvEmptyFavorites);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("FAVORITE_LIST")) {
            favoriteList = (ArrayList<Recipe>) intent.getSerializableExtra("FAVORITE_LIST");
        } else {
            favoriteList = new ArrayList<>();
            Log.e("FavoritesActivity", "Não foi possível carregar a lista de favoritos.");
        }

        adapter = new RecipeAdapter(this, favoriteList, this);
        rvFavorites.setLayoutManager(new LinearLayoutManager(this));
        rvFavorites.setAdapter(adapter);

        detailsLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.hasExtra("UPDATED_RECIPE") && clickedPosition != -1) {

                            Recipe updatedRecipe = (Recipe) data.getSerializableExtra("UPDATED_RECIPE");

                            if (updatedRecipe != null) {
                                if (updatedRecipe.isFavorite()) {
                                    favoriteList.set(clickedPosition, updatedRecipe);
                                    adapter.notifyItemChanged(clickedPosition);
                                } else {
                                    favoriteList.remove(clickedPosition);
                                    adapter.notifyItemRemoved(clickedPosition);


                                    checkEmptyList();
                                }
                            }
                            clickedPosition = -1;
                        }
                    }
                }
        );

        checkEmptyList();
    }


    private void checkEmptyList() {
        if (favoriteList.isEmpty()) {
            rvFavorites.setVisibility(View.GONE);
            tvEmptyFavorites.setVisibility(View.VISIBLE);
        } else {
            rvFavorites.setVisibility(View.VISIBLE);
            tvEmptyFavorites.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onRecipeClick(int position) {
        clickedPosition = position;
        Recipe clickedRecipe = favoriteList.get(position);
        Intent intent = new Intent(this, RecipeDetailsActivity.class);
        intent.putExtra("EXTRA_RECEITA", clickedRecipe);
        detailsLauncher.launch(intent);
    }
}