package com.example.dreamvalutbackend.domain.playlist.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.dreamvalutbackend.domain.playlist.domain.MyPlaylist;

public interface MyPlaylistRepository extends JpaRepository<MyPlaylist, Long> {

}
