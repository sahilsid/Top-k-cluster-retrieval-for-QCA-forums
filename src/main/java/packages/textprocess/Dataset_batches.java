package packages.textprocess;

import java.io.FileReader;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang3.ObjectUtils.Null;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.util.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.*;

public class Dataset_batches {
    String file_name;
    String path;

    public static void main(String[] args) throws Exception {
        // Scanner keyboard = new Scanner(System.in);
        // System.out.println("Enter file name");
        // String file_name = keyboard.nextLine();
        // System.out.println("Enter path to the file");
        // String path = keyboard.nextLine();
        // JSONObject input_file = getjson(path);
        // JsonNode input_file= jsonImport.getjsonlarge(path);
        Dataset_batches.getjsonlarge("src/main/resources/android/android_questions",
                "src/main/resources/subFiles/questions/android_questions");
    }

    public Dataset_batches(String file_name, String path) throws Exception {
        this.file_name = file_name;
        this.path = path;

    }

    public static void getjsonlarge(String path, String destination) throws Exception {
        // JsonNode input_file=largeJson.readJsonWithObjectMapper(path);
        JsonNode input_file = jsonImport.getjsonlarge(path + ".json");
        Iterator<String> nodeIterator = input_file.fieldNames();
        Integer file_number = 1;
        Iterator<String> nodeIterator2 = input_file.fieldNames();
        int count = 0;
        ObjectMapper mapper = new ObjectMapper();
        OutputStream out;
        Integer threshold = 2500;
        Integer totalSubFiles = threshold >0 ? Math.floorDiv(input_file.size(),threshold )+1: 0 ;
        JsonNode subFile[] = new JsonNode[totalSubFiles];
        subFile[0] = mapper.createObjectNode();
        out = new FileOutputStream(destination + file_number + ".json");
        while (nodeIterator2.hasNext()) {

            if (count == threshold) {
                count = 0;
                mapper.writeValue(out, subFile[file_number-1]);
                subFile[file_number-1]=null;
                file_number++;
                out = new FileOutputStream(destination + file_number + ".json");
                subFile[file_number-1] = mapper.createObjectNode();

            }
            String key = nodeIterator2.next();

            ((ObjectNode) subFile[file_number-1]).put(key, input_file.get(key));

            count++;
        }
        System.out.println(out);
        mapper.writeValue(out, (Object) subFile[file_number-1]);
    
    }
}
