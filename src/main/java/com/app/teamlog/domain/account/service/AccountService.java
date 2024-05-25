package com.app.teamlog.domain.account.service;

import com.app.teamlog.domain.account.dto.*;
import com.app.teamlog.domain.account.model.Account;
import com.app.teamlog.domain.account.repository.AccountRepository;
import com.app.teamlog.domain.accountfollow.service.query.AccountFollowQueryService;
import com.app.teamlog.domain.file.info.entity.FileInfo;
import com.app.teamlog.domain.file.management.service.FileManagementService;
import com.app.teamlog.domain.token.dto.CreateTokenResult;
import com.app.teamlog.domain.token.service.TokenService;
import com.app.teamlog.global.dto.ApiResponse;
import com.app.teamlog.global.exception.BadRequestException;
import com.app.teamlog.global.exception.ResourceAlreadyExistsException;
import com.app.teamlog.global.exception.ResourceNotFoundException;
import com.app.teamlog.global.utility.PasswordUtil;
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
        final Account currentAccount = prepareAccount(accountId);

        return AccountValidateResult.from(currentAccount);
    }

    @Transactional(readOnly = true)
    public AccountReadDetailResult readDetail(String identification, Account currentAccount) {
        Account account = accountRepository.findByIdentification(identification)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 회원입니다. identification: " + identification));

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
            throw new BadRequestException("아이디 또는 비밀번호가 일치하지 않습니다.");
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
        final Account account = prepareAccount(currentAccount.getIdx());
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

    private Account prepareAccount(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new BadRequestException("존재하지 않는 회원입니다. id: " + accountId));
    }

    /**
     * 아이디 중복 체크
     * @param identification
     */
    private void checkIdDuplication(String identification) {
        if (accountRepository.findByIdentification(identification).isPresent()) {
            throw new ResourceAlreadyExistsException("이미 존재하는 회원입니다.");
        }
    }
}
