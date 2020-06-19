package EmailScraper;

public class Main {
        public static void main(String[] args) throws InterruptedException {
            int amount = 3;
            String starterURL = "http://cnn.com";
            EmailScraper emailScraper = new EmailScraper(starterURL, amount);
            emailScraper.scrapeEmails();
            emailScraper.printEmails();
        }
}
