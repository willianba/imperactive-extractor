package com.tcc.extractor.client;

import java.util.List;
import java.util.stream.Collectors;

import com.tcc.extractor.dto.GitHubRepositoryContent;
import com.tcc.extractor.helper.ClientHelper;
import com.tcc.extractor.dto.GitHubContentForTranslation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class GitHubClient {

  private static final String GITHUB_JSON = "application/vnd.github.v3+json";
  private static final String GITHUB_RAW = "application/vnd.github.v3.raw";

  private RestTemplate restTemplate = new RestTemplate();

  @Autowired
  private ClientHelper clientHelper;

  @Value("${github.personal.token}")
  private String gitPersonalToken;

  public List<GitHubContentForTranslation> getFilesContent(
      List<GitHubRepositoryContent> files) {
    HttpEntity<String> entity = getHttpEntityWithHeaders(GITHUB_RAW);

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

  public List<GitHubRepositoryContent[]> getRepositoryContent(List<String> urls) {
    HttpEntity<String> entity = getHttpEntityWithHeaders(GITHUB_JSON);
    List<String> apiUrls = clientHelper.getDirectoryApiUrl(urls);

    return apiUrls.stream()
      .map(url -> restTemplate.exchange(url, HttpMethod.GET, entity, GitHubRepositoryContent[].class)
        .getBody())
      .collect(Collectors.toList());
  }

  private HttpEntity<String> getHttpEntityWithHeaders(String accept) {
    HttpHeaders headers = new HttpHeaders();
    headers.set(HttpHeaders.ACCEPT, accept);
    headers.set(HttpHeaders.AUTHORIZATION, "token " + gitPersonalToken);
    return new HttpEntity<>(headers);
  }
}
