package com.example.flavormatch;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        findViewById(R.id.signInButton).setOnClickListener(view -> signIn());
        FirebaseApp.initializeApp(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        addRecipeToFirebase("Pie de Manzana", "Delicioso pie de manzana casero.", "harina, sal, azúcar, mantequilla, huevo, agua, manzanas, nuez moscada, canela, zumo de limón");
    }
    private void addRecipeToFirebase(String recipeName, String description, String ingredients) {
        String recipeId = mDatabase.child("recipes").push().getKey();
        Recipe recipe = new Recipe(recipeId, recipeName, description, ingredients);
        mDatabase.child("recipes").child(recipeId).setValue(recipe);
    }
    // Clase Recipe
    public static class Recipe {
        public String id;
        public String name;
        public String description;
        public String ingredients;

        public Recipe() {
            // Constructor por defecto necesario para llamadas a DataSnapshot.getValue(Recipe.class)
        }

        public Recipe(String id, String name, String description, String ingredients) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.ingredients = ingredients;
        }
    }
    private void getRecipesByIngredient(String ingredient) {
        mDatabase.child("recipes").orderByChild("ingredients").startAt(ingredient).endAt(ingredient + "\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot recipeSnapshot : dataSnapshot.getChildren()) {
                    Recipe recipe = recipeSnapshot.getValue(Recipe.class);
                    // Manejar la receta, por ejemplo, mostrar en la UI
                    Log.d("Recipe", "Nombre: " + recipe.name + ", Descripción: " + recipe.description);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejar posibles errores
                Log.w("getRecipesByIngredient", "loadPost:onCancelled", databaseError.toException());
            }
        });
    }


    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Iniciar la actividad de la cámara
            Log.d("MainActivity", "signInResult:success");
            Intent intent = new Intent(this, CameraActivity.class);
            startActivity(intent);
            finish();
        } catch (ApiException e) {
            Log.w("MainActivity", "signInResult:failed code=" + e.getStatusCode());
        }
    }
}