
/**
 * @author Sahil
 */

package packages.textprocess;

import java.util.*;
import com.github.chen0040.data.text.TextFilter;
import com.github.chen0040.data.text.StopWordRemoval;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.chen0040.data.text.PorterStemmer;
import org.json.simple.JSONObject;

public class Preprocess {

    TextFilter stemmer = new PorterStemmer();
    StopWordRemoval filter = new StopWordRemoval();

    public Preprocess() {
        filter.setRemoveNumbers(true);
        filter.setRemoveXmlTag(true);
    }

    public static void main(String[] args) throws Exception {
        Preprocess p = new Preprocess();
        JsonNode questions = jsonImport.getjsonlarge("src/main/resources/android_questions.json");
        List<String> processed = new ArrayList<String>();
        Iterator<String> nodeIterator  =  questions.fieldNames();

        while(nodeIterator.hasNext()){
            String key = nodeIterator.next();
            //System.out.println(key); 
            //System.out.println( questions.get(key).get("body").toString());
            // processed = p.keywords((String)value.get("body"));

            // System.out.print ("\n\nQ Id : " + k + " \t ");

            // System.out.print("Keywords : [ " );
            // for (String id : processed) {
            //     System.out.print(id);
            //     System.out.print(" , ");
            // }
            // System.out.print(" ]");

        }


    }

    public static List<String> tokenize(String text) {

        List<String> result = new ArrayList<String>();

        text = text.replaceAll("[^a-zA-Z0-9]", " ");

        StringTokenizer tokens = new StringTokenizer(text, " ");

        while (tokens.hasMoreTokens()) {
            result.add(tokens.nextToken().toLowerCase());
        }

        return result;
    }

    public List<String> stopwords(String sentence) {

        List<String> processed = filter.filter(tokenize(sentence));
        return processed;
    }

    public List<String> stopwords(List<String> list) {

        List<String> processed = filter.filter(list);
        return processed;
    }

    public List<String> stem(String sentence) {

        List<String> result = stemmer.filter(tokenize(sentence));
        return result;
    }

    public List<String> stem(List<String> sentence) {

        List<String> result = stemmer.filter(sentence);
        return result;
    }

    public List<String> keywords(String text) {
        Preprocess p = new Preprocess();
        List<String> processed = p.stopwords(text);
        //processed = p.stem(processed);
        
        return processed;
    }

}
