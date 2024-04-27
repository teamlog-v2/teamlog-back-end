package com.test.teamlog.domain.account.service;

import com.test.teamlog.domain.account.dto.*;
import com.test.teamlog.domain.account.model.Account;
import com.test.teamlog.domain.account.repository.AccountRepository;
import com.test.teamlog.domain.file.info.entity.FileInfo;
import com.test.teamlog.domain.file.management.service.FileManagementService;
import com.test.teamlog.domain.token.dto.CreateTokenResult;
import com.test.teamlog.domain.token.service.TokenService;
import com.test.teamlog.domain.accountfollow.service.query.AccountFollowQueryService;
import com.test.teamlog.global.dto.ApiResponse;
import com.test.teamlog.global.exception.ResourceAlreadyExistsException;
import com.test.teamlog.global.exception.ResourceNotFoundException;
import com.test.teamlog.global.utility.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;

    private final TokenService tokenService;
    private final AccountFollowQueryService accountFollowQueryService;
    private final FileManagementService fileManagementService;

    public List<AccountSearchResult> search(String id, String name) {
        List<Account> accountList = accountRepository.searchAccountByIdentificationAndName(id, name);

        return accountList.stream().map(AccountSearchResult::from).toList();
    }

    public AccountValidateResult validate(Long accountId) {
        final Account currentAccount = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("ACCOUNT"));

        return AccountValidateResult.from(currentAccount);
    }

    @Transactional(readOnly = true)
    public AccountReadDetailResult readDetail(String identification, Account currentAccount) {
        Account account = accountRepository.findByIdentification(identification)
                .orElseThrow(() -> new ResourceNotFoundException("ACCOUNT"));

        AccountReadDetailResult result = AccountReadDetailResult.from(account);

        if (currentAccount == null || !identification.equals(currentAccount.getIdentification())) {
            result.setIsMe(currentAccount != null ? Boolean.FALSE : null);
            result.setIsFollow(currentAccount != null ? accountFollowQueryService.isFollow(currentAccount, account) : null);
        } else {
            result.setIsMe(Boolean.TRUE);
            result.setIsFollow(Boolean.FALSE);
        }

        return result;
    }

    @Transactional
    public SignInResult signIn(SignInInput input) {
        final String identification = input.getIdentification();
        Account account = accountRepository.findByIdentification(identification).orElse(null);

        // FIXME: 추후 Exception 바꿀 예정. 프론트와 같이 바꿔야 한다.
        if (account == null || !PasswordUtil.matches(input.getPassword(), account.getPassword())) {
            throw new ResourceNotFoundException("ACCOUNT");
        }

        final CreateTokenResult createTokenResult = tokenService.createToken(identification);
        return SignInResult.from(createTokenResult);
    }

    // 회원 가입
    @Transactional
    public SignUpResult signUp(SignUpInput input) {
        checkIdDuplication(input.getIdentification());

        final Account account = input.toAccount();
        accountRepository.save(account);

        return SignUpResult.from(account);
    }

    @Transactional
    public ApiResponse updateAccount(AccountUpdateRequest request, MultipartFile image, Account currentAccount) throws IOException {
        // FIXME: LazyInitializationException 이슈로 잠시 추가한 것으로 개선 필요
        final Account account = accountRepository.findByIdentification(currentAccount.getIdentification())
                .orElseThrow(() -> new ResourceNotFoundException("ACCOUNT"));

        final FileInfo profileImage = image != null ? fileManagementService.uploadFile(image) : null;

        account.update(request.getName(), request.getIntroduction(), profileImage);

        return new ApiResponse(Boolean.TRUE, "사용자 정보 수정 성공");
    }

    @Transactional
    public ApiResponse updateProfileImage(MultipartFile image, Account currentAccount) throws IOException {
        currentAccount.updateProfileImage(fileManagementService.uploadFile(image));

        accountRepository.save(currentAccount);
        return new ApiResponse(Boolean.TRUE, "프로필 이미지 수정 성공");
    }

    @Transactional
    public ApiResponse deleteProfileImage(Account currentAccount) {
        currentAccount.updateProfileImage(null);

        accountRepository.save(currentAccount);
        return new ApiResponse(Boolean.TRUE, "프로필 이미지 삭제 성공");
    }

    //회원 탈퇴
    @Transactional
    public ApiResponse deleteAccount(Account currentAccount) {
        accountRepository.delete(currentAccount);
        return new ApiResponse(Boolean.TRUE, "회원 탈퇴 성공");
    }

    public Account readByIdentification(String identification) {
        return accountRepository.findByIdentification(identification)
                .orElseThrow(() -> new ResourceNotFoundException("ACCOUNT"));
    }

    public List<Account> readAllByIdentificationIn(List<String> identificationList) {
        return accountRepository.findAllByIdentificationIn(identificationList);
    }

    // identification 중복 체크
    private void checkIdDuplication(String identification) {
        if (accountRepository.findByIdentification(identification).isPresent()) {
            throw new ResourceAlreadyExistsException("이미 존재하는 회원입니다.");
        }
    }
}
