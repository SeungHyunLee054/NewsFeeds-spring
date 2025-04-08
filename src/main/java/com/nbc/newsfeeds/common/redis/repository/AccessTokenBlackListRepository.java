package com.nbc.newsfeeds.common.redis.repository;

import org.springframework.data.repository.CrudRepository;

import com.nbc.newsfeeds.common.redis.vo.AccessTokenBlackList;

public interface AccessTokenBlackListRepository extends CrudRepository<AccessTokenBlackList, String> {
}
