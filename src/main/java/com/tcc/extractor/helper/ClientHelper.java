package com.tcc.extractor.helper;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class ClientHelper {

  public List<String> getDirectoryApiUrl(List<String> urls) {
    return urls.stream()
      .map(this::getUserAndRepo)
      .map(this::getDirectoryFormattedApiUrl)
      .collect(Collectors.toList());
  }

  private String[] getUserAndRepo(String gitUrl) {
    Pattern pattern = Pattern.compile("\\.com/(.*)");
    Matcher matcher = pattern.matcher(gitUrl);
    matcher.find(); // TODO remove, bad practice
    return matcher.group(1).split("/");
  }

  private String getDirectoryFormattedApiUrl(String[] userAndRepo) {
    StringBuilder sb = new StringBuilder();
    String user = userAndRepo[0];
    String repo = userAndRepo[1];
    return sb.append("https://api.github.com/repos/")
      .append(user)
      .append("/")
      .append(repo)
      .append("/contents")
      .toString();
  }
}
