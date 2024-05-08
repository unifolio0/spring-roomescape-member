package roomescape.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.controller.request.UserLoginRequest;
import roomescape.controller.request.UserSignUpRequest;
import roomescape.controller.response.MemberResponse;
import roomescape.controller.response.UserResponse;
import roomescape.service.MemberService;

@RestController
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/members")
    public ResponseEntity<List<MemberResponse>> findAll() {
        List<MemberResponse> memberResponses = memberService.findAll();

        return ResponseEntity.ok(memberResponses);
    }

    @PostMapping("/members")
    public ResponseEntity<Void> save(@RequestBody UserSignUpRequest userSignUpRequest) {
        memberService.save(userSignUpRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<Void> userLogin(HttpServletResponse response, @RequestBody UserLoginRequest userLoginRequest) {
        String accessToken = memberService.createToken(userLoginRequest);
        Cookie cookie = new Cookie("token", accessToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/login/check")
    public ResponseEntity<UserResponse> checkLogin(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        UserResponse userResponse = memberService.findByCookies(cookies);
        return ResponseEntity.ok(userResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> userLogout(HttpServletResponse response) {
        Cookie cookie = new Cookie("token", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        return ResponseEntity.ok().build();
    }
}
