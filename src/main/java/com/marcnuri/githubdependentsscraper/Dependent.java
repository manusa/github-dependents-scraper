/*
 * Created on 2020-07-18, 20:35
 */
package com.marcnuri.githubdependentsscraper;


import java.util.Objects;

public class Dependent {
  private final String organization;
  private final String name;
  private final String url;
  private final int stars;
  private final int forks;

  Dependent(String organization, String name, String url, int stars, int forks) {
    this.organization = organization;
    this.name = name;
    this.url = url;
    this.stars = stars;
    this.forks = forks;
  }

  String getOrganization() {
    return organization;
  }

   String getName() {
    return name;
  }

  String getUrl() {
    return url;
  }

  int getStars() {
    return stars;
  }

  int getForks() {
    return forks;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Dependent dependent = (Dependent) o;
    return stars == dependent.stars &&
      forks == dependent.forks &&
      Objects.equals(organization, dependent.organization) &&
      Objects.equals(name, dependent.name) &&
      Objects.equals(url, dependent.url);
  }

  @Override
  public int hashCode() {
    return Objects.hash(organization, name, url, stars, forks);
  }
}
