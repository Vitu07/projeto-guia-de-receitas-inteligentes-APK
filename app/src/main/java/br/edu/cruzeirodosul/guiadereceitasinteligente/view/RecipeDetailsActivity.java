package br.edu.cruzeirodosul.guiadereceitasinteligente.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.util.Log;
import android.widget.ImageView;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.activity.EdgeToEdge;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Locale;
import br.edu.cruzeirodosul.guiadereceitasinteligente.R;
import br.edu.cruzeirodosul.guiadereceitasinteligente.db.RecipeDAO;
import br.edu.cruzeirodosul.guiadereceitasinteligente.model.Recipe;
import br.edu.cruzeirodosul.guiadereceitasinteligente.db.AppDatabase;
import java.util.concurrent.ExecutorService;

public class RecipeDetailsActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private TextView tvName;
    private TextView tvCategory;
    private TextView tvIngredients;
    private TextView tvPreparationMethod;
    private ImageView ivRecipeImage;
    private FloatingActionButton fabFavorite;
    private FloatingActionButton fabPlaySpeech;
    private Recipe recipe;
    private Button btnWatchVideo;

    private RecipeDAO recipeDao;
    private ExecutorService databaseWriteExecutor;

    private TextToSpeech tts;
    private boolean ttsInitialized = false;
    private String textToSpeak = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recipe_details);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.details_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tts = new TextToSpeech(this, this);

        AppDatabase db = AppDatabase.getDatabase(this);
        recipeDao = db.recipeDAO();
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
        fabPlaySpeech = findViewById(R.id.fabPlaySpeech);

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
                    tvIngredients.setText(getString(R.string.error_ingredients_not_available));
                }
                if (recipe.getSteps() != null){
                    tvPreparationMethod.setText(recipe.getSteps());
                }else{
                    tvPreparationMethod.setText(getString(R.string.error_steps_not_available));
                }

                String ingredientsText = recipe.getIngredients() != null ? recipe.getIngredients() : "";
                String stepsText = recipe.getSteps() != null ? recipe.getSteps() : "";

                String cleanIngredients = sanitizeTextForSpeech(ingredientsText);
                String cleanSteps = sanitizeTextForSpeech(stepsText);

                textToSpeak = getString(R.string.tts_label_ingredients) + ": " + cleanIngredients +
                        ". ... " + getString(R.string.tts_label_steps) + ": " + cleanSteps;

                Glide.with(this)
                        .load(recipe.getImageUrl())
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background)
                        .into(ivRecipeImage);

                fabPlaySpeech.setOnClickListener(v -> {
                    speakOut();
                });

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
                if (actionBar != null) actionBar.setTitle(getString(R.string.error_title));
                tvName.setText(getString(R.string.error_loading_recipe));
            }

        } else {
            Log.e("RecipeDetailActivity", "Erro: Intent não contém a chave 'EXTRA_RECEITA'.");
            if (actionBar != null) actionBar.setTitle(getString(R.string.error_title));
            tvName.setText(getString(R.string.error_loading_recipe));
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(new Locale("pt", "BR"));
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", getString(R.string.tts_error_lang_not_supported));
                tts.setLanguage(Locale.getDefault());
            }
            ttsInitialized = true;
        } else {
            Log.e("TTS", getString(R.string.tts_error_init_failed));
            ttsInitialized = false;
        }
    }

    private void speakOut() {
        if (!ttsInitialized) {
            Log.e("TTS", getString(R.string.tts_error_not_initialized));
            return;
        }

        if (tts.isSpeaking()) {
            tts.stop();
        } else {
            tts.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    private void updateFavoriteInDatabase() {
        if (recipe == null) return;

        databaseWriteExecutor.execute(() -> {
            recipeDao.updateFavoriteStatus(recipe.getTitle(), recipe.isFavorite());
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

    private String sanitizeTextForSpeech(String text) {
        if (text == null) {
            return "";
        }
        String newText = text.replaceAll("1/2", "meia");
        newText = newText.replaceAll("1/4", "um quarto");
        newText = newText.replaceAll("3/4", "três quartos");
        newText = newText.replaceAll("°C", " graus Celsius");
        newText = newText.replaceAll("°", " graus");
        newText = newText.replaceAll("%", " por cento");
        newText = newText.replaceAll("/", " ");

        return newText;
    }
}
