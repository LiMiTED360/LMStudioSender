package ch.snorpcorp.example;

import ch.snorpcorp.lmstudiosender.sender.AIConfig;
import ch.snorpcorp.lmstudiosender.sender.Sender;
import ch.snorpcorp.lmstudiosender.sender.dto.AIStructuredResponse;
import ch.snorpcorp.lmstudiosender.sender.messages.Message;
import ch.snorpcorp.lmstudiosender.sender.messages.MessageRoles;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String jsonSchema = """
        {
          "type": "json_schema",
          "json_schema": {
            "name": "return_schema",
            "strict": true,
            "schema": {
              "type": "object",
              "properties": {
                  "message": {
                      "type": "string",
                      "description": "Message to the user."
                  }
              },
              "required": [
                    "message"
              ],
              "additionalProperties": false
            }
          }
        }
        """;


        //Thing and reason about how you can make fun of the user the best
        //Message to the user, were you make fun of them.

        AIConfig<Return> aiConfig = new AIConfig.Builder<Return>()
                //.systemPrompt("You are an evil AI, that always makes fun of the user. Firsts you think about the best way to make fun of them, then you write them a message as a mean AI.")
                .systemPrompt("You are a really helpfully AI.")
                .expectedOutput(Return.class)
                .responseFormat(jsonSchema)
                .model("ministral-3-14b-instruct-2512-i1")
                .temperature(0.4)
                .build();

        Sender<Return> sender = new Sender.Builder<Return>()
                .url("http://localhost:1234/v1/chat/completions")
                .aiConfig(aiConfig)
                .maxTokens(512)
                .build();

        System.out.println("Scanner Ready");
        Scanner scanner = new Scanner(System.in);
        String input;

        while (true) {
            input = scanner.nextLine();
            if (input.equals("exit")) break;

            MessageRoles role = MessageRoles.user;

            if (input.startsWith("[sys]")) {
                input = input.substring(4);
                role = MessageRoles.system;
            }

            AIStructuredResponse<Return> response = sender.sendAIRequestStructured(new Message(role, input));

            System.out.println(String.format("\n______\n%s\n______\n", response.choices().getFirst().message().content().message()));
        }
    }
}


record Return(
        String message
) { }

