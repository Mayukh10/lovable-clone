package com.MayukhProjects.lovable_clone.entity;

import com.MayukhProjects.lovable_clone.enums.MessageRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumns(
            {
                    @JoinColumn(name = "project_id",referencedColumnName = "project_id",nullable = false),
                    @JoinColumn(name = "user_id",referencedColumnName = "user_id",nullable = false)
            }
    )
    ChatSession chatSession;

    @Column(columnDefinition = "text",nullable = false)
    String content;
    String toolCalls;

    Integer tokensUsed = 0;

    @CreationTimestamp
    Instant createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    MessageRole role;

    @OneToMany(mappedBy = "chatMessage")
    @OrderBy("sequenceOrder ASC")
    List<ChatEvent> event;
}
