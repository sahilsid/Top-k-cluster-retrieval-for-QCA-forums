package packages.textprocess;

import java.util.*;
import com.github.chen0040.data.text.TextFilter;
import com.github.chen0040.data.text.StopWordRemoval;
import com.github.chen0040.data.text.PorterStemmer;

public class Preprocess {

    TextFilter stemmer = new PorterStemmer();
    StopWordRemoval filter = new StopWordRemoval();

    Preprocess() {
        filter.setRemoveNumbers(true);
        filter.setRemoveXmlTag(true);
    }

    public static void main(String[] args) {
        Preprocess p = new Preprocess();
        List<String> processed = p.keywords(
                "I've managed to setup the chroot chroots but I'm unsure how to get the chroot to recognise usb devices. I'll be doing so work with microcontrollers hence I neeed to to recognise my usb based programmer.</p>\n\n<p>For starters how do I populate the dev directory with the host dev directory(android)?</p>\n\n<p>Is it then just a matter of getting the right kernel modules loaded?</p>\n\n<p>I'm running prime1.5 firmware on the asus transformer</p>");
        for (String key : processed) {
            System.out.println(key);
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
        processed = p.stem(processed);

        return processed;
    }

}
