package br.edu.cruzeirodosul.guiadereceitasinteligente.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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


public class MainActivity extends AppCompatActivity implements RecipeAdapter.OnRecipeClickListener {

    private RecyclerView rvRecipes;
    private RecipeAdapter adapter;
    private ArrayList<Recipe> recipeList = new ArrayList<>();
    private ActivityResultLauncher<Intent> detailsLauncher;
    private int clickedPosition = -1;

    private RecipeDAO recipeDao;
    private ExecutorService databaseWriteExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        AppDatabase db = AppDatabase.getDatabase(this);
        recipeDao = db.recipeDAO();
        databaseWriteExecutor = AppDatabase.databaseWriteExecutor;

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupRecyclerView();

        detailsLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.hasExtra("UPDATED_RECIPE") && clickedPosition != -1) {

                            Recipe updatedRecipe = (Recipe) data.getSerializableExtra("UPDATED_RECIPE");

                            if (updatedRecipe != null) {
                                recipeList.set(clickedPosition, updatedRecipe);
                                adapter.notifyItemChanged(clickedPosition);
                            }
                            clickedPosition = -1;
                        }
                    }
                }
        );

        loadRecipesFromDatabase();
    }

    private void setupRecyclerView() {
        rvRecipes = findViewById(R.id.rvRecipes);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvRecipes.setLayoutManager(layoutManager);

        adapter = new RecipeAdapter(this, recipeList, this);
        rvRecipes.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!recipeList.isEmpty()) {
            loadRecipesFromDatabase();
        }
    }

    private void loadRecipesFromDatabase() {
        databaseWriteExecutor.execute(() -> {

            if (recipeDao.getRecipeCount() == 0) {
                Log.d("MainActivity", "Banco de dados vazio. Populando...");
                List<Recipe> mockRecipes = AppDatabase.getMockData();
                recipeDao.insertAll(mockRecipes);
                Log.d("MainActivity", "Banco de dados populado.");
            }

            List<Recipe> allRecipes = recipeDao.getAllRecipes();

            runOnUiThread(() -> {
                recipeList.clear();
                recipeList.addAll(allRecipes);
                adapter.notifyDataSetChanged();
            });
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id ==  R.id.action_favorites){
            Intent intent = new Intent(this, FavoritesActivity.class);
            startActivity(intent);
            return true;
        }

        if(id == R.id.action_about){
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.about_title))
                    .setMessage(getString(R.string.about_message))
                    .setPositiveButton(getString(R.string.dialog_ok), null)
                    .show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRecipeClick(int position) {
        clickedPosition = position;
        Recipe clickedRecipe = recipeList.get(position);
        Intent intent = new Intent(this, RecipeDetailsActivity.class);
        intent.putExtra("EXTRA_RECEITA", clickedRecipe);

        detailsLauncher.launch(intent);
    }
}