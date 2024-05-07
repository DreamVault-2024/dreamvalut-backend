package com.example.dreamvalutbackend.domain.like.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.dreamvalutbackend.domain.like.domain.Like;
import com.example.dreamvalutbackend.domain.track.domain.Track;
import com.example.dreamvalutbackend.domain.user.domain.User;


public interface LikeRepository extends JpaRepository<Like, Long> {
	Optional<Like> findByUserAndTrack(User user, Track track);

	Long countByTrackId(Long trackId);

	@Query("SELECT count(l) > 0 FROM Like l WHERE l.user.userId = :userId AND l.track.id = :trackId")
	Boolean existsByUserIdAndTrackId(@Param("userId") Long userId, @Param("trackId") Long trackId);


	@Query("SELECT l.track FROM Like l WHERE l.user.userId = :userId ORDER BY l.id DESC")
	List<Track> findTracksByUserId(@Param("userId") Long userId, Pageable pageable);

	@Query("SELECT l.track FROM Like l WHERE l.user.userId = :userId ORDER BY l.id DESC")
	Page<Track> findTopLikedTracksByUserId(@Param("userId") Long userId, Pageable pageable);

}
