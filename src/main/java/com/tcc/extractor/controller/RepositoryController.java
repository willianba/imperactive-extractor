package com.tcc.extractor.controller;

import java.util.List;

import com.tcc.extractor.dto.GitHubRawContent;
import com.tcc.extractor.dto.RepositoryRequestDTO;
import com.tcc.extractor.rabbitmq.TranslatorPublisher;
import com.tcc.extractor.service.RepositoryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE)
public class RepositoryController {

  @Autowired
  private RepositoryService service;

  @Autowired
  private TranslatorPublisher publisher;

  @PostMapping
  public ResponseEntity<String> extractFiles(@RequestBody RepositoryRequestDTO repositoryRequestDTO,
      @RequestHeader(name = "GitToken") String gitToken) {
    List<GitHubRawContent> rawFiles = service.extractFiles(repositoryRequestDTO, gitToken);
    publisher.sendFilesToTranslation(rawFiles);
    return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body("Files sent to translation");
  }

}
