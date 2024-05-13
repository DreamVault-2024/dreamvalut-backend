package com.example.dreamvalutbackend.domain.playlist.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.dreamvalutbackend.domain.playlist.domain.Playlist;


public interface PlaylistRepositoryCustom {
	Page<Playlist> findUnionOfCreatedAndFollowedPlaylists(Long userId, Pageable pageable);

}
