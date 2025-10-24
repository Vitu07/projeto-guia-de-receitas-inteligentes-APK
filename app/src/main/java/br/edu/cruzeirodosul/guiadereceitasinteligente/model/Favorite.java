package br.edu.cruzeirodosul.guiadereceitasinteligente.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName ="favorite_table")
public class Favorite {

    @PrimaryKey
    @NonNull
    private String title;

    public Favorite(@NonNull String title) {
        this.title = title;
    }

    @NonNull
    public String getTitle() {
        return title;
    }
}
