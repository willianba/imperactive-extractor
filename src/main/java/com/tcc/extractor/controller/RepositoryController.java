package com.tcc.extractor.controller;

import java.util.List;

import com.tcc.extractor.dto.GitHubContentForTranslation;
import com.tcc.extractor.dto.TranslationRequest;
import com.tcc.extractor.rabbitmq.TranslatorPublisher;
import com.tcc.extractor.service.RepositoryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
  public ResponseEntity<String> extractFiles(@RequestBody TranslationRequest translationRequest,
      @RequestHeader(name = "GitToken") String gitToken) {
    List<GitHubContentForTranslation> files = service.extractFiles(translationRequest, gitToken);

    if (!files.isEmpty()) {
      publisher.sendFilesToTranslation(files);
      return ResponseEntity.status(HttpStatus.OK)
        .contentType(MediaType.TEXT_PLAIN)
        .body("Files sent to translation");
    }

    return ResponseEntity.status(HttpStatus.NOT_FOUND)
      .contentType(MediaType.TEXT_PLAIN)
      .body("No files found with the required extension");
  }
}
