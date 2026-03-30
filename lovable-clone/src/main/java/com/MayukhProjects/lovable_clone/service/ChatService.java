package com.MayukhProjects.lovable_clone.service;

import org.springframework.ai.chat.model.ChatResponse;

import java.util.List;

public interface ChatService {

    List<ChatResponse> getProjectChatHistory(Long projectId);

}
