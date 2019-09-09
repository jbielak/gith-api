package com.jbielak.githapi;

import com.google.gson.Gson;

public class TestUtils {

    public static <T> T jsonToObject(String json, Class<T> classOf) {
        Gson gson = new Gson();
        return gson.fromJson(json, classOf);
    }
}
