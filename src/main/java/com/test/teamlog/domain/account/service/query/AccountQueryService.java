package com.test.teamlog.domain.account.service.query;

import com.test.teamlog.domain.account.model.User;
import com.test.teamlog.domain.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountQueryService {
    private final AccountRepository accountRepository;

    public Optional<User> findByIdx(Long idx) {
        return accountRepository.findById(idx);
    }

    public List<User> findUsersNotInProjectMember(Long projectId) {
        return accountRepository.findUsersNotInProjectMember(projectId);
    }

    public Optional<User> findByIdentification(String identification) {
        return accountRepository.findByIdentification(identification);
    }

    public List<User> findAllByIdentificationIn(List<String> identificationList) {
        return accountRepository.findAllByIdentificationIn(identificationList);
    }

}
