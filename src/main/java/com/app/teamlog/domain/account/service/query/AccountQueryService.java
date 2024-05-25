package com.app.teamlog.domain.account.service.query;

import com.app.teamlog.domain.account.model.Account;
import com.app.teamlog.domain.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountQueryService {
    private final AccountRepository accountRepository;

    public Optional<Account> findByIdx(Long idx) {
        return accountRepository.findById(idx);
    }

    public Optional<Account> findByIdentification(String identification) {
        return accountRepository.findByIdentification(identification);
    }

    public List<Account> findAllByIdentificationIn(List<String> identificationList) {
        return accountRepository.findAllByIdentificationIn(identificationList);
    }

}
