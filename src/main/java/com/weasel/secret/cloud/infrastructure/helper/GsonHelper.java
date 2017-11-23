package com.weasel.secret.cloud.infrastructure.helper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by dell on 2017/11/23.
 */
public final class GsonHelper {

    public final static Gson gson = new GsonBuilder().create();

    /**
     *
     * @param object
     * @return
     */
    public static String toJson(Object object){
        return gson.toJson(object);
    }

    /**
     *
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T fromJson(String json,Class<T> clazz){
        return gson.fromJson(json,clazz);
    }

    protected GsonHelper(){}
}
