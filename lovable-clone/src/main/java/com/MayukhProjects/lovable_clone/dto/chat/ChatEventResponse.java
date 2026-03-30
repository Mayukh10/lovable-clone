package com.MayukhProjects.lovable_clone.dto.chat;

import com.MayukhProjects.lovable_clone.enums.ChatEventType;
import jakarta.persistence.*;

public record ChatEventResponse(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        Long id,
        ChatEventType type,
        Integer sequenceOrder,
        String content,

        String filePath,
        String metadata
) {


}
