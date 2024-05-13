package com.example.dreamvalutbackend.domain.playlist.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.dreamvalutbackend.domain.playlist.domain.Playlist;

public interface PlaylistRepository extends JpaRepository<Playlist, Long>, PlaylistRepositoryCustom {

    Page<Playlist> findByIsCuratedTrue(Pageable pageable);

    Page<Playlist> findByIsCuratedFalseAndIsPublicTrue(Pageable pageable);

    Page<Playlist> findAllByUser_UserId(Long userId, Pageable pageable);

    Page<Playlist> findAllByIdInAndIsCurated(List<Long> ids, Boolean isCurated, Pageable pageable);
}

