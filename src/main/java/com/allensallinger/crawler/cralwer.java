package com.allensallinger.crawler;


import com.allensallinger.crawler.executor.executor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;

public class cralwer {

    private static ConcurrentHashMap<String, Boolean> seenLinks = new ConcurrentHashMap<String, Boolean>();

    private static ConcurrentLinkedQueue<String> linksAvailable = new ConcurrentLinkedQueue<String>();

    private static final ReentrantLock lock = new ReentrantLock();

    // extract links
    public static void extractPageLinks(Element content) {
        Elements links = content.getElementsByTag("a");
        for (Element link : links) {
            String linkHref = link.attr("href");
            String linkText = link.text();
            if (linkHref.length() >= 4) {
                if (linkHref.substring(0,4).equals("http") && !linkHref.contains("user")) {
                    System.out.println("link: " + linkHref);
                    System.out.println("linkText: " + linkText);

                    if (!seenLinks.contains(linkHref)){
                        seenLinks.put(linkHref, false);
                        final boolean urlAdded = linksAvailable.add(linkHref);

                        if (urlAdded) {
                            System.out.println("A url has been added to scrape");
                        }
                    }

                }
            }

        }
    }


    public static void scrapeUrl(String url) throws IOException {
        seenLinks.put(url, true);

        Document doc = Jsoup.connect(url)
                .userAgent("java_crawler")
                .get();

        System.out.println(doc.text());

        Element content = doc.getElementById("siteTable");
        System.out.println(content.text());

        // extract links
        extractPageLinks(content);

    }


    public static void main(String args[]) throws IOException {
        System.out.println("==== Start of the crawler =====");

        // define the runnalbe task that will be called by the executor
        Runnable runnableTask;

        linksAvailable.add("https://reddit.com/r/Ethtrader");

        while  (seenLinks.size() < 50 ) {

            runnableTask = () -> {
                try {
                    String url = linksAvailable.poll();

                    scrapeUrl(url);

                    System.out.println("Increment run");
                } catch (IOException e)  {
                    System.out.println("Interruption triggered");
                    e.printStackTrace();
                }
            };

            executor.execute(runnableTask);

        }

        executor.shutdown();





        // starter link

        // for loop that runs through the queue of other links
    }

}
