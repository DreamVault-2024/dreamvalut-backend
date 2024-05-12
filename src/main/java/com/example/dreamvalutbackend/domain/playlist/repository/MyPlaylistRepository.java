package com.example.dreamvalutbackend.domain.playlist.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.dreamvalutbackend.domain.playlist.domain.MyPlaylist;
import com.example.dreamvalutbackend.domain.playlist.domain.Playlist;
import com.example.dreamvalutbackend.domain.user.domain.User;

public interface MyPlaylistRepository extends JpaRepository<MyPlaylist, Long> {

    void deleteByPlaylist(Playlist playlist);

    boolean existsByUserAndPlaylist(User user, Playlist playlist);

    Optional<MyPlaylist> findByUserAndPlaylist(User user, Playlist playlist);

    Page<MyPlaylist> findAllByUser_UserId(Long userId, Pageable pageable);
}
