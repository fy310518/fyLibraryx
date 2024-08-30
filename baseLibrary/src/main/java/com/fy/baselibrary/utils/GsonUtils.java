package com.fy.baselibrary.utils;

import android.util.ArrayMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Gson 工具类
 * Created by fangs on 2017/7/18.
 */
public class GsonUtils {

    private GsonUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 将bean转换成Json字符串
     * @param bean
     */
    public static String toJson(Object bean) {
        return new Gson().toJson(bean);
    }

    /**
     * 将 list 转换成json字符串
     * @return
     */
    public static <T> String listToJson(List<T> data){
        Gson gson = new Gson();

        return gson.toJson(data);
    }

    /**
     * 将 map 转换成 json字符串
     * @param params
     * @return
     */
    public static <T> String mapToJsonStr(Map<String, T> params){
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();

        return gson.toJson(params);
    }

    /**
     * 没有被 @Expose 标注的字段会被排除
     * @param bean
     */
    public static String toJsonExclude(Object bean) {
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();

        return gson.toJson(bean);
    }



    /**
     * 将Json字符串转换成对象
     * @param json
     * @param type
     */
    public static<T> T fromJson(String json, Class<T> type) {
        Gson gson = new GsonBuilder()
                .create();

        return gson.fromJson(json, type);
    }

    /**
     * 没有被 @Expose 标注的字段会被排除
     * @param json
     * @param type
     */
    public static<T> T fromJsonExclude(String json, Class<T> type) {
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        return gson.fromJson(json, type);
    }

    /**
     * 将Json字符串转换成对象
     * @param json
     * @param typeOfT  Type type = new TypeToken<BeanModule<CaseInfoBean>>(){}.getType();
     */
    public static<T> T fromJson(String json, Type typeOfT) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(new TypeToken<ArrayMap<String,Object>>(){}.getType(), new ObjectTypeAdapterRewrite())
                .create();

        return gson.fromJson(json, typeOfT);
    }

    /**
     * json字符串 转换成 json对象
     * @param jsonStr
     * @return
     */
    public static JsonObject jsonStrToJsonObj(String jsonStr){
        JsonObject returnData = null;
        try {
            returnData = JsonParser.parseString(jsonStr).getAsJsonObject();
        } catch (Exception e) {
            e.printStackTrace();
            returnData = new JsonObject();
        }

        return returnData;
    }

    /**
     * Json字符串转 对象 【list ， map 等均可】
     */
    public static<T> T jsonToObj(String jsonStr, Type typeOfT) {
        Gson gson = new GsonBuilder().serializeNulls().disableHtmlEscaping().create();

        return gson.fromJson(jsonStr, typeOfT);
    }

    /**
     * 将Json字符串转换成 List集合
     * @param jsonStr
     * @param <T>
     * @return
     */
    public static <T> List<T> jsonToList(String jsonStr, Class<T> clazz) {
        List<T> lst = new ArrayList<>();

        try {
            JsonArray array = JsonParser.parseString(jsonStr).getAsJsonArray();
            for (final JsonElement elem : array) {
                lst.add(new Gson().fromJson(elem, clazz));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return lst;
    }

    public static <T> List<T> jsonToList(String jsonStr, Type typeOfT) {
        List<T> lst = new ArrayList<>();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(new TypeToken<ArrayMap<String,Object>>(){}.getType(), new ObjectTypeAdapterRewrite())
                .create();

        try {
            JsonArray array = JsonParser.parseString(jsonStr).getAsJsonArray();
            for (final JsonElement elem : array) {
                lst.add(gson.fromJson(elem, typeOfT));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return lst;
    }




    public static class ObjectTypeAdapterRewrite extends TypeAdapter<Object> {
        private final TypeAdapter<Object> delegate = new Gson().getAdapter(Object.class);

        @Override
        public Object read(JsonReader in) throws IOException {
            JsonToken token = in.peek();
            switch (token) {
                case BEGIN_ARRAY:
                    List<Object> list = new ArrayList<>();
                    in.beginArray();
                    while (in.hasNext()) {
                        list.add(read(in));
                    }
                    in.endArray();
                    return list;

                case BEGIN_OBJECT:
                    ArrayMap<String, Object> map = new ArrayMap<>();
                    in.beginObject();
                    while (in.hasNext()) {
                        map.put(in.nextName(), read(in));
                    }
                    in.endObject();
                    return map;

                case STRING:
                    return in.nextString();

                case NUMBER:
                    /**
                     * 改写数字的处理逻辑，将数字值分为整型与浮点型。
                     */
                    double dbNum = in.nextDouble();

                    // 数字超过long的最大值，返回浮点类型
                    if (dbNum > Long.MAX_VALUE) {
                        return dbNum;
                    }
                    // 判断数字是否为整数值
                    long lngNum = (long) dbNum;
                    if (dbNum == lngNum) {
                        try {
                            return (int) lngNum;
                        } catch (Exception e) {
                            return lngNum;
                        }
                    } else {
                        return dbNum;
                    }

                case BOOLEAN:
                    return in.nextBoolean();

                case NULL:
                    in.nextNull();
                    return null;

                default:
                    throw new IllegalStateException();
            }
        }

        @Override
        public void write(JsonWriter out, Object value) throws IOException {
            delegate.write(out,value);
        }
    }

}
