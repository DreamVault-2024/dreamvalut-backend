package com.example.dreamvalutbackend.domain.tag.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.dreamvalutbackend.domain.tag.domain.TrackTag;

public interface TrackTagRepository extends JpaRepository<TrackTag, Long> {

    @Query("SELECT tt FROM TrackTag tt WHERE tt.tag.tagName = :tagName")
    List<TrackTag> findByTagName(@Param("tagName") String tagName);
}
