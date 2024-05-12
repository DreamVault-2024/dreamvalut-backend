package com.example.dreamvalutbackend.domain.track.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.dreamvalutbackend.domain.track.domain.Track;

@Repository
public interface TrackRepository extends JpaRepository<Track, Long> {

    Page<Track> findAllByGenreId(Long genreId, Pageable pageable);
}
