/*
 * package com.poc.poc.kafka;
 * 
 * import org.springframework.beans.factory.annotation.Autowired; import
 * org.springframework.web.bind.annotation.GetMapping; import
 * org.springframework.web.bind.annotation.RestController;
 * 
 * @RestController public class KafkaController {
 * 
 * @Autowired private KafkaProducer kafkaProducer;
 * 
 * @GetMapping("/send") public String sendMessage() { String message =
 * "Hello, Kafka!"; kafkaProducer.sendMessage(message); return "Message sent: "
 * + message; } }
 */