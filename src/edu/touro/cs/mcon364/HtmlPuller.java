package edu.touro.cs.mcon364;


import org.apache.log4j.Logger;
import org.htmlunit.FailingHttpStatusCodeException;
import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlPage;

import java.io.IOException;
import java.net.MalformedURLException;


public class HtmlPuller {
    WebClient client = new WebClient();
//    private final Logger logger = Logger.getLogger(HtmlPuller.class.getName());

    public static void main(String[] args) throws Exception {

        WebClient client = new WebClient();
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);
        String url = "https://lcm.touro.edu/academics/registrar/";
        HtmlPage page = client.getPage(url);
        String pageContent = page.asXml();
        System.out.println(pageContent);

    }

    public String clientSetUp(String url) throws IOException {

        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);
        HtmlPage page = client.getPage(url);
////            logger.info("URL converted to XML");
//        } catch (FailingHttpStatusCodeException e) {
////            logger.error("A HTTP error occurred: " + e.getStatusCode());
//        } catch (MalformedURLException e) {
////            logger.error("The URL is not well-formed: " + url);
//        } catch (IOException e) {
////            logger.error("An error occurred while trying to read from the URL: " + url);
//        } catch (ClassCastException e){
////            logger.error("An Error occurred converting the link to XML");
//        }
        if (page != null) {
            return page.asXml();
        }
        return null;
    }

}
