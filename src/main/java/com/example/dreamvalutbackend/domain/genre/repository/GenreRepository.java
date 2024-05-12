package com.example.dreamvalutbackend.domain.genre.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.dreamvalutbackend.domain.genre.domain.Genre;

public interface GenreRepository extends JpaRepository<Genre, Long> {

    Page<Genre> findAll(Pageable pageable);
}
