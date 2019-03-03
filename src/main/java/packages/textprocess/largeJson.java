package packages.textprocess;
import com.fasterxml.jackson.databind.ObjectMapper;
import  com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
public class largeJson {
    public  void readJsonWithObjectMapper() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode productNode = new ObjectMapper().readTree(new FileInputStream("src/main/resources/android/android_questions.json"));
        System.out.println(productNode);

        System.out.println(productNode.get("51829").get("body"));
    }
    public static void main(String[] args) throws Exception {
       largeJson a = new largeJson();
       a.readJsonWithObjectMapper();
    }
}