package pl.spicymobile.reactmobigate;

import android.support.annotation.Nullable;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.ReadableType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Utils {
    /**
     * Converts a react native readable map into a JSON object.
     *
     * @param readableMap map to convert to JSON Object
     * @return JSON Object that contains the readable map properties
     */
    @Nullable
    public static JSONObject readableMapToJson(ReadableMap readableMap) throws JSONException {
        JSONObject jsonObject = new JSONObject();

        if (readableMap == null) {
            return null;
        }

        ReadableMapKeySetIterator iterator = readableMap.keySetIterator();
        if (!iterator.hasNextKey()) {
            return null;
        }

        while (iterator.hasNextKey()) {
            String key = iterator.nextKey();
            ReadableType readableType = readableMap.getType(key);

            switch (readableType) {
                case Null:
                    jsonObject.put(key, null);
                    break;
                case Boolean:
                    jsonObject.put(key, readableMap.getBoolean(key));
                    break;
                case Number:
                    // Can be int or double.
                    jsonObject.put(key, readableMap.getInt(key));
                    break;
                case String:
                    jsonObject.put(key, readableMap.getString(key));
                    break;
                case Map:
                    jsonObject.put(key, readableMapToJson(readableMap.getMap(key)));
                    break;
                case Array:
                    jsonObject.put(key, convertArrayToJson(readableMap.getArray(key)));
                default:
                    // Do nothing and fail silently
            }
        }

        return jsonObject;
    }

    public static JSONArray convertArrayToJson(ReadableArray readableArray) throws JSONException {
        JSONArray array = new JSONArray();
        for (int i = 0; i < readableArray.size(); i++) {
            switch (readableArray.getType(i)) {
                case Null:
                    break;
                case Boolean:
                    array.put(readableArray.getBoolean(i));
                    break;
                case Number:
                    array.put(readableArray.getDouble(i));
                    break;
                case String:
                    array.put(readableArray.getString(i));
                    break;
                case Map:
                    array.put(readableMapToJson(readableArray.getMap(i)));
                    break;
                case Array:
                    array.put(convertArrayToJson(readableArray.getArray(i)));
                    break;
            }
        }
        return array;
    }

    public static int[] JSonArray2IntArray(JSONArray jsonArray){
        int[] intArray = new int[jsonArray.length()];
        for (int i = 0; i < intArray.length; ++i) {
            intArray[i] = jsonArray.optInt(i);
        }
        return intArray;
    }

    public static JSONArray intList2JSONArray(List<Integer> list){
        if(list != null && list.size()>0) {
            JSONArray jsArray = new JSONArray();
            for (int i = 0; i < list.size(); i++) {
                jsArray.put(list.get(i));
            }
            return jsArray;
        }else
            return new JSONArray();

    }
}
