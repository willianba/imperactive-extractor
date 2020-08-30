package com.tcc.extractor.client;

import java.util.List;
import java.util.stream.Collectors;

import com.tcc.extractor.dto.GitHubContent;
import com.tcc.extractor.dto.GitHubRawContent;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class GitHubClient {

  private static final int SUBSTRING_BEGINNING = 19;

  private RestTemplate restTemplate = new RestTemplate();

  public List<GitHubRawContent> getFilesRawContent(List<GitHubContent> filteredFiles,
      String gitToken) {
    HttpHeaders headers = new HttpHeaders();
    headers.set(HttpHeaders.ACCEPT, "application/vnd.github.v3.raw");
    headers.set(HttpHeaders.AUTHORIZATION, gitToken);
    HttpEntity<String> entity = new HttpEntity<>(headers);

    return filteredFiles.stream().map(file -> {
      GitHubRawContent result = new GitHubRawContent();
      String content = restTemplate.exchange(file.getUrl(), HttpMethod.GET, entity, String.class)
        .getBody();
      result.setName(file.getName());
      result.setContent(content);
      return result;
    }).collect(Collectors.toList());
  }

  public List<GitHubContent[]> getRepoContent(List<String> urls, String gitToken) {
    HttpHeaders headers = new HttpHeaders();
    headers.set(HttpHeaders.ACCEPT, "application/vnd.github.v3+json");
    headers.set(HttpHeaders.AUTHORIZATION, gitToken);
    HttpEntity<String> entity = new HttpEntity<>(headers);

    List<String> apiUrls = getDirectoryApiUrl(urls);

    return apiUrls.stream()
      .map(url -> restTemplate.exchange(url, HttpMethod.GET, entity, GitHubContent[].class)
        .getBody())
      .collect(Collectors.toList());
  }

  private List<String> getDirectoryApiUrl(List<String> urls) {
    return urls.stream()
      .map(this::getSplittedUrl)
      .map(this::getDirectoryFormattedApiUrl)
      .collect(Collectors.toList());
  }

  private String[] getSplittedUrl(String gitUrl) {
    return gitUrl.substring(SUBSTRING_BEGINNING).split("/");
  }

  private String getDirectoryFormattedApiUrl(String[] splittedUrl) {
    StringBuilder sb = new StringBuilder();
    String user = splittedUrl[0];
    String repo = splittedUrl[1];
    return sb.append("https://api.github.com/repos/")
      .append(user)
      .append("/")
      .append(repo)
      .append("/contents")
      .toString();
  }
}
