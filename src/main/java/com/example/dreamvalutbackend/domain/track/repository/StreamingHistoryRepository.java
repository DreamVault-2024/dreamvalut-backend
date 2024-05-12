package com.example.dreamvalutbackend.domain.track.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.dreamvalutbackend.domain.track.domain.StreamingHistory;
import com.example.dreamvalutbackend.domain.track.domain.Track;

@Repository
public interface StreamingHistoryRepository extends JpaRepository<StreamingHistory, Long> {
	@Query("SELECT sh.track FROM StreamingHistory sh WHERE sh.user.userId = :userId ORDER BY sh.createdAt DESC")
	Page<Track> findTracksByUserId(Long userId, Pageable pageable);

}
