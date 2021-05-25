package io.github.ust.mico.loanbrokercustomer;

import java.util.Random;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.ust.mico.loanbrokercustomer.kafka.MicoCloudEventImpl;
import io.github.ust.mico.loanbrokercustomer.messageprocessing.CloudEventManipulator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MessageGenerator {

  // @Autowired
  // CloudEventManipulator cloudEventManipulator;

  @Autowired
  private Sender sender;

  public MessageGenerator(Sender sender) {
    this.sender = sender;
    try {
      startProduction();
    } catch (InterruptedException ie) {
      // TODO: handle exception
    }
  }

  private void startProduction() throws InterruptedException {
    while (true) {
      this.produceMessage();
      Thread.sleep(5000);
    }
  }

  private void produceMessage() {
    // Create message.
    MicoCloudEventImpl<JsonNode> cloudEvent = new MicoCloudEventImpl<JsonNode>();
    // Fill base fields.
    cloudEvent.setRandomId();
    cloudEvent.setBaseCloudEvent(cloudEvent);
    CloudEventManipulator cloudEventManipulator = new CloudEventManipulator();
    cloudEventManipulator.setMissingHeaderFields(cloudEvent, "");
    // Set return address.
    cloudEvent.setReturnTopic("retour");
    // Create loan request.
    ObjectNode data = JsonNodeFactory.instance.objectNode();
    data.put("SSN", UUID.randomUUID().toString());
    data.put("loan", Math.round(new Random().ints(10000, 100000 + 1).findFirst().getAsInt() / 5000) * 5000);
    data.put("term", Math.round(new Random().ints(12, 48 + 1).findFirst().getAsInt() / 12) * 12);
    cloudEvent.setData(data);
    // Log and send.
    log.debug("Created msg: '{}'", cloudEvent);
    sender.send(cloudEvent);
  }
}
