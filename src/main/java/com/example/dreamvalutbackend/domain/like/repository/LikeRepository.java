package com.example.dreamvalutbackend.domain.like.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.dreamvalutbackend.domain.like.domain.Like;
import com.example.dreamvalutbackend.domain.track.domain.Track;
import com.example.dreamvalutbackend.domain.user.domain.User;

public interface LikeRepository extends JpaRepository<Like, Long> {
	Optional<Like> findByUserAndTrack(User user, Track track);

	Long countByTrackId(Long trackId);
}
