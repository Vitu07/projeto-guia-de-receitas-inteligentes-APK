package br.edu.cruzeirodosul.guiadereceitasinteligente.model;

import java.io.Serializable;

public class Recipe implements Serializable {

    private String title;
    private String ingredients;
    private String steps;
    private String imageUrl;
    private String videoUrl;
    private String category;

    private boolean isFavorite = false;
    public Recipe(String title, String ingredients, String steps, String imageUrl, String videoUrl, String category) {
        this.title = title;
        this.ingredients = ingredients;
        this.steps = steps;
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public String getIngredients() {
        return ingredients;
    }

    public String getSteps() {
        return steps;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getCategory() {
        return category;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
