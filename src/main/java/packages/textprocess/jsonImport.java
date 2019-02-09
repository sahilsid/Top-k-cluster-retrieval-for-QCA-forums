
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

            String keyStr = (String) key;
            String keyvalue = (String) jsonObj.get(keyStr);
            System.out.println("key: " + keyStr + " value: " + keyvalue);

        }
    }
}