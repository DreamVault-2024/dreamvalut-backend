package com.example.dreamvalutbackend.domain.playlist.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.dreamvalutbackend.domain.playlist.domain.Playlist;
import com.example.dreamvalutbackend.domain.playlist.domain.PlaylistTrack;

public interface PlaylistTrackRepository extends JpaRepository<PlaylistTrack, Long> {

    @Query("SELECT pt FROM PlaylistTrack pt WHERE pt.playlist.id = :playlistId ORDER BY pt.createdAt DESC")
    Page<PlaylistTrack> findAllByPlaylistId(@Param("playlistId") Long playlistId, Pageable pageable);

    void deleteByPlaylist(Playlist playlist);
}
