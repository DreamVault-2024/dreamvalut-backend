package com.example.dreamvalutbackend.domain.track.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.dreamvalutbackend.domain.track.domain.StreamingHistory;

@Repository
public interface StreamingHistoryRepository extends JpaRepository<StreamingHistory, Long> {

}
