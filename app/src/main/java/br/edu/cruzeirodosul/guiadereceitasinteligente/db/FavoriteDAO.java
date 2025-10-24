package br.edu.cruzeirodosul.guiadereceitasinteligente.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import br.edu.cruzeirodosul.guiadereceitasinteligente.model.Favorite;

@Dao
public interface FavoriteDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Favorite favorite);

    @Delete
    void delete(Favorite favorite);

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_table WHERE title =:title LIMIT 1)")
    boolean isFavorite(String title);

    @Query("SELECT * FROM favorite_table")
    List<Favorite> getAllFavorites();
}
