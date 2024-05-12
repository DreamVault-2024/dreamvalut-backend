package com.example.dreamvalutbackend.domain.playlist.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.dreamvalutbackend.domain.playlist.domain.Playlist;
import com.example.dreamvalutbackend.domain.playlist.domain.PlaylistTrack;
import com.example.dreamvalutbackend.domain.track.domain.Track;

public interface PlaylistTrackRepository extends JpaRepository<PlaylistTrack, Long> {

    Page<PlaylistTrack> findAllByPlaylistId(Long playlistId, Pageable pageable);

    void deleteByPlaylist(Playlist playlist);

    boolean existsByPlaylistAndTrack(Playlist playlist, Track track);

    Optional<PlaylistTrack> findByPlaylistAndTrack(Playlist playlist, Track track);

    List<PlaylistTrack> findByPlaylist(Playlist playlist);
}
