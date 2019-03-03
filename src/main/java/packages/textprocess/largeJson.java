package packages.textprocess;
import com.fasterxml.jackson.databind.ObjectMapper;
import  com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
public class largeJson {
    public static  JsonNode readJsonWithObjectMapper(String path) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode productNode = new ObjectMapper().readTree(new FileInputStream(path));
        System.out.println(productNode);
        return productNode;
    }
    
    public static void main(String[] args) throws Exception {
       largeJson a = new largeJson();
    }
}