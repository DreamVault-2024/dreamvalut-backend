package com.example.dreamvalutbackend.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.dreamvalutbackend.domain.user.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUserEmail(String userEmail);

	@Query("select m from User m where m.socialId = :socialId")
	Optional<User> findBySocialId(@Param("socialId") String socialId);


}
