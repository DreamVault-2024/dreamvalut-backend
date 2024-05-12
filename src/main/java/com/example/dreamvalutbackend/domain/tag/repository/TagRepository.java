package com.example.dreamvalutbackend.domain.tag.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.dreamvalutbackend.domain.tag.domain.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findByTagName(String tagName);
}
