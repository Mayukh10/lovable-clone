package com.MayukhProjects.lovable_clone.service.impl;

import com.MayukhProjects.lovable_clone.dto.chat.ChatResponse;
import com.MayukhProjects.lovable_clone.entity.ChatMessage;
import com.MayukhProjects.lovable_clone.entity.ChatSession;
import com.MayukhProjects.lovable_clone.entity.ChatSessionId;
import com.MayukhProjects.lovable_clone.mapper.ChatMapper;
import com.MayukhProjects.lovable_clone.repository.ChatMessageRepository;
import com.MayukhProjects.lovable_clone.repository.ChatSessionRepository;
import com.MayukhProjects.lovable_clone.security.AuthUtil;
import com.MayukhProjects.lovable_clone.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final AuthUtil authUtil;
    private final ChatMapper chatMapper;


    @Override
    public List<ChatResponse> getProjectChatHistory(Long projectId) {
        Long userId = authUtil.getCurrentUserId();

        ChatSession chatSession = chatSessionRepository.getReferenceById(
                new ChatSessionId(projectId, userId)
        );
        List<ChatMessage> chatMessageList = chatMessageRepository.findbyChatSession(chatSession);

        return chatMapper.fromListOfChatMessage(chatMessageList);
    }
}
