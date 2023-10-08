package com.test.teamlog.domain.postmedia.service;

import com.test.teamlog.domain.postmedia.repository.PostMediaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostMediaQueryService {
    private final PostMediaRepository postMediaRepository;
}
