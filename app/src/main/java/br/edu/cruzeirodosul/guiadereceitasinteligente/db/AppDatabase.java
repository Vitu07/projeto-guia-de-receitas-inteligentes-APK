package br.edu.cruzeirodosul.guiadereceitasinteligente.db;


import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import br.edu.cruzeirodosul.guiadereceitasinteligente.model.Recipe;

@Database(entities =  {Recipe.class}, version =1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract  RecipeDAO recipeDAO();

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;

    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context){
        if(INSTANCE == null){
            synchronized (AppDatabase.class){
                if (INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "recipe_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }



    public static ArrayList<Recipe> getMockData(){
        ArrayList<Recipe> list = new ArrayList<>();
        list.add(new Recipe(
                "Bolo de Chocolate",
                "2 xícaras de farinha\n1 xícara de açúcar\n3 ovos\n1/2 xícara de óleo\n1 xícara de chocolate em pó\n1 colher de fermento.",
                "Passos:\n1. Misture os ingredientes secos.\n2. Adicione os ovos e o óleo.\n3. Bata tudo na batedeira.\n4. Asse por 40 minutos.",
                "https://bakespot.com.br/wp-content/uploads/2018/11/C%C3%B3pia-de-Bolo-Tradicional-de-Chocolate-sem-calda-1-600x400.jpg",
                "https://youtu.be/o0weSddcIO4",
                "Doces"
        ));
        list.add(new Recipe(
                "Macarrão ao Pesto",
                "500g de macarrão\n2 xícaras de manjericão\n1/2 xícara de parmesão\n1/4 xícara de pinoli\n2 dentes de alho\n1/2 xícara de azeite.",
                "1. Cozinhe o macarrão.\n2. Bata o manjericão, parmesão, pinoli, alho e azeite no processador.\n3. Misture o molho ao macarrão.",
                "https://cozinha365.com.br/wp-content/uploads/2025/02/Penne-ao-Molho-Pesto-S.webp",
                "https://youtu.be/tJBsiJmW4s8",
                "Salgados"
        ));
        list.add(new Recipe(
                "Vitamina de Banana",
                "2 bananas\n1 copo de leite\n2 colheres de aveia\n1 colher de mel.",
                "1. Coloque todos os ingredientes no liquidificador.\n2. Bata até ficar homogêneo.\n3. Sirva imediatamente.",
                "https://espaconatelie.com.br/wp-content/uploads/2024/03/vitamina-de-banana.jpg",
                "https://youtu.be/uvYJ235I2mg",
                "Bebidas"
        ));
        list.add(new Recipe(
                "Strogonoff de Frango",
                "1kg de peito de frango em cubos\n1 lata de creme de leite\n1/2 xícara de ketchup\n2 colheres de mostarda\n1 cebola picada\n2 dentes de alho picados\nSal e pimenta a gosto.",
                "1. Tempere o frango com sal e pimenta.\n2. Doure o alho e a cebola.\n3. Adicione o frango e frite até dourar.\n4. Adicione o ketchup e a mostarda, misture bem.\n5. Desligue o fogo e adicione o creme de leite.",
                "https://sabores-new.s3.amazonaws.com/public/2024/11/estrogonofe-pratico-de-frango-1.jpg",
                "https://youtu.be/qAcrlw_SSd4",
                "Salgados"
        ));
        list.add(new Recipe(
                "Brigadeiro",
                "1 lata de leite condensado\n1 colher de sopa de manteiga sem sal\n4 colheres de sopa de chocolate em pó\nChocolate granulado para enrolar.",
                "1. Em uma panela, misture o leite condensado, a manteiga e o chocolate em pó.\n2. Cozinhe em fogo baixo, mexendo sem parar, até desgrudar do fundo da panela (ponto de brigadeiro).\n3. Despeje em um prato untado e deixe esfriar.\n4. Enrole bolinhas e passe no chocolate granulado.",
                "https://admin.docepedia.com/site/uploads/2024/08/brigadeiros-vender-d.jpg",
                "https://youtu.be/1pUQ8uKMikw",
                "Doces"
        ));
        list.add(new Recipe(
                "Limonada Suíça",
                "2 limões Taiti com casca\n1/2 lata de leite condensado\n500ml de água gelada\nGelo a gosto.",
                "1. Lave bem os limões e corte as pontas.\n2. Corte os limões em 4 partes, retire a parte branca do meio.\n3. Bata no liquidificador os limões (com casca), a água e o gelo por cerca de 30 segundos.\n4. Coe a mistura.\n5. Volte o suco coado para o liquidificador, adicione o leite condensado e bata rapidamente só para misturar.\n6. Sirva imediatamente.",
                "https://cdn.casaeculinaria.com/wp-content/uploads/2024/04/29114609/Limonada-suica-1.webp",
                "https://youtu.be/wxld3E90lAQ",
                "Bebidas"
        ));
        list.add(new Recipe(
                "Omelete Simples",
                "2 ovos\nSal e pimenta do reino a gosto\n1 colher de chá de manteiga ou óleo.",
                "1. Em uma tigela, bata os ovos, sal e pimenta.\n2. Aqueça a manteiga ou óleo em uma frigideira antiaderente em fogo médio.\n3. Despeje a mistura de ovos na frigideira.\n4. Cozinhe por cerca de 2-3 minutos, ou até as bordas começarem a firmar.\n5. Com uma espátula, levante as bordas e incline a frigideira para o ovo líquido escorrer por baixo.\n6. Dobre a omelete ao meio e sirva.",
                "https://canaldareceita.com.br/wp-content/uploads/2025/09/Omelete-Simples-e-Rapida-1200x675.jpg",
                "https://youtu.be/yh6f7gKNtyY",
                "Salgados"
        ));
        return list;
    }


}
