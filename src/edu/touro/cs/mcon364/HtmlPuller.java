package edu.touro.cs.mcon364;


import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlPage;
import java.io.IOException;


public class HtmlPuller {
    WebClient client = new WebClient();

    public static void main(String[] args) throws Exception {

    }

    public String clientSetUp(String url) throws IOException {

        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);
        HtmlPage page = client.getPage(url);
        if (page != null) {
            return page.asXml();
        }
        return null;
    }

}
