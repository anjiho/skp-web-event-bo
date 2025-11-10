package kr.co.syrup.adreport.framework.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * JsonSting 파싱 유틸
 * <PRE>
 *     1. JsonString : { "key" : "value", "key": "value", .... }
 *     1. 사용법 : Object obj = GsonUtils.method(jsonStrObject, JsonKey);
 *     2. 작성자 : 안지호
 * </PRE>
 */
public class GsonUtils {

    /**
     * JsonString > String 파싱
     * @param jsonStr
     * @param property
     * @return
     */
    public static String parseStringJsonStr(String jsonStr, String property) {
        if (jsonStr == null || "".equals(jsonStr)) return null;

        Gson gson = new Gson();

        JsonElement element = gson.fromJson(jsonStr, JsonElement.class);
        JsonObject jsonObject = element.getAsJsonObject();
        JsonElement jsonElement = jsonObject.get(property);

        if (jsonElement != null) {
            if (jsonElement.isJsonNull()) {
                return "";
            } else {
                return jsonElement.getAsString();
            }
        } else {
            return null;
        }
    }

    /**
     * JsonString > Integer 파싱
     * @param jsonStr
     * @param property
     * @return
     */
    public static Integer parseIntJsonStr(String jsonStr, String property) {
        if (jsonStr == null || "".equals(jsonStr)) return null;

        Gson gson = new Gson();

        JsonElement element = gson.fromJson(jsonStr, JsonElement.class);
        JsonObject jsonObject = element.getAsJsonObject();
        JsonElement jsonElement = jsonObject.get(property);

        if (jsonElement != null) {
            if (jsonElement.isJsonNull()) {
                return 0;
            } else {
                return jsonElement.getAsInt();
            }
        } else {
            return null;
        }
    }

    /**
     * JsonString > JsonArray 파싱
     * @param jsonStr
     * @param property
     * @return
     */
    public static JsonArray parseJsonArrayJsonStr(String jsonStr, String property) {
        if (jsonStr == null || "".equals(jsonStr)) return null;

        Gson gson = new Gson();

        JsonElement element = gson.fromJson(jsonStr, JsonElement.class);
        JsonObject jsonObject = element.getAsJsonObject();
        JsonElement jsonElement = jsonObject.get(property);

        if (jsonElement != null) {
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            return jsonArray;
        } else {
            return null;
        }
    }

    public static <T> List<T> getObjectFromJsonArray(JsonArray jsonArray, Class<T> type) {
        return new Gson().fromJson(jsonArray.toString(), new ListOf<>(type));
    }

    /**
     * Object to JsonString 변환
     * @param object
     * @return
     */
    public static String getJsonStringAsObject(Object object) {
        if (object != null) {
            Gson gson = new Gson();
            return gson.toJson(object);
        }
        return null;
    }

    public static Boolean parseBooleanJsonStr(String jsonStr, String property) {
        if (PredicateUtils.isNull(jsonStr)) {
            return null;
        }
        Gson gson = new Gson();

        JsonElement element = gson.fromJson(jsonStr, JsonElement.class);
        JsonObject jsonObject = element.getAsJsonObject();
        JsonElement jsonElement = jsonObject.get(property);

        if (jsonElement != null) {
            if (jsonElement.isJsonNull()) {
                return null;
            } else {
                return jsonElement.getAsBoolean();
            }
        } else {
            return null;
        }
    }

    /**
     * JsonString > Integer 파싱
     * @param jsonStr
     * @param property
     * @return
     */
    public static Long parseLongFromJsonStr(String jsonStr, String property) {
        if (jsonStr == null || "".equals(jsonStr)) return null;

        Gson gson = new Gson();

        JsonElement element = gson.fromJson(jsonStr, JsonElement.class);
        JsonObject jsonObject = element.getAsJsonObject();
        JsonElement jsonElement = jsonObject.get(property);

        if (jsonElement != null) {
            if (jsonElement.isJsonNull()) {
                return null;
            } else {
                return jsonElement.getAsLong();
            }
        } else {
            return null;
        }
    }

    public static <T> T fromJson(String jsonStr, Class<T> classOfT) {
        return new Gson().fromJson(jsonStr, classOfT);
    }

}
