package com.example.dreamvalutbackend.domain.track.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.dreamvalutbackend.domain.track.domain.Track;

public interface StreamingHistoryRepositoryCustom {
	Page<Track> findPopularTracks(Pageable pageable);

	Page<Track> findDistinctAndRecentTracksByUserId(Long userId, Pageable pageable);
}
