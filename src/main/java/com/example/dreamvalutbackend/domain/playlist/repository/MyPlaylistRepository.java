package com.example.dreamvalutbackend.domain.playlist.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.dreamvalutbackend.domain.playlist.domain.MyPlaylist;
import com.example.dreamvalutbackend.domain.playlist.domain.Playlist;

public interface MyPlaylistRepository extends JpaRepository<MyPlaylist, Long> {

    void deleteByPlaylist(Playlist playlist);
}
