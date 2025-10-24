package br.edu.cruzeirodosul.guiadereceitasinteligente.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import br.edu.cruzeirodosul.guiadereceitasinteligente.R;
import br.edu.cruzeirodosul.guiadereceitasinteligente.model.Recipe;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private Context context;
    private ArrayList<Recipe> recipeList;
    private OnRecipeClickListener clickListener;

    public interface OnRecipeClickListener {
        void onRecipeClick(int position);
    }

    public RecipeAdapter(Context context, ArrayList<Recipe> recipeList, OnRecipeClickListener clickListener) {
        this.context = context;
        this.recipeList = recipeList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_recipe, parent, false);
        return new RecipeViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);

        holder.tvRecipeName.setText(recipe.getTitle());
        holder.tvRecipeCategory.setText(recipe.getCategory());

        ColorStateList tagColor;
        ColorStateList textColor;

        if ("Doces".equalsIgnoreCase(recipe.getCategory())) {
            tagColor = ContextCompat.getColorStateList(context, R.color.tag_doces_bg);
            textColor = ContextCompat.getColorStateList(context, R.color.tag_doces_text);
        } else if ("Salgados".equalsIgnoreCase(recipe.getCategory())) {
            tagColor = ContextCompat.getColorStateList(context, R.color.tag_salgados_bg);
            textColor = ContextCompat.getColorStateList(context, R.color.tag_salgados_text);
        } else if ("Bebidas".equalsIgnoreCase(recipe.getCategory())) {
            tagColor = ContextCompat.getColorStateList(context, R.color.tag_bebidas_bg);
            textColor = ContextCompat.getColorStateList(context, R.color.tag_bebidas_text);
        } else {
            tagColor = ContextCompat.getColorStateList(context, R.color.tag_default_bg);
            textColor = ContextCompat.getColorStateList(context, R.color.tag_default_text);
        }

        holder.tvRecipeCategory.setBackgroundTintList(tagColor);
        holder.tvRecipeCategory.setTextColor(textColor);

        Glide.with(context)
                .load(recipe.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(holder.ivRecipeThumbnail);

        if (recipe.isFavorite()) {
            holder.ivFavoriteIcon.setImageResource(R.drawable.ic_favorite_filled);
            holder.ivFavoriteIcon.setVisibility(View.VISIBLE);
        } else {
            holder.ivFavoriteIcon.setImageResource(R.drawable.ic_favorite_border);
            holder.ivFavoriteIcon.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {

        ImageView ivRecipeThumbnail;
        TextView tvRecipeName;
        TextView tvRecipeCategory;
        ImageView ivFavoriteIcon;

        public RecipeViewHolder(@NonNull View itemView, OnRecipeClickListener clickListener) {
            super(itemView);

            ivRecipeThumbnail = itemView.findViewById(R.id.ivRecipeThumbnail);
            tvRecipeName = itemView.findViewById(R.id.tvRecipeName);
            tvRecipeCategory = itemView.findViewById(R.id.tvRecipeCategory);
            ivFavoriteIcon = itemView.findViewById(R.id.ivFavoriteIcon);

            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        clickListener.onRecipeClick(pos);
                    }
                }
            });
        }
    }
}