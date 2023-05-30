package edu.touro.cs.mcon364;


import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.htmlunit.FailingHttpStatusCodeException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.concurrent.CountDownLatch;

public class EmailAndLinkExtractor implements Runnable {
    private static int count = 0;
    private int id;
    private final HtmlPuller htmlPuller = new HtmlPuller();
    private final CountDownLatch latch;
    private static final Set<String> emailsExtracted = Collections.synchronizedSet(new HashSet<>());
    private static final BlockingQueue<String> linksExtracted = new LinkedBlockingQueue<>();
    private static final Set<String> linkChecker = Collections.synchronizedSet(new HashSet<>());
    static final Set<Map<String, String>> emailList = new LinkedHashSet<>();

    String regexForEmails = "\\b[A-Za-z0-9._%+-]+@(?!.*\\.png)(?!.*2x\\.)(?!.*1x\\.)(?!.*72\\.)(?!.*\\.webp)(?!.*\\.io)[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\\b";
    String regexForLinks = "https?:\\/\\/(www\\.)?([a-zA-Z0-9\\-]+)\\.(edu|org|net|io|ru|com)(?!.*\\.png)(?!.*\\.jpg)(?!.*instagram)(?!.*tiktok)(?!.*linkedin)(?!.*\\.ico)(?!.*\\.css)(?!.*\\.js)(?!.*\\.svg)[^\\s\\\"]*";


    private static final Logger logger = Logger.getLogger(HtmlPuller.class.getName());

    public EmailAndLinkExtractor(CountDownLatch latch) {
        this.id = ++count;
        PropertyConfigurator.configure("log4j.properties");
        this.latch = latch;
    }

    public void begin(String url) throws IOException {
        String pageContent = htmlPuller.clientSetUp(url);


        obtainEmails(pageContent, url);
        obtainLinks(pageContent);

        while (emailsExtracted.size() < 10000) {
            String link = linksExtracted.remove();
            logger.info("Now Searching on thread" + id + " " + link);
            try {
                pageContent = htmlPuller.clientSetUp(link);
            } catch (FailingHttpStatusCodeException e) {
                logger.error("A HTTP error occurred: " + e.getStatusCode());
                continue;
            } catch (MalformedURLException e) {
                logger.error("The URL is not well-formed: " + url);
                continue;
            } catch (IOException e) {
                logger.error("An error occurred while trying to read from the URL: " + url);
                continue;
            } catch (ClassCastException e) {
                logger.error("An Error occurred converting the link to XML");
                continue;

            }
            logger.info("Size of Email List: " + emailsExtracted.size());
            synchronized (emailsExtracted) {
                switch (emailsExtracted.size()) {
                    case 1000 -> logger.info("EMAILS AT 1000");
                    case 2000 -> logger.info("EMAILS AT 2000");
                    case 3000 -> logger.info("EMAILS AT 3000");
                    case 4000 -> logger.info("EMAILS AT 4000");
                    case 5000 -> logger.info("EMAILS AT 5000");
                }
            }
            if (pageContent != null) {
                obtainEmails(pageContent, link);
                obtainLinks(pageContent);
            }
        }

    }


    private void obtainEmails(String text, String url) {
        Pattern pattern = Pattern.compile(regexForEmails);
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            String email = matcher.group();
            synchronized (emailsExtracted) {
                if (emailsExtracted.add(email)) {
                    logger.info("Thread " + id + " Added Email:  " + email);
                    Map<String, String> emailData = new HashMap<>();
                    emailData.put("email", email);
                    emailData.put("linkFound", url);
                    synchronized (emailList){
                        emailList.add(emailData);
                    }

                }
            }
        }

    }

    private void obtainLinks(String text) {
        Pattern pattern = Pattern.compile(regexForLinks);
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            String link = matcher.group();
            synchronized (linkChecker) {
                if (linkChecker.add(link)) {
                    synchronized (linksExtracted) {
                        linksExtracted.add(link);
                    }
                }
            }
        }

    }
    public static BlockingQueue<String> getLinksExtracted() {
        return new LinkedBlockingQueue<>(linksExtracted);
    }


    @Override
    public void run() {
        logger.info("################# THREAD: " + id + " STARTED");
        try {
            begin("https://lcm.touro.edu/");
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            latch.countDown();
            logger.info("################# THREAD: " + id + " ENDED");
        }

    }
}

