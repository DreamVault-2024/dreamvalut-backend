package com.example.dreamvalutbackend.redis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.dreamvalutbackend.redis.domain.Token;

@Repository
public interface TokenRepository extends CrudRepository<Token, Long> {
}
