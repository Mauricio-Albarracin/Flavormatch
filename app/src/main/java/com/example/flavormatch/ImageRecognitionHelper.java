package com.example.flavormatch;

import android.content.Context;
import android.graphics.Bitmap;
import java.util.ArrayList;
import java.util.List;

public class ImageRecognitionHelper {

    private Context context;

    public ImageRecognitionHelper(Context context) {
        this.context = context;
    }

    public List<String> recognizeImage(Bitmap bitmap) {
        // Aquí iría la lógica para reconocer los ingredientes en la imagen
        // Por ahora, solo devolveremos una lista de ejemplo
        List<String> ingredients = new ArrayList<>();
        ingredients.add("Tomate");
        ingredients.add("Queso");
        ingredients.add("Albahaca");
        return ingredients;
    }
}
