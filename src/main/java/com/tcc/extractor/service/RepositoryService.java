package com.tcc.extractor.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.tcc.extractor.client.GitHubClient;
import com.tcc.extractor.dto.GitHubContent;
import com.tcc.extractor.dto.GitHubRawContent;
import com.tcc.extractor.dto.RepositoryRequestDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RepositoryService {

  private static final String CONTENT_TYPE = "file";

  @Autowired
  private GitHubClient client;

  public List<GitHubRawContent> extractFiles(RepositoryRequestDTO repositoryRequestDTO,
      String gitToken) {
    List<GitHubContent[]> repositoryContent = client.getRepoContent(
      repositoryRequestDTO.getRepositoriesUrl(), gitToken);
    List<GitHubContent> filteredFiles = getFilesWithDesiredExtension(repositoryContent,
      repositoryRequestDTO.getExtension());
    return client.getFilesRawContent(filteredFiles, gitToken);
  }

  private List<GitHubContent> getFilesWithDesiredExtension(List<GitHubContent[]> repositoryContent,
      List<String> extensions) {
    return repositoryContent.stream()
      .flatMap(items -> Arrays.stream(items))
      .filter(item -> item.getType().equals(CONTENT_TYPE))
      .filter(item -> extensions.stream().anyMatch(ext -> item.getName().contains(ext)))
      .collect(Collectors.toList());
  }
}
