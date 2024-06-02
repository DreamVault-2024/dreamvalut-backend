package com.example.dreamvalutbackend.domain.track.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.example.dreamvalutbackend.domain.track.domain.QStreamingHistory;
import com.example.dreamvalutbackend.domain.track.domain.QTrack;
import com.example.dreamvalutbackend.domain.track.domain.Track;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

@Repository
public class StreamingHistoryRepositoryImpl implements StreamingHistoryRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	@Autowired
	public StreamingHistoryRepositoryImpl(EntityManager em) {
		this.queryFactory = new JPAQueryFactory(em);
	}

	@Override
	public Page<Track> findPopularTracks(Pageable pageable) {
		QStreamingHistory streamingHistory = QStreamingHistory.streamingHistory;
		QTrack track = QTrack.track;

		List<Long> trackIds = queryFactory
			.select(streamingHistory.track.id)
			.from(streamingHistory)
			.groupBy(streamingHistory.track.id)
			.orderBy(streamingHistory.id.count().desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		if (trackIds.isEmpty()) {
			return Page.empty(pageable);
		}

		List<Track> tracks = queryFactory
			.selectFrom(track)
			.where(track.id.in(trackIds))
			.fetch();

		List<Track> sortedTracks = trackIds.stream()
			.map(id -> tracks.stream()
				.filter(t -> t.getId().equals(id))
				.findFirst()
				.orElse(null))
			.filter(Objects::nonNull)
			.collect(Collectors.toList());

		long total = queryFactory
			.selectFrom(streamingHistory)
			.fetchCount();

		return new PageImpl<>(sortedTracks, pageable, total);
	}

	@Override
	public Page<Track> findDistinctAndRecentTracksByUserId(Long userId, Pageable pageable) {
		QStreamingHistory streamingHistory = QStreamingHistory.streamingHistory;
		QTrack track = QTrack.track;

		// 각 track_id에 대한 최신 created_at을 찾아서 중복을 제거
		List<Tuple> latestEntries = queryFactory
			.select(streamingHistory.track.id, streamingHistory.createdAt.max().as("latestCreatedAt"))
			.from(streamingHistory)
			.where(streamingHistory.user.id.eq(userId))
			.groupBy(streamingHistory.track.id)
			.fetch();

		// 최신 created_at 기준으로 정렬된 track_id 리스트 추출
		List<Long> trackIds = latestEntries.stream()
			.sorted((t1, t2) -> {
				LocalDateTime t1Date = t1.get(1, LocalDateTime.class);
				LocalDateTime t2Date = t2.get(1, LocalDateTime.class);
				return t2Date.compareTo(t1Date); // 최신순 정렬
			})
			.map(tuple -> tuple.get(streamingHistory.track.id))
			.skip(pageable.getOffset())
			.limit(pageable.getPageSize())
			.collect(Collectors.toList());

		if (trackIds.isEmpty()) {
			return Page.empty(pageable);
		}

		// 트랙 세부 정보 조회
		List<Track> tracks = queryFactory
			.selectFrom(track)
			.where(track.id.in(trackIds))
			.fetch();

		// 트랙 ID 목록의 순서대로 트랙을 정렬
		List<Track> sortedTracks = trackIds.stream()
			.map(id -> tracks.stream()
				.filter(t -> t.getId().equals(id))
				.findFirst()
				.orElse(null))
			.collect(Collectors.toList());

		// 중복 제거된 track_id를 기반으로 총 수를 계산
		long total = latestEntries.size();

		return new PageImpl<>(sortedTracks, pageable, total);
	}


}

