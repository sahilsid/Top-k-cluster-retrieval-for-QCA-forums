
/**
 * @author Sahil
 */

package packages.textprocess;
import java.io.FileReader;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class jsonImport {

    public static void main(String[] args) {

        JSONObject result = getjson("src/main/resources/android_questions.json");
        System.out.print(result);
    }

    public static JSONObject getjson(String path) {

        JSONParser parser = new JSONParser();
        JSONObject jsonObject = new JSONObject();
        try {
            Object obj = parser.parse(new FileReader(path));
            jsonObject = (JSONObject) obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static void printJsonObject(JSONObject jsonObj) {
        for (Object key : jsonObj.keySet()) {
            // based on you key types
            String keyStr = (String) key;
            JSONObject keyvalue = (JSONObject) jsonObj.get(keyStr);
            // System.out.println("key: " + keyStr + " value: " + keyvalue);
            // Print key and value
            System.out.println("key: " + keyStr + " value: " + keyvalue);

            // for nested objects iteration if required
            // if (keyvalue instanceof JSONObject)
            // printJsonObject((JSONObject) keyvalue);
        }
    }
}