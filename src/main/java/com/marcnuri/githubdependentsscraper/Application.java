/*
 * Created on 2020-07-18, 18:09
 */
package com.marcnuri.githubdependentsscraper;

import javax.inject.Inject;
import picocli.CommandLine;

import java.net.MalformedURLException;
import java.net.URL;

@CommandLine.Command(name = "github-dependents")
public class Application implements Runnable {

  private final ScraperService scraperService;

  @CommandLine.Parameters(index = "0", paramLabel = "URL", arity = "1",
    description = "GitHub URL to the projects dependents list")
  String dependentsUrl;

  @Inject
  public Application(ScraperService scraperService) {
    this.scraperService = scraperService;
  }

  @Override
  public void run() {
    try {
      new URL(dependentsUrl);
      scraperService.scrape(dependentsUrl);
    } catch(MalformedURLException ex) {
      System.err.printf("URL %s is invalid, please provide a valid URL.%n", dependentsUrl);
      CommandLine.usage(this, System.out);
    } catch (Exception e) {
      System.err.println(e.getMessage());
      e.printStackTrace();
    }
  }

}
