package com.tcc.extractor.helper;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class ClientHelper {

  private static final String FILE = "blob";
  private static final String DIRECTORY = "tree";

  public List<String> getDirectoryApiUrl(List<String> urls) {
    return urls.stream()
      .map(this::getRepoPath)
      .map(this::getDirectoryFormattedApiUrl)
      .collect(Collectors.toList());
  }

  private String getRepoPath(String gitUrl) {
    Pattern pattern = Pattern.compile("\\.com/(.*)");
    Matcher matcher = pattern.matcher(gitUrl);
    matcher.find(); // TODO remove, bad practice
    return matcher.group(1);
  }

  private String getDirectoryFormattedApiUrl(String repoPath) {
    StringBuilder sb = new StringBuilder();
    String[] urlParameters = getUrlParameters(repoPath);
    return sb.append("https://api.github.com/repos/")
      .append(urlParameters[0])
      .append("/")
      .append(urlParameters[1])
      .append("/contents/")
      .append(urlParameters[2])
      .toString();
  }

  private String[] getUrlParameters(String repoPath) {
    String[] splittedPath = repoPath.split("/");
    if (repoPath.contains(FILE)) {
      return new String[] {
        splittedPath[0],
        splittedPath[1],
        splittedPath[4] + "/" + splittedPath[5] // ugly af but no time brother, also tighy coupled to my repo
      };
    }

    if (repoPath.contains(DIRECTORY)) {
      return new String[] {
        splittedPath[0],
        splittedPath[1],
        splittedPath[4]
      };
    }

    return new String[] { // array for a root's repo url
      splittedPath[0],
      splittedPath[1],
      ""
    };
  }
}
