package com.MayukhProjects.lovable_clone.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ChatSession {

    @EmbeddedId
    private  ChatSessionId id;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @MapsId("projectId")
    @JoinColumns(
            {
                    @JoinColumn(name = "project_id",nullable = false),

            }
    )
    Project project;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @MapsId("userId")
    @JoinColumns(
            {
                    @JoinColumn(name = "user_id",nullable = false,updatable = false)

            }
    )
    User user;

    String title;

    @CreationTimestamp
    @Column(nullable = false,updatable = false)
    Instant createdAt;

    @UpdateTimestamp
    Instant updatedAt;
    Instant deletedAt;
}
