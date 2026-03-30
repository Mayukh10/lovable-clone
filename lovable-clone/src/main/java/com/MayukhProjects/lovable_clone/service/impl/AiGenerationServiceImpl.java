package com.MayukhProjects.lovable_clone.service.impl;

import com.MayukhProjects.lovable_clone.entity.*;
import com.MayukhProjects.lovable_clone.enums.ChatEventType;
import com.MayukhProjects.lovable_clone.enums.MessageRole;
import com.MayukhProjects.lovable_clone.error.ResourceNotFoundException;
import com.MayukhProjects.lovable_clone.llm.LlmResponseParser;
import com.MayukhProjects.lovable_clone.llm.Prompt;
import com.MayukhProjects.lovable_clone.llm.advisor.FileTreeContextAdvisor;
import com.MayukhProjects.lovable_clone.llm.tools.CodeGenerationTools;
import com.MayukhProjects.lovable_clone.repository.*;
import com.MayukhProjects.lovable_clone.security.AuthUtil;
import com.MayukhProjects.lovable_clone.service.AiGenerationService;
import com.MayukhProjects.lovable_clone.service.ProjectFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiGenerationServiceImpl implements AiGenerationService {


    private final ChatClient chatClient;
    private final AuthUtil authUtil;
    private static final Pattern FILE_TAG_PATTERN =
            Pattern.compile("<file path=\"([^\"]+)\">\\s*([\\s\\S]*?)\\s*</file>");
    private final ProjectFileService projectFileService;

    private final FileTreeContextAdvisor fileTreeContextAdvisor;
    private final LlmResponseParser llmResponseParser;
    private final ChatSessionRepository chatSessionRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatEventRepository chatEventRepository;

    //private String Prompt;


    @Override
    @PreAuthorize("@security.canEditProject(#projectId)")
    public Flux<String> streamResponse(String message, Long projectId) {

        Long userId = authUtil.getCurrentUserId();
        ChatSession chatSession = createChatSessionIfNotExists(projectId, userId);

        Map<String, Object> advisorParams = Map.of(
                "userId", userId,
                "projectId", projectId
        );
        StringBuilder fullResponseBuffer = new StringBuilder();

        CodeGenerationTools codeGenerationTools = new CodeGenerationTools(projectFileService,projectId);

        return chatClient.prompt()
                .system(Prompt.CODE_GENERATION_SYSTEM_PROMPT)
                .user(message)
                .tools(codeGenerationTools)
                .advisors(advisorSpec -> {
                            advisorSpec.params(advisorParams);
                            advisorSpec.advisors(fileTreeContextAdvisor);
                        }
                ).stream()
                .chatResponse()
                .doOnNext(chatResponse -> {
                    String content = chatResponse.getResult().getOutput().getText();
                    fullResponseBuffer.append(content);

                })
                .doOnComplete(() -> {
                    log.info("✅ STREAM COMPLETED");
                    Schedulers.boundedElastic().schedule(() -> {
                        parseAndSaveFiles(fullResponseBuffer.toString(), projectId);
                        finalizeChats(userMessage,chatSession,fullResponseBuffer.toString(), projectId );
                    });


                })
                // ✅ Fixed error logging
                .doOnError(error -> log.error("❌ Error during Streaming for Project {}", projectId, error))
                .map(chatResponse -> Objects.requireNonNull(chatResponse.getResult().getOutput().getText()));


    }

    private void finalizeChats(String userMessage,ChatSession chatSession,String fullText, Long projectId){

        chatMessageRepository.save(
                ChatMessage.builder()
                        .chatSession(chatSession)
                        .role(MessageRole.USER)
                        .content(userMessage)
                        .build()


        );

        ChatMessage assistantChatMessage = ChatMessage.builder()
                .role(MessageRole.ASSISTANT)
                .chatSession(chatSession)
                .build();

        List<ChatEvent> chatEventList = llmResponseParser(fullText,assistantChatMessage);

        chatEventList.stream()
                .filter(e -> e.getType() == ChatEventType.FILE_EDIT)
                .forEach(e -> projectFileService.saveFile(projectId, e.getFilePath(), e.getContent()));

        chatEventRepository.saveAll(chatEventList);


    }

    private void parseAndSaveFiles(String userMessage, ChatSession chatSession, String fullText, Long project){

    }







    private void parseAndSaveFiles(String fullResponse, Long projectId) {

        log.info("📂 Parsing full response...");

        Matcher matcher = FILE_TAG_PATTERN.matcher(fullResponse);

        boolean found = false;

        while (matcher.find()) {
            found = true;

            String filepath = matcher.group(1);
            String fileContent = matcher.group(2).trim();

            log.info("📄 Saving file: {} (content length: {})",
                    filepath, fileContent.length());

            projectFileService.saveFile(projectId, filepath, fileContent);
        }

        if (!found) {
            log.error("❌ No file patterns matched in LLM response!");
        }
    }


    private void createChatSessionIfNotExists(Long projectId, Long userId) {

        ChatSessionId chatSessionId = new ChatSessionId(projectId, userId);
        ChatSession chatSession = chatSessionRepository.findById(chatSessionId).orElse(null);

        if(chatSession == null){
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new ResourceNotFoundException("project", projectId.toString()));

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("user", userId.toString()));

            chatSession = ChatSession.builder()
                    .id(chatSessionId)
                    .project(project)
                    .user(user)
                    .build();

            chatSession = chatSessionRepository.save(chatSession);

        }
        return chatSession;






    }
}

