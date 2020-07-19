/*
 * Created on 2020-07-18, 18:09
 */
package com.marcnuri.githubdependentsscraper;

import javax.inject.Inject;
import picocli.CommandLine;

@CommandLine.Command
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
      scraperService.scrape(dependentsUrl);
    } catch (Exception e) {
      System.err.println(e.getMessage());
      e.printStackTrace();
    }
  }

}
