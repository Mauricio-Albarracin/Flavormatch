package com.example.flavormatch;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class RecipeListActivity extends AppCompatActivity {

    private Button button1;
    private Button button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);

        // Obtener los ingredientes detectados
        List<String> detectedIngredients = getIntent().getStringArrayListExtra("DETECTED_INGREDIENTS");

        // Cargar recetas basadas en los ingredientes detectados
        loadRecipes(detectedIngredients);

        button1.setOnClickListener(view -> {
            // Acción al presionar el botón 1 (puedes cambiar la lógica según necesites)
        });

        button2.setOnClickListener(view -> {
            // Acción al presionar el botón 2 (puedes cambiar la lógica según necesites)
        });
    }

    private void loadRecipes(List<String> ingredients) {
        // Implementar lógica para cargar recetas desde la base de datos
        // y actualizar los botones con las recetas cargadas.
        // Ejemplo simplificado:
        if (ingredients != null && !ingredients.isEmpty()) {
            button1.setText("Receta 1: " + ingredients.get(0));
            if (ingredients.size() > 1) {
                button2.setText("Receta 2: " + ingredients.get(1));
            }
        } else {
            button1.setText("Receta 1");
            button2.setText("Receta 2");
        }
    }
}
