package io.github.ust.mico.loanbrokercustomer;

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

  private int ssn = 0;

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
    MicoCloudEventImpl<JsonNode> cloudEvent = new MicoCloudEventImpl<JsonNode>();
    cloudEvent.setRandomId();
    cloudEvent.setBaseCloudEvent(cloudEvent);
    CloudEventManipulator cloudEventManipulator = new CloudEventManipulator();
    cloudEventManipulator.setMissingHeaderFields(cloudEvent, "");
    // Set return address.
    cloudEvent.setReturnTopic("retour");
    ObjectNode data = JsonNodeFactory.instance.objectNode();
    data.put("SSN", this.ssn += 1);
    cloudEvent.setData(data);
    log.debug("Created msg: '{}'", cloudEvent);
    sender.send(cloudEvent);
  }
}
