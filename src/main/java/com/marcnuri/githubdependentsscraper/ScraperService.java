/*
 * Created on 2020-07-19, 8:22
 */
package com.marcnuri.githubdependentsscraper;

import javax.inject.Singleton;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Singleton
public class ScraperService {

  private static final String USER_AGENT = "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)";
  private static final String REFERRER = "http://www.google.com";

  void scrape(String url) throws IOException, InterruptedException {
    Page page = processPage(fetchPage(url));
    if (page.getDependents().isEmpty()) {
      System.out.println("No dependents found, if this is unexpected verify that the provided URL is valid.");
      return;
    }
    System.out.print("[");
    boolean isNotFirst = false;
    while(true) {
      for(Dependent dependent : page.getDependents()) {
        if (isNotFirst) {
          System.out.print(",");
        }
        isNotFirst = true;
        System.out.printf("%n  {");
        System.out.printf("\"organization\": \"%s\", ", dependent.getOrganization());
        System.out.printf("\"name\": \"%s\", ", dependent.getName());
        System.out.printf("\"url\": \"%s\", ", dependent.getUrl());
        System.out.printf("\"stars\": %s, ", dependent.getStars());
        System.out.printf("\"forks\": %s", dependent.getForks());
        System.out.print("}");
      }
      if (page.getNextUrl() == null) {
        break;
      }
      page = processPage(fetchPage(page.getNextUrl()));
    }
    System.out.printf("%n]");
  }

  private Document fetchPage(String url) throws IOException, InterruptedException {
    Document ret;
    try {
       ret = Jsoup.connect(url).userAgent(USER_AGENT).referrer(REFERRER).get();
    } catch(HttpStatusException exception) {
      if (exception.getStatusCode() == 429) {
        Thread.sleep(30000);
        ret = Jsoup.connect(url).userAgent(USER_AGENT).referrer(REFERRER).get();
      } else {
        throw exception;
      }
    }
    return ret;
  }

  private Page processPage(Document page) throws IOException {
    if (isAbuse(page)) {
      throw new ScraperException("GitHub abuse detected");
    }
    final List<Dependent> dependents = page.select("#dependents .Box-Row").stream()
      .map(ScraperService::extractDependent)
      .filter(Objects::nonNull)
      .collect(Collectors.toList());
    return new Page(extractNextPageUrl(page), dependents);
  }

  private static String extractNextPageUrl(Document page) {
    return page.select("#dependents .paginate-container a").stream()
      .filter(link -> link.text().contains("Next"))
      .map(link -> link.attr("abs:href"))
      .findAny()
      .orElse(null);
  }

  private static Dependent extractDependent(Element row) {
    final Element link = row.select("a.text-bold").first();
    if (!link.text().isBlank()) {
      final String organization = link.attr("href").split("/")[1];
      final String name = link.text().trim();
      final String url = link.attr("abs:href");
      final Elements starForks = row.select("span.pl-3");
      final int stars = Integer.parseInt(starForks.eq(0).text().trim().replaceAll("[^0-9]", ""));
      final int forks = Integer.parseInt(starForks.eq(1).text().trim().replaceAll("[^0-9]", ""));
      return new Dependent(organization, name, url, stars, forks);
    }
    System.err.printf("%n%nWARNING: Dependents not found%n%nPage content:%n%s", row.text());
    return null;
  }

  private static boolean isAbuse(Document page) {
    return page.text().contains("abuse detection");
  }
}
