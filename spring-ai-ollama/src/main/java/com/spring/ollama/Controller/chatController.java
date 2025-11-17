package com.spring.ollama.Controller;

import com.spring.ollama.Model.ChatSession;
import com.spring.ollama.Service.ChatHistoryService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping("/new")
    public ChatSession newChat(@RequestParam String msg) {
        return history.createNewChat(msg);
    }

    @GetMapping("/chat")
    public ResponseEntity<String> chat(@RequestParam String id, @RequestParam String q) {

        String ai = chatClient.prompt(q).call().content();

        history.addToChat(id, "You: " + q);
        history.addToChat(id, "AI: " + ai);

        return ResponseEntity.ok(ai);
    }

    @GetMapping("/list")
    public List<ChatSession> list() {
        return history.loadAll();
    }

    @GetMapping("/load")
    public ChatSession load(@RequestParam String id) {
        return history.getChat(id);
    }

    @DeleteMapping("/delete")
    public void deleteChat(@RequestParam String id) {
        history.deleteChat(id);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {

        try {
            String content = new String(file.getBytes());

            String ai = chatClient
                    .prompt("Read this file and explain in short:\n" + content)
                    .call()
                    .content();

            return ResponseEntity.ok(ai);

        } catch (Exception e) {
            return ResponseEntity.ok("Could not read file!");
        }
    }
}
