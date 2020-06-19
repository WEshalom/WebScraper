package EmailScraper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class LinkScraper {
    public static void main(String[] args) throws IOException {
        LinkScraper.printLinks("https://3M.com");
    }

    private static void printLinks(String url) {

        Document webPageDoc = null;
        try {
            webPageDoc = Jsoup.connect(url).timeout(20000).get();
        } catch (IOException ex) {
            System.out.println("something went wrong with: " + url + " " + ex);
            return;
        }

        Elements linksOnDoc = webPageDoc.select("a[href]");

        for (Element link : linksOnDoc) {
            String absoluteURL = link.attr("abs:href");
            System.out.println(absoluteURL);
        }
    }

}


