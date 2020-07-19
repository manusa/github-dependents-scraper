/*
 * Created on 2020-07-18, 20:38
 */
package com.marcnuri.githubdependentsscraper;

import java.io.IOException;

public class ScraperException extends IOException {

  public ScraperException(String message) {
    super(message);
  }
}
