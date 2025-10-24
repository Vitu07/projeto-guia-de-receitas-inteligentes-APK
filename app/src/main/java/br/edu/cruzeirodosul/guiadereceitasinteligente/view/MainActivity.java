package br.edu.cruzeirodosul.guiadereceitasinteligente.view;

import android.content.Intent;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import br.edu.cruzeirodosul.guiadereceitasinteligente.R;
import br.edu.cruzeirodosul.guiadereceitasinteligente.adapter.RecipeAdapter;
import br.edu.cruzeirodosul.guiadereceitasinteligente.db.FavoriteDAO;
import br.edu.cruzeirodosul.guiadereceitasinteligente.model.Recipe;

import br.edu.cruzeirodosul.guiadereceitasinteligente.db.AppDatabase;
import br.edu.cruzeirodosul.guiadereceitasinteligente.model.Favorite;


public class MainActivity extends AppCompatActivity implements RecipeAdapter.OnRecipeClickListener {

    private RecyclerView rvRecipes;
    private RecipeAdapter adapter;
    private ArrayList<Recipe> recipeList = new ArrayList<>();
    private ActivityResultLauncher<Intent> detailsLauncher;
    private int clickedPosition = -1;

    private FavoriteDAO favoriteDao;
    private ExecutorService databaseWriteExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppDatabase db = AppDatabase.getDatabase(this);
        favoriteDao = db.favoriteDAO();
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
        loadAndSyncData();
    }

    private void loadAndSyncData() {
        databaseWriteExecutor.execute(() -> {
            ArrayList<Recipe> rawList = getMockData();
            List<Favorite> favoritesFromDb = favoriteDao.getAllFavorites();

            Set<String> favoriteTitles = new HashSet<>();
            for (Favorite f : favoritesFromDb) {
                favoriteTitles.add(f.getTitle());
            }

            for (Recipe recipe : rawList) {
                if (favoriteTitles.contains(recipe.getTitle())) {
                    recipe.setFavorite(true);
                } else {
                    recipe.setFavorite(false);
                }
            }

            runOnUiThread(() -> {
                recipeList.clear();
                recipeList.addAll(rawList);
                adapter.notifyDataSetChanged();
            });
        });
    }

    private ArrayList<Recipe> getMockData(){
        ArrayList<Recipe> list = new ArrayList<>();
        list.add(new Recipe(
                "Bolo de Chocolate",
                "Ingredientes: 2 xícaras de farinha, 1 xícara de açúcar, 3 ovos, 1/2 xícara de óleo, 1 xícara de chocolate em pó, 1 colher de fermento.",
                "Passos: 1. Misture os ingredientes secos. 2. Adicione os ovos e o óleo. 3. Bata tudo na batedeira. 4. Asse por 40 minutos.",
                "https://bakespot.com.br/wp-content/uploads/2018/11/C%C3%B3pia-de-Bolo-Tradicional-de-Chocolate-sem-calda-1-600x400.jpg",
                "https://youtu.be/o0weSddcIO4",
                "Doces"
        ));
        list.add(new Recipe(
                "Macarrão ao Pesto",
                "Ingredientes: 500g de macarrão, 2 xícaras de manjericão, 1/2 xícara de parmesão, 1/4 xícara de pinoli, 2 dentes de alho, 1/2 xícara de azeite.",
                "Passos: 1. Cozinhe o macarrão. 2. Bata o manjericão, parmesão, pinoli, alho e azeite no processador. 3. Misture o molho ao macarrão.",
                "https://cozinha365.com.br/wp-content/uploads/2025/02/Penne-ao-Molho-Pesto-S.webp",
                "https://youtu.be/tJBsiJmW4s8",
                "Salgados"
        ));
        list.add(new Recipe(
                "Vitamina de Banana",
                "Ingredientes: 2 bananas, 1 copo de leite, 2 colheres de aveia, 1 colher de mel.",
                "Passos: 1. Coloque todos os ingredientes no liquidificador. 2. Bata até ficar homogêneo. 3. Sirva imediatamente.",
                "https://espaconatelie.com.br/wp-content/uploads/2024/03/vitamina-de-banana.jpg",
                "https://www.youtube.com/watch?v=0kYYsAD-m9Q",
                "Bebidas"
        ));
        return list;
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
            ArrayList<Recipe> favoriteList = new ArrayList<>();
            for (Recipe recipe : recipeList) {
                if (recipe.isFavorite()) {
                    favoriteList.add(recipe);
                }
            }

            Intent intent = new Intent(this, FavoritesActivity.class);
            intent.putExtra("FAVORITE_LIST", favoriteList);
            startActivity(intent);

            return true;
        }

        if(id == R.id.action_about){
            new AlertDialog.Builder(this)
                    .setTitle("Sobre")
                    .setMessage("Guia de Receitas Inteligente\nVersão 1.0")
                    .setPositiveButton("OK", null)
                    .show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRecipeClick(int position) {
        clickedPosition = position;

        Recipe clickedRecipe = recipeList.get(position);
        Intent intent = new Intent(this, br.edu.cruzeirodosul.guiadereceitasinteligente.view.RecipeDetailsActivity.class);
        intent.putExtra("EXTRA_RECEITA", clickedRecipe);

        detailsLauncher.launch(intent);
    }
}