package com.spring.ollama.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.ollama.Model.ChatSession;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ChatHistoryService {

    private final File file = new File("src/main/resources/chat-history.json");
    private final ObjectMapper mapper = new ObjectMapper();

    public List<ChatSession> loadAll() {
        try {
            if (!file.exists()) return new ArrayList<>();
            return mapper.readValue(file, new TypeReference<>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public void saveAll(List<ChatSession> sessions) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, sessions);
        } catch (Exception ignored) {}
    }

    public ChatSession createNewChat(String firstMsg) {
        List<ChatSession> list = loadAll();

        String title = firstMsg.length() > 20 ? firstMsg.substring(0, 20) + "..." : firstMsg;
        ChatSession session = new ChatSession(UUID.randomUUID().toString(), title);

        session.getMessages().add("You: " + firstMsg);
        list.add(session);

        saveAll(list);
        return session;
    }

    public void addToChat(String id, String msg) {
        List<ChatSession> list = loadAll();

        for (ChatSession c : list)
            if (c.getId().equals(id))
                c.getMessages().add(msg);

        saveAll(list);
    }

    public ChatSession getChat(String id) {
        return loadAll().stream().filter(c -> c.getId().equals(id)).findFirst().orElse(null);
    }

    public void deleteChat(String id) {
        List<ChatSession> list = loadAll();
        list.removeIf(c -> c.getId().equals(id));
        saveAll(list);
    }
}
