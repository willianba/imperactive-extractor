package com.tcc.extractor.client;

import java.util.List;
import java.util.stream.Collectors;

import com.tcc.extractor.dto.GitHubRepositoryContent;
import com.tcc.extractor.dto.GitHubContentForTranslation;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class GitHubClient {

  private static final int SUBSTRING_BEGINNING = 19;
  private static final String GITHUB_JSON = "application/vnd.github.v3+json";
  private static final String GITHUB_RAW = "application/vnd.github.v3.raw";

  private RestTemplate restTemplate = new RestTemplate();

  public List<GitHubContentForTranslation> getFilesContent(
      List<GitHubRepositoryContent> files, String gitToken) {
    HttpEntity<String> entity = getHttpEntityWithHeaders(GITHUB_RAW, gitToken);

    return files.stream()
      .map(file -> {
        GitHubContentForTranslation result = new GitHubContentForTranslation();
        String content = restTemplate.exchange(file.getUrl(), HttpMethod.GET, entity, String.class)
          .getBody();
        result.setName(file.getName());
        result.setContent(content);
        return result;
    }).collect(Collectors.toList());
  }

  public List<GitHubRepositoryContent[]> getRepositoryContent(List<String> urls, String gitToken) {
    HttpEntity<String> entity = getHttpEntityWithHeaders(GITHUB_JSON, gitToken);
    List<String> apiUrls = getDirectoryApiUrl(urls);

    return apiUrls.stream()
      .map(url -> restTemplate.exchange(url, HttpMethod.GET, entity, GitHubRepositoryContent[].class)
        .getBody())
      .collect(Collectors.toList());
  }

  private HttpEntity<String> getHttpEntityWithHeaders(String accept, String authorization) {
    HttpHeaders headers = new HttpHeaders();
    headers.set(HttpHeaders.ACCEPT, accept);
    headers.set(HttpHeaders.AUTHORIZATION, authorization);
    return new HttpEntity<>(headers);
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
