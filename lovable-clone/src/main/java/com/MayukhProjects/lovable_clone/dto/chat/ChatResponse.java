package com.MayukhProjects.lovable_clone.dto.chat;

import com.MayukhProjects.lovable_clone.entity.ChatEvent;
import com.MayukhProjects.lovable_clone.entity.ChatSession;
import com.MayukhProjects.lovable_clone.enums.MessageRole;
import org.springframework.ai.chat.messages.Message;

import java.time.Instant;
import java.util.List;

public record ChatResponse(

        Long id,
        ChatSession chatSession,
        MessageRole role,
        List<ChatEvent> events,

        String content,

        Integer tokensUsed,

        Instant createdAt

) {


}
