package com.facticoapp.nuup.parser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public class GsonParser {
    private static final String TAG = GsonParser.class.getName();

    public static Object getObjectFromJSON(String json, Class c) {
        Object object = null;
        try {
            Gson gson = new Gson();
            object = gson.fromJson(json, c);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return object;
    }

    /*public static List<Item> getListItemsFromJSON(String json) {
        List<Item> items = null;
        try {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Item>>() {}.getType();
            items = gson.fromJson(json, listType);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return items;
    }*/

    public static String createJsonFromObject(Object object) {
        Gson gson = new Gson();
        return gson.toJson(object);
    }

    public static String createJsonFromObjectWithExposeAnnotations(Object object) {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(object);
    }
}
