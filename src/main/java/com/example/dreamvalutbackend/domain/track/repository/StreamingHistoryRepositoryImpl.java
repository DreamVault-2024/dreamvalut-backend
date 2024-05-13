package com.example.dreamvalutbackend.domain.track.repository;

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


}
