package com.example.dreamvalutbackend.domain.track.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.dreamvalutbackend.domain.track.domain.TrackDetail;

@Repository
public interface TrackDetailRepository extends JpaRepository<TrackDetail, Long> {

}
