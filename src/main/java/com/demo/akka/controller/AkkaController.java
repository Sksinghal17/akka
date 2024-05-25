package com.demo.akka.controller;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Props;
import akka.actor.typed.javadsl.AskPattern;
import akka.actor.typed.javadsl.Behaviors;
import com.demo.akka.behavior.FireAndForget;
import com.demo.akka.behavior.RequestResponse;
import com.demo.akka.dto.RequestMessage;
import com.demo.akka.dto.ResponseMessage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.concurrent.CompletionStage;

@RestController
public class AkkaController {

  @GetMapping("/fireandforget")
  public void fireandforget(@RequestParam String message) {
    ActorSystem<String> system = ActorSystem.create(FireAndForget.create(), "fireAndForget");
    system.tell(message);
  }

  @GetMapping("/requestresponse")
  public CompletionStage<ResponseMessage> requestresponse(@RequestParam String message) {

    ActorSystem<Void> system = ActorSystem.create(Behaviors.empty(), "RequestResponse");

    ActorRef<RequestMessage> requestHandler = system.systemActorOf(RequestResponse.create(),
        "requestHandler", Props.empty());

    CompletionStage<ResponseMessage> response = AskPattern.ask(requestHandler,
        (replyTo) -> new RequestMessage(message, replyTo), Duration.ofMillis(500),
        system.scheduler());

    return response;
  }


}
