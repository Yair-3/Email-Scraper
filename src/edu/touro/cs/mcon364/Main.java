package edu.touro.cs.mcon364;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {

        CountDownLatch latch = new CountDownLatch(16);
        ExecutorService ex = Executors.newFixedThreadPool(16);

        for (int i = 0; i < 16; i++){
            ex.execute(new EmailAndLinkExtractor(latch));
        }
        latch.await();

        SQLDatabaseConnection sqlDatabaseConnection = new SQLDatabaseConnection(EmailAndLinkExtractor.emailList);
        sqlDatabaseConnection.execute();
        System.out.println();

        ex.shutdown();
    }
}
