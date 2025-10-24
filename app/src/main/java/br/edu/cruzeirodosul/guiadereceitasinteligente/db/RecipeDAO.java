package br.edu.cruzeirodosul.guiadereceitasinteligente.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import br.edu.cruzeirodosul.guiadereceitasinteligente.model.Recipe;

@Dao
public interface RecipeDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<Recipe> recipes);

    @Query("SELECT * FROM recipe_table ORDER BY title ASC")
    List<Recipe> getAllRecipes();

    @Query("SELECT * FROM recipe_table WHERE isFavorite = 1 ORDER BY title ASC")
    List<Recipe> getAllFavorites();

    @Query("UPDATE recipe_table SET isFavorite = :isFavorite WHERE title = :title")
    void updateFavoriteStatus(String title, boolean isFavorite);

    @Query("SELECT COUNT(*) FROM recipe_table")
    int getRecipeCount();
}
