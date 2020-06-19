package EmailScraper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.http.HttpClient;
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

public class EmailScraper {

    private int amountOfEmails;
    private Set<String> emails = new HashSet<>();

    private Queue<String> links = new LinkedList<>();
    private Set<String> linksCheckerSet = new HashSet<>();
    private String previousLink = "https://example.com/";

    EmailScraper(String rootWebsite, int amount) {
        amountOfEmails = amount;
        links.add(rootWebsite);
    }

    public void scrapeEmails() {
        //Breadth First Search
        while (emails.size() < amountOfEmails) {

            addLinksToQueue(links.peek());
            addEmailsFromHeadOfQueue();
            removeHeadOfQueue();

            System.out.println("-------------------------");
            System.out.println("emailSet " + emails.size());
            System.out.println("linksQueue " + links.size());
            System.out.println("-------------------------");

        }
    }

    private void addLinksToQueue(String url) {
        Document webPageDoc = getWebDoc(url);
        Elements linksOnDoc = webPageDoc.select("a[href]");

        for (Element link : linksOnDoc) {
            String absoluteURL = link.attr("abs:href");

            if (isLinkValid(absoluteURL)){
                links.add(absoluteURL);
            }
        }
    }

    private Document getWebDoc(String url) {
        Document webPageDoc = null;
        try {
            webPageDoc = Jsoup.connect(url)
                    .timeout(90000)
                    .ignoreHttpErrors(true)
                    .get();
        } catch (IOException | IllegalArgumentException ex) {
            System.out.println("something went wrong with: "
                    + url + " " + ex);
            System.out.println(links.toString());
        }
        return webPageDoc;
    }

    private boolean isLinkValid(String link){
        if(isLinkDoubled(link)&&
           isLinkHostNameShort(link)&&
            !link.contains(".png")&&
            !link.contains(".pdf")&&
            !link.contains("mp3")){
            return true;
        }
        else
            return false;
    }

    private boolean isLinkHostNameShort(String link) {
        String[] stringArray = link.split("/");
        if(stringArray.length < 5){
            return true;
        }
        else
            return false;
    }

    private boolean isLinkDoubled(String link) {
        return linksCheckerSet.add(link);
    }

    private void addEmailsFromHeadOfQueue() {
        String headOfQueue = links.peek();
        Document webPageDoc = getWebDoc(headOfQueue);

        Pattern p = Pattern.compile(
                "[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+");
        Matcher matcher = p.matcher(webPageDoc.text());
        while (matcher.find()) {
            System.out.println("found email");
            emails.add(matcher.group());
        }
    }

    private void removeHeadOfQueue() {
        System.out.println("Remove " + links.peek() +" head of Queue");
        links.remove();
    }

    public void printEmails() {
        Object[] emailArray = emails.toArray();
        for (Object s : emailArray) {
            System.out.println(s);
        }
    }

    private String getHostNameViaSplitAndReconstruct(String link){
        String[] stringArray = link.split("/");
        return stringArray[0] + "//" + stringArray[2];
    }

    private String getHostNameViaURLClass(String link){
        try {
            URL url = new URL(link);
            if(url.getHost().contains("h")) {

                System.out.println(url.getProtocol() + "://" + url.getHost());
                return url.getProtocol() + "://" + url.getHost();
            }
            else
                return "discard";
        } catch (MalformedURLException e) {
            System.out.println("Java URL error");
            return "discard";
        }
    }


    private String getHostNameViaRegex(String url) {
        Pattern pattern = Pattern.compile
                ("^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?");
        Matcher matcher = pattern.matcher(url);
        return matcher.group(1) + "://" + matcher.group(2);
    }

}


