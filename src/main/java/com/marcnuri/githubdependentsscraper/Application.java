/*
 * Created on 2020-07-18, 18:09
 */
package com.marcnuri.githubdependentsscraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Application {

  private static final String USER_AGENT = "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)";
  private static final String REFERRER = "http://www.google.com";
  private static final String BASE_URL = "https://github.com/fabric8io/fabric8-maven-plugin/network/dependents?package_id=UGFja2FnZS0xODEwNTMzNjE%3D";

  private void start() throws IOException {
    final Document startPage = Jsoup.connect(BASE_URL)
      .userAgent(USER_AGENT)
      .referrer(REFERRER)
      .get();
    System.out.println("[");
    boolean isNotFirst = false;
    Page page = processPage(startPage);
    while (page.getNextUrl() != null) {
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
    }
    System.out.println("]");
  }

  private Page processPage(Document page) throws IOException {
    if (isAbuse(page)) {
      throw new ScraperException("GitHub abuse detected");
    }
    final List<Dependent> dependents = page.select("#dependents .Box-Row").stream()
      .map(Application::extractDependent)
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
      final int stars = Integer.parseInt(starForks.eq(0).text().trim());
      final int forks = Integer.parseInt(starForks.eq(1).text().trim());
      return new Dependent(organization, name, url, stars, forks);
    }
    return null;
  }

  private static boolean isAbuse(Document page) {
    return page.text().contains("abuse detection");
  }

  public static void main(String... args) throws Exception {
    new Application().start();
  }
}
