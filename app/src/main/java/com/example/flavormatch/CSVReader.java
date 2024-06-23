package com.example.flavormatch;

import android.content.Context;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSVReader {

    public static Map<String, List<String>> readCSV(Context context, int resourceId) {
        Map<String, List<String>> data = new HashMap<>();
        InputStream inputStream = context.getResources().openRawResource(resourceId);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    String key = parts[0];
                    List<String> values = new ArrayList<>();
                    for (int i = 1; i < parts.length; i++) {
                        values.add(parts[i]);
                    }
                    data.put(key, values);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return data;
    }
}
