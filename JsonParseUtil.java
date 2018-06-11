package com.example.demo;

/**
 * Created by 张城城 on 2018/6/11.
 */
import com.google.gson.*;
import net.sf.json.JSONObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 多层嵌套json数据转换为单层，同时规格化
 **/
public class JsonParseUtil {
    /**
     *把拍平后的json进行格式化处理，输出标准的json格式
     * @param uglyJSONString
     * @return
     */
    public static String jsonFormatter(String uglyJSONString){

        Map<String,Object> map = new HashMap<>();
        parseJson2Map(map,uglyJSONString,null);
        JSONObject jsonObject = JSONObject.fromObject(map);
        uglyJSONString = jsonObject.toString();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(uglyJSONString);
        String prettyJsonString = gson.toJson(je);
        ///System.out.println(prettyJsonString);
        return prettyJsonString;
    }

    public static void parseJson2Map(Map map,JsonObject jsonObject,String parentKey){
        for (Map.Entry<String, JsonElement> object : jsonObject.entrySet()) {
            String key = object.getKey();
            JsonElement value = object.getValue();
            String fullkey = (null == parentKey || parentKey.trim().equals("")) ? key : parentKey.trim() + "." + key;
            //判断对象的类型，如果是空类型则安装空类型处理
            if (value.isJsonNull()){
                map.put(fullkey,null);
                continue;
            //如果是JsonObject对象则递归处理
            }else if (value.isJsonObject()){
                parseJson2Map(map,value.getAsJsonObject(),fullkey);
            //如果是JsonArray数组则迭代，然后进行递归
            }else if (value.isJsonArray()){
                JsonArray jsonArray = value.getAsJsonArray();
                Iterator<JsonElement> iterator = jsonArray.iterator();
                while (iterator.hasNext()) {
                    JsonElement jsonElement1 = iterator.next();
                    parseJson2Map(map, jsonElement1.getAsJsonObject(), fullkey);
                }
                continue;
             // 如果是JsonPrimitive对象则获取当中的值,则还需要再次进行判断一下
            }else if (value.isJsonPrimitive()){
                try {
                    JsonElement element = new JsonParser().parse(value.getAsString());
                    if (element.isJsonNull()){
                        map.put(fullkey,value.getAsString());
                    }else if (element.isJsonObject()) {
                        parseJson2Map(map, element.getAsJsonObject(), fullkey);
                    } else if (element.isJsonPrimitive()) {
                        JsonPrimitive jsonPrimitive = element.getAsJsonPrimitive();

                        if (jsonPrimitive.isNumber()) {
                            map.put(fullkey, jsonPrimitive.getAsNumber());
                        } else {
                            map.put(fullkey, jsonPrimitive.getAsString());
                        }
                    } else if (element.isJsonArray()) {
                        JsonArray jsonArray = element.getAsJsonArray();
                        Iterator<JsonElement> iterator = jsonArray.iterator();
                        while (iterator.hasNext()) {
                            parseJson2Map(map, iterator.next().getAsJsonObject(), fullkey);
                        }
                    }
                }catch (Exception e){
                    map.put(fullkey,value.getAsString());
                }
            }
        }
    }

    /**
     * 使用Gson拍平json字符串，即当有多层json嵌套时，可以把多层的json拍平为一层
     * @param map
     * @param json
     * @param parentKey
     */
    public static void parseJson2Map(Map map, String json, String parentKey){
        JsonElement jsonElement = new JsonParser().parse(json);
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            parseJson2Map(map,jsonObject,parentKey);
            //传入的还是一个json数组
        }else if (jsonElement.isJsonArray()){
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            Iterator<JsonElement> iterator = jsonArray.iterator();
            while (iterator.hasNext()){
                parseJson2Map(map,iterator.next().getAsJsonObject(),parentKey);
            }
        }else if (jsonElement.isJsonPrimitive()){
            System.out.println("please check the json format!");
        }else if (jsonElement.isJsonNull()){

        }
    }
    public static void main(String[] args){
        String json = "{\"code\":200, \"message\":\"ok\", \"data\":\"{\\\"id\\\":131,\\\"appId\\\":6,\\\"versionCode\\\":6014000}\"}";

        String test = "{" + "\"hello\": \"sweetzcc\"," +
                "\"topic\": \"gjs\"," +
                "\"Id\": \"180605Ceb8NB\"," +
                "\"Type\": \"REG\"," +
                "\"Time\": \"2018-06-05 10:02:24\"," +
                "\"sweetzcc\": \"{\\\"needUpdate\\\":true,\\\"Info\\\":\\\"{\\\\\\\"apple\\\\\\\":\\\\\\\"BB199DA64A7692E927722BFD1CA\\\\\\\",\\\\\\\"token\\\\\\\":null,\\\\\\\"uniqueId\\\\\\\":\\\\\\\"868387\\\\\\\",\\\\\\\"pushSweetToken\\\\\\\":\\\\\\\"a968\\\\\\\",\\\\\\\"device\\\\\\\":\\\\\\\"android\\\\\\\",\\\\\\\"systemName\\\\\\\":\\\\\\\"Re\\\\\\\",\\\\\\\"systemV\\\\\\\":\\\\\\\"7.0\\\\\\\",\\\\\\\"pVersion\\\\\\\":\\\\\\\"4.9\\\\\\\",\\\\\\\"key\\\\\\\":\\\\\\\"63e78ea58\\\\\\\",\\\\\\\"chan\\\\\\\":\\\\\\\"net\\\\\\\",\\\\\\\"push\\\\\\\":\\\\\\\"4\\\\\\\",\\\\\\\"userName\\\\\\\":null,\\\\\\\"product\\\\\\\":\\\\\\\"sweet\\\\\\\",\\\\\\\"crime\\\\\\\":1528,\\\\\\\"update1\\\\\\\":15281}\\\"}\"" +
                "}";
        String  array = "{'name':'111','child':[{'child':[{'name':'333'}]},{'name':'2221'}]}" ;
        System.out.println(jsonFormatter(test));
    }

}
