package br.edu.cruzeirodosul.guiadereceitasinteligente.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.activity.EdgeToEdge;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import br.edu.cruzeirodosul.guiadereceitasinteligente.R;
import br.edu.cruzeirodosul.guiadereceitasinteligente.adapter.RecipeAdapter;
import br.edu.cruzeirodosul.guiadereceitasinteligente.db.RecipeDAO;
import br.edu.cruzeirodosul.guiadereceitasinteligente.model.Recipe;

import br.edu.cruzeirodosul.guiadereceitasinteligente.db.AppDatabase;

public class FavoritesActivity extends AppCompatActivity implements RecipeAdapter.OnRecipeClickListener {

    private RecyclerView rvFavorites;
    private TextView tvEmptyFavorites;
    private RecipeAdapter adapter;
    private ArrayList<Recipe> favoriteList = new ArrayList<>();

    private ActivityResultLauncher<Intent> detailsLauncher;
    private int clickedPosition = -1;

    private RecipeDAO recipeDao;
    private ExecutorService databaseWriteExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_favorites);

        AppDatabase db = AppDatabase.getDatabase(this);
        recipeDao = db.recipeDAO();
        databaseWriteExecutor = AppDatabase.databaseWriteExecutor;

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.favorites_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.favorites_title));
        }

        rvFavorites = findViewById(R.id.rvFavorites);
        tvEmptyFavorites = findViewById(R.id.tvEmptyFavorites);

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
                                if (!updatedRecipe.isFavorite()) {
                                    favoriteList.remove(clickedPosition);
                                    adapter.notifyItemRemoved(clickedPosition);
                                    checkEmptyList();
                                } else {
                                    favoriteList.set(clickedPosition, updatedRecipe);
                                    adapter.notifyItemChanged(clickedPosition);
                                }
                            }
                            clickedPosition = -1;
                        }
                    }
                }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFavoritesFromDatabase();
    }

    private void loadFavoritesFromDatabase() {
        databaseWriteExecutor.execute(() -> {
            List<Recipe> favRecipes = recipeDao.getAllFavorites();

            runOnUiThread(() -> {
                favoriteList.clear();
                favoriteList.addAll(favRecipes);
                adapter.notifyDataSetChanged();
                checkEmptyList();
            });
        });
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
