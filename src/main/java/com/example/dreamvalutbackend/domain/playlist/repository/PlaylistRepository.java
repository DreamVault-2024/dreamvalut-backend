package com.example.dreamvalutbackend.domain.playlist.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.dreamvalutbackend.domain.playlist.domain.Playlist;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {

}