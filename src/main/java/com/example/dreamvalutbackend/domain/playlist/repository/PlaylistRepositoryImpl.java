package com.example.dreamvalutbackend.domain.playlist.repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.example.dreamvalutbackend.domain.playlist.domain.Playlist;
import com.example.dreamvalutbackend.domain.playlist.domain.QMyPlaylist;
import com.example.dreamvalutbackend.domain.playlist.domain.QPlaylist;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

public class PlaylistRepositoryImpl implements PlaylistRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Autowired
	public PlaylistRepositoryImpl(EntityManager em) {
		this.queryFactory = new JPAQueryFactory(em);
	}

	@Override
	public Page<Playlist> findUnionOfCreatedAndFollowedPlaylists(Long userId, Pageable pageable) {
		QPlaylist playlist = QPlaylist.playlist;
		QMyPlaylist myPlaylist = QMyPlaylist.myPlaylist;

		List<Long> createdPlaylistIds = queryFactory
			.select(playlist.id)
			.from(playlist)
			.where(playlist.user.userId.eq(userId))
			.fetch();

		List<Long> followedPlaylistIds = queryFactory
			.select(myPlaylist.playlist.id)
			.from(myPlaylist)
			.where(myPlaylist.user.userId.eq(userId))
			.fetch();

		Set<Long> unionIds = new HashSet<>(createdPlaylistIds);
		unionIds.addAll(followedPlaylistIds);

		List<Playlist> result = queryFactory
			.selectFrom(playlist)
			.where(playlist.id.in(unionIds))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		long total = queryFactory
			.selectFrom(playlist)
			.where(playlist.id.in(unionIds))
			.fetchCount();

		return new PageImpl<>(result, pageable, total);

	}



}
