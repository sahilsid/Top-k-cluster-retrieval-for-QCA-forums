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

    public static void main(String[] args) throws Exception{
        // Scanner keyboard = new Scanner(System.in);
        // System.out.println("Enter file name");
        // String file_name = keyboard.nextLine();
        // System.out.println("Enter path to the file");
        // String path = keyboard.nextLine();
        // JSONObject input_file = getjson(path);
        // JsonNode input_file= jsonImport.getjsonlarge(path);
        Dataset_batches.getjsonlarge("src/main/resources/android_questions");
    }

    public Dataset_batches(String file_name, String path) throws Exception {
        this.file_name = file_name;
        this.path = path;

    }

    public static void getjsonlarge(String path) throws Exception {
        // JsonNode input_file=largeJson.readJsonWithObjectMapper(path);
        JsonNode input_file = jsonImport.getjsonlarge(path+".json");
        Iterator<String> nodeIterator = input_file.fieldNames();
        Integer file_number = 1;
        Iterator<String> nodeIterator2 = input_file.fieldNames();
        int count = 0;
        System.out.println(path);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode subFile = mapper.createObjectNode();
        OutputStream out;

        out = new FileOutputStream(path + file_number + ".json");
        while (nodeIterator2.hasNext()) {
            if (count == 5000) {
                file_number++;
                mapper.writeValue(out, subFile);
                subFile = null;
                out = new FileOutputStream(path + file_number + ".json");
                subFile =  mapper.createObjectNode();
            }
            String key = nodeIterator2.next();
			((ObjectNode) subFile).put(key, input_file.get(key));

            count++;
            if (count == 5000)
                break;
            if (input_file.get(key) == null) {
                System.exit(0);

            }

        }
        mapper.writeValue(out, subFile);
    }

}
