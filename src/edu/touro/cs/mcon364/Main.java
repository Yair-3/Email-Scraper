package edu.touro.cs.mcon364;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
	EmailAndLinkExtractor emailAndLinkExtractor = new EmailAndLinkExtractor();
    emailAndLinkExtractor.begin("https://lcm.touro.edu/");

    }
}
