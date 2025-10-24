package br.edu.cruzeirodosul.guiadereceitasinteligente.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.util.Log;
import android.widget.ImageView;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import br.edu.cruzeirodosul.guiadereceitasinteligente.R;
import br.edu.cruzeirodosul.guiadereceitasinteligente.db.FavoriteDAO;
import br.edu.cruzeirodosul.guiadereceitasinteligente.model.Recipe;

import br.edu.cruzeirodosul.guiadereceitasinteligente.db.AppDatabase;
import br.edu.cruzeirodosul.guiadereceitasinteligente.model.Favorite;
import java.util.concurrent.ExecutorService;


public class RecipeDetailsActivity extends AppCompatActivity {

    private TextView tvName;
    private TextView tvCategory;
    private TextView tvIngredients;
    private TextView tvPreparationMethod;
    private ImageView ivRecipeImage;
    private FloatingActionButton fabFavorite;
    private Recipe recipe;
    private Button btnWatchVideo;

    private FavoriteDAO favoriteDao;
    private ExecutorService databaseWriteExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        AppDatabase db = AppDatabase.getDatabase(this);
        favoriteDao = db.favoriteDAO();
        databaseWriteExecutor = AppDatabase.databaseWriteExecutor;

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        tvName = findViewById(R.id.tvRecipeNameDetail);
        tvCategory = findViewById(R.id.tvRecipeCategoryDetail);
        tvIngredients = findViewById(R.id.tvRecipeIngredientsDetail);
        tvPreparationMethod = findViewById(R.id.tvRecipeInstructionsDetail);
        ivRecipeImage = findViewById(R.id.ivRecipeImageDetail);
        fabFavorite = findViewById(R.id.fabFavorite);
        btnWatchVideo = findViewById(R.id.btnWatchVideo);


        Intent intent = getIntent();
        final String EXTRA_KEY = "EXTRA_RECEITA";

        if (intent != null && intent.hasExtra(EXTRA_KEY)){
            this.recipe = (Recipe) intent.getSerializableExtra(EXTRA_KEY);

            if(recipe != null){

                if (actionBar != null){
                    actionBar.setTitle(recipe.getTitle());
                }

                tvName.setText(recipe.getTitle());
                tvCategory.setText(recipe.getCategory());

                if (recipe.getIngredients() != null) {
                    tvIngredients.setText(recipe.getIngredients());
                } else {
                    tvIngredients.setText("Ingredientes não disponíveis.");
                }
                if (recipe.getSteps() != null){
                    tvPreparationMethod.setText(recipe.getSteps());
                }else{
                    tvPreparationMethod.setText("Modo de preparo não disponível.");
                }
                Glide.with(this)
                        .load(recipe.getImageUrl())
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background)
                        .into(ivRecipeImage);

                updateHeartIcon();

                fabFavorite.setOnClickListener(v -> {
                    recipe.setFavorite(!recipe.isFavorite());
                    updateHeartIcon();
                    updateFavoriteInDatabase();
                });

                final String videoUrl = recipe.getVideoUrl();
                if (videoUrl != null && !videoUrl.isEmpty()) {
                    btnWatchVideo.setVisibility(View.VISIBLE);
                    btnWatchVideo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent videoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
                            startActivity(videoIntent);
                        }
                    });
                }

            } else {
                Log.e("RecipeDetailActivity", "Erro: Objeto Recipe é nulo.");
                if (actionBar != null) actionBar.setTitle("Erro");
                tvName.setText("Erro ao carregar receita");
            }

        } else {
            Log.e("RecipeDetailActivity", "Erro: Intent não contém a chave 'EXTRA_RECEITA'.");
            if (actionBar != null) actionBar.setTitle("Erro");
            tvName.setText("Erro ao carregar receita");
        }
    }

    private void updateFavoriteInDatabase() {
        if (recipe == null) return;

        final Favorite favorite = new Favorite(recipe.getTitle());

        databaseWriteExecutor.execute(() -> {
            if (recipe.isFavorite()) {
                favoriteDao.insert(favorite);
                Log.d("DB_UPDATE", "Inserido: " + favorite.getTitle());
            } else {
                favoriteDao.delete(favorite);
                Log.d("DB_UPDATE", "Deletado: " + favorite.getTitle());
            }
        });
    }

    private void updateHeartIcon() {
        if (recipe == null) {
            fabFavorite.setImageResource(R.drawable.ic_favorite_border);
            return;
        }

        if (recipe.isFavorite()) {
            fabFavorite.setImageResource(R.drawable.ic_favorite_filled);
        } else {
            fabFavorite.setImageResource(R.drawable.ic_favorite_border);
        }
    }

    @Override
    public void finish() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("UPDATED_RECIPE", recipe);
        setResult(RESULT_OK, resultIntent);
        super.finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}