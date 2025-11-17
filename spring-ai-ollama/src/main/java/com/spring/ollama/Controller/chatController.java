package com.spring.ollama.Controller;

import com.spring.ollama.Model.ChatSession;
import com.spring.ollama.Service.ChatHistoryService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping
public class chatController {

    private final ChatClient chatClient;
    private final ChatHistoryService history;

    public chatController(ChatClient.Builder builder, ChatHistoryService historyService) {
        this.chatClient = builder.build();
        this.history = historyService;
    }

    // CREATE NEW CHAT
    @PostMapping("/new")
    public ChatSession newChat(@RequestParam String msg) {
        return history.createNewChat(msg);
    }

    // MAIN CHAT ENDPOINT
    @GetMapping("/chat")
    public ResponseEntity<String> chat(@RequestParam String id, @RequestParam String q) {

        ChatSession session = history.getChat(id);

        // Convert saved history into model messages
        List<Message> messages = new ArrayList<>();

        for (String msg : session.getMessages()) {
            if (msg.startsWith("You:")) {
                messages.add(new UserMessage(msg.substring(4).trim()));
            } else if (msg.startsWith("AI:")) {
                messages.add(new AssistantMessage(msg.substring(3).trim()));
            }
        }

        // Add current user message
        messages.add(new UserMessage(q));

        // Send FULL history to model for memory
        String ai = chatClient
                .prompt()
                .messages(messages)
                .call()
                .content();

        // Save both messages in history file
        history.addToChat(id, "You: " + q);
        history.addToChat(id, "AI: " + ai);

        return ResponseEntity.ok(ai);
    }

    // LIST ALL CHATS
    @GetMapping("/list")
    public List<ChatSession> list() {
        return history.loadAll();
    }

    // LOAD A CHAT
    @GetMapping("/load")
    public ChatSession load(@RequestParam String id) {
        return history.getChat(id);
    }

    // DELETE CHAT
    @DeleteMapping("/delete")
    public void deleteChat(@RequestParam String id) {
        history.deleteChat(id);
    }

    // FILE UPLOAD
    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {

        try {
            String content = new String(file.getBytes());

            String ai = chatClient
                    .prompt("Read this file and explain in simple words:\n\n" + content)
                    .call()
                    .content();

            return ResponseEntity.ok(ai);

        } catch (Exception e) {
            return ResponseEntity.ok("Could not read file!");
        }
    }
}
