package roomescape.service;

import jakarta.servlet.http.Cookie;
import java.util.List;
import org.springframework.stereotype.Service;
import roomescape.controller.request.UserLoginRequest;
import roomescape.controller.request.UserSignUpRequest;
import roomescape.controller.response.CheckMemberResponse;
import roomescape.controller.response.MemberResponse;
import roomescape.controller.response.TokenResponse;
import roomescape.domain.Member;
import roomescape.infrastructure.JwtTokenProvider;
import roomescape.repository.MemberRepository;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public MemberService(MemberRepository memberRepository, JwtTokenProvider jwtTokenProvider) {
        this.memberRepository = memberRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public MemberResponse save(UserSignUpRequest userSignUpRequest) {
        Member member = userSignUpRequest.toEntity();
        Member savedMember = memberRepository.save(member);
        return MemberResponse.from(savedMember);
    }

    public TokenResponse createToken(UserLoginRequest userLoginRequest) {
        if (!checkInvalidLogin(userLoginRequest.email(), userLoginRequest.password())) {
            throw new IllegalArgumentException("사용자 없음");
        }
        Member member = memberRepository.findByEmail(userLoginRequest.email());
        return new TokenResponse(jwtTokenProvider.createToken(member));
    }

    private boolean checkInvalidLogin(String email, String password) {
        return memberRepository.checkExistMember(email, password);
    }

    public CheckMemberResponse findById(Long id) {
        Member member = memberRepository.findById(id).orElseThrow();
        return new CheckMemberResponse(member.getName());
    }

    public List<MemberResponse> findAll() {
        List<Member> members = memberRepository.findAll();

        return members.stream()
                .map(MemberResponse::from)
                .toList();
    }

    public CheckMemberResponse findByCookies(Cookie[] cookies) {
        Long memberId = jwtTokenProvider.getMemberIdByCookie(cookies);
        return findById(memberId);
    }
}
