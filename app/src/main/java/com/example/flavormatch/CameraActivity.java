package com.example.flavormatch;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

public class CameraActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private Camera camera;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private float oldDist = 1f;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        // Inicializar Firebase Database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        surfaceView = findViewById(R.id.cameraFrame);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        Button captureButton = findViewById(R.id.captureButton);
        captureButton.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        REQUEST_CAMERA_PERMISSION);
            } else {
                openCamera();
            }
        });

        surfaceView.setOnTouchListener((v, event) -> {
            if (event.getPointerCount() > 1) {
                handleZoom(event);
            }
            return true;
        });

        getRecipesByIngredient("manzana");
    }

    private void handleZoom(MotionEvent event) {
        Camera.Parameters params = camera.getParameters();
        int maxZoom = params.getMaxZoom();
        int zoom = params.getZoom();
        float newDist = getFingerSpacing(event);
        if (newDist > oldDist) {
            // Zoom in
            if (zoom < maxZoom) {
                zoom++;
                params.setZoom(zoom);
                camera.setParameters(params);
            }
        } else if (newDist < oldDist) {
            // Zoom out
            if (zoom > 0) {
                zoom--;
                params.setZoom(zoom);
                camera.setParameters(params);
            }
        }
        oldDist = newDist;
    }

    private float getFingerSpacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private void openCamera() {
        try {
            camera = Camera.open();
            setCameraDisplayOrientation(this, Camera.CameraInfo.CAMERA_FACING_BACK, camera);
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getRecipesByIngredient(String ingredient) {
        mDatabase.child("recipes").orderByChild("ingredients").startAt(ingredient).endAt(ingredient + "\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot recipeSnapshot : dataSnapshot.getChildren()) {
                    MainActivity.Recipe recipe = recipeSnapshot.getValue(MainActivity.Recipe.class);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        if (camera != null) {
            try {
                camera.setPreviewDisplay(holder);
                camera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        if (surfaceHolder.getSurface() == null || camera == null) {
            return;
        }
        try {
            camera.stopPreview();
        } catch (Exception e) {
            // Ignorar error al intentar detener una vista previa inexistente
        }

        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    public static void setCameraDisplayOrientation(AppCompatActivity activity, int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }
}
