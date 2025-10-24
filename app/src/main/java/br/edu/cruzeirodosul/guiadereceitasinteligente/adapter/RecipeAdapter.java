package br.edu.cruzeirodosul.guiadereceitasinteligente.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            clickListener.onRecipeClick(position);
                        }
                    }
                }
            });
        }
    }
}