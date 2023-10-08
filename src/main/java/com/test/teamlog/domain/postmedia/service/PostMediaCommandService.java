package com.test.teamlog.domain.postmedia.service;

import com.test.teamlog.domain.postmedia.repository.PostMediaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostMediaCommandService {
    private final PostMediaRepository postMediaRepository;
}
