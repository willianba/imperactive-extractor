package com.tcc.extractor.rabbitmq;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tcc.extractor.dto.GitHubContentForTranslation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TranslatorPublisher {

  private static final Logger logger = LoggerFactory.getLogger(TranslatorPublisher.class);

  @Autowired
  private AmqpTemplate amqpTemplate;

  @Autowired
  private ObjectMapper mapper;

  @Value("${direct.exchange}")
  private String directExchange;

  @Value("${translate.routing.key}")
  private String translateRoutingKey;

  public void sendFilesToTranslation(List<GitHubContentForTranslation> files) {
    try {
      String jsonMessage = mapper.writeValueAsString(files);
      Message message = MessageBuilder.withBody(jsonMessage.getBytes())
        .setContentType(MessageProperties.CONTENT_TYPE_JSON).build();
      amqpTemplate.send(directExchange, translateRoutingKey, message);
    } catch (JsonProcessingException e) {
      logger.error("Error converting message to JSON: {}", e.getMessage());
    }
  }
}
