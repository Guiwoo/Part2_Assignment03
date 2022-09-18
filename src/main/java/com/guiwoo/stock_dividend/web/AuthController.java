package com.guiwoo.stock_dividend.web;

import com.guiwoo.stock_dividend.model.Auth;
import com.guiwoo.stock_dividend.persist.entity.MemberEntity;
import com.guiwoo.stock_dividend.security.TokenProvider;
import com.guiwoo.stock_dividend.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final MemberService memberService;
    private final TokenProvider tokenProvider;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Auth.SignUp req){
        MemberEntity memberEntity = memberService.memberRegister(req);
        log.info("Member Registered");
        return ResponseEntity.ok(memberEntity);
    }
    @PostMapping("/signin")
    public ResponseEntity<?>signin(@RequestBody Auth.SignIn req){
        MemberEntity m = memberService.Authenticate(req);
        String token = tokenProvider.generateToken(m.getUsername(), m.getRoles());
        return ResponseEntity.ok(token);

    }
}
