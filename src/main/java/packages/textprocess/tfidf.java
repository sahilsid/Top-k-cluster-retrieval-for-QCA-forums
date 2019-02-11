
package packages.textprocess;
import java.util.*;
/**
 * tfidf
 */
public class tfidf {
public static void main(String[] args) {
    List<Dictionary<String,String>> termsfreq = new ArrayList<Dictionary<String,String>>();
    for(Dictionary<String,String> elem : termsfreq){

       Enumeration key = elem.keys();

       while(key.hasMoreElements()){
           System.out.println(elem.get(key.nextElement()));
       }
    }
    System.out.print("o");
}
    
}