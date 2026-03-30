package com.MayukhProjects.lovable_clone.repository;

import com.MayukhProjects.lovable_clone.entity.ChatEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatEventRepository extends JpaRepository<ChatEvent,Long> {

}
