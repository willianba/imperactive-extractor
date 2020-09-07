package com.tcc.extractor.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.tcc.extractor.client.GitHubClient;
import com.tcc.extractor.dto.GitHubRepositoryContent;
import com.tcc.extractor.dto.GitHubContentForTranslation;
import com.tcc.extractor.dto.TranslationRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RepositoryService {

  private static final String CONTENT_TYPE = "file";

  @Autowired
  private GitHubClient client;

  public List<GitHubContentForTranslation> extractFiles(TranslationRequest translationRequest) {
    List<GitHubRepositoryContent[]> repositoryNestedContent = client.getRepositoryContent(
      translationRequest.getRepositoriesUrl());

    List<GitHubRepositoryContent> repositoryContent = flatNestedContent(repositoryNestedContent);

    List<GitHubRepositoryContent> filteredFiles = filterFilesByExtension(repositoryContent,
      translationRequest.getExtension());

    List<GitHubContentForTranslation> filesContent = client.getFilesContent(filteredFiles);

    filesContent.forEach(file -> {
      file.setSourceLanguage(translationRequest.getSourceLanguage());
      file.setTargetLanguage(translationRequest.getTargetLanguage());
    });

    return filesContent;
  }

  private List<GitHubRepositoryContent> flatNestedContent(List<GitHubRepositoryContent[]> nestedContent) {
    return nestedContent.stream()
      .flatMap(items -> Arrays.stream(items))
      .collect(Collectors.toList());
  }

  private List<GitHubRepositoryContent> filterFilesByExtension(List<GitHubRepositoryContent> repositoryContent,
      List<String> extensions) {
    return repositoryContent.stream()
      .filter(item -> item.getType().equals(CONTENT_TYPE))
      .filter(item -> extensions.stream().anyMatch(ext -> item.getName().contains(ext)))
      .collect(Collectors.toList());
  }
}
