package com.example.dreamvalutbackend.domain.playlist.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.dreamvalutbackend.domain.playlist.domain.PlaylistTrack;

public interface PlaylistTrackRepository extends JpaRepository<PlaylistTrack, Long> {

}
