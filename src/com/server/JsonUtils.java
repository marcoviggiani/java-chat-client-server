package com.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class JsonUtils {
    private static final Logger logger = LogManager.getLogger(JsonUtils.class);

    public static <T> void writeOnFileJson(ArrayList<T> arrayList, String filename) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (FileWriter writer = new FileWriter(filename)) {
            gson.toJson(arrayList, writer);
        } catch (IOException e) {
            logger.log(Level.ERROR, "Errore durante la scrittura del file JSON " + filename + "\n"+ e);
        }
    }

    public static <T> ArrayList<T> readOnFileJson(String filename, Class<T> typeClass) {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(filename)) {
            Type typeList = TypeToken.getParameterized(ArrayList.class, typeClass).getType();
            ArrayList<T> list = gson.fromJson(reader, typeList);
            return (list != null) ? list : new ArrayList<>();
        } catch (IOException e) {
            logger.log(Level.ERROR, "Errore durante la lettura del file JSON " + filename + "\n" + e);
            return new ArrayList<>();
        }
    }
}
