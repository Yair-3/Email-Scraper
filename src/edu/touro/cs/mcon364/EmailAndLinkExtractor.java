package edu.touro.cs.mcon364;


import com.shapesecurity.salvation2.Values.Hash;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.htmlunit.FailingHttpStatusCodeException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class EmailAndLinkExtractor {
    HtmlPuller htmlPuller = new HtmlPuller();
    String pageContent;
    HashSet<String> emailsExtracted = new HashSet<>();
    Queue<String> linksExtracted = new LinkedList<>();
    HashSet<String> linkChecker = new HashSet<>();

    String regexForEmails = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\\b";
    String regexForLinks = "https?:\\/\\/(www\\.)?([a-zA-Z0-9\\-]+)\\.(edu|org|net|io|ru|com)(?!.*\\.png)(?!.*\\.jpg)(?!.*\\.ico)(?!.*\\.css)(?!.*\\.js)(?!.*\\.svg)[^\\s\\\"]*";

    private static final Logger logger = Logger.getLogger(HtmlPuller.class.getName());

    public EmailAndLinkExtractor() {
        PropertyConfigurator.configure("log4j.properties");
    }

    public void begin(String url) throws IOException {
        pageContent = htmlPuller.clientSetUp(url);
        obtainEmails(pageContent, emailsExtracted);
        obtainLinks(pageContent, linksExtracted);

        while (emailsExtracted.size() < 1000) {
            String link = linksExtracted.remove();
            logger.info("Now Searching " + link);
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
            if (pageContent != null) {
                obtainEmails(pageContent, emailsExtracted);
                obtainLinks(pageContent, linksExtracted);
            }
        }
        System.out.println(emailsExtracted);

    }


    private HashSet<String> obtainEmails(String text, HashSet<String> emailsExtracted) {
        Pattern pattern = Pattern.compile(regexForEmails);
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            String email = matcher.group();
            if (emailsExtracted.add(email)) {
                logger.info("Email Added: " + email);
            }
        }

        return emailsExtracted;
    }

    private Queue<String> obtainLinks(String text, Queue<String> linksExtracted) {
        Pattern pattern = Pattern.compile(regexForLinks);
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            String link = matcher.group();
            if (linkChecker.add(link)) {
                linksExtracted.add(link);
                logger.info("Link Obtained: " + link);
            }
        }

        return linksExtracted;
    }

}

