package com.tcc.extractor.dto;

import java.util.List;

public class RepositoryRequestDTO {

  private List<String> repositoriesUrl;
  private List<String> extension;

  public List<String> getRepositoriesUrl() {
    return this.repositoriesUrl;
  }

  public void setRepositoriesUrl(List<String> repositoriesUrl) {
    this.repositoriesUrl = repositoriesUrl;
  }

  public List<String> getExtension() {
    return this.extension;
  }

  public void setExtension(List<String> extension) {
    this.extension = extension;
  }
}
