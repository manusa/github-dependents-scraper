/*
 * Created on 2020-07-18, 20:48
 */
package com.marcnuri.githubdependentsscraper;

import java.util.List;
import java.util.Objects;

public class Page {
  private final String nextUrl;
  private final List<Dependent> dependents;

  Page(String nextUrl,
    List<Dependent> dependents) {
    this.nextUrl = nextUrl;
    this.dependents = dependents;
  }

  String getNextUrl() {
    return nextUrl;
  }

  List<Dependent> getDependents() {
    return dependents;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Page page = (Page) o;
    return Objects.equals(nextUrl, page.nextUrl) &&
      Objects.equals(dependents, page.dependents);
  }

  @Override
  public int hashCode() {
    return Objects.hash(nextUrl, dependents);
  }
}
