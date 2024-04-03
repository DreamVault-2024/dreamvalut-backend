package com.example.dreamvalutbackend.domain.track.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.dreamvalutbackend.domain.track.domain.Track;

@Repository
public interface TrackRepository extends JpaRepository<Track, Long> {

}
