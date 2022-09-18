package com.guiwoo.stock_dividend.service;

import com.guiwoo.stock_dividend.exception.impl.user.AlreadyHasTakenUserException;
import com.guiwoo.stock_dividend.exception.impl.user.CanNotFindUsername;
import com.guiwoo.stock_dividend.exception.impl.user.PasswordDoesNotMatch;
import com.guiwoo.stock_dividend.model.Auth;
import com.guiwoo.stock_dividend.persist.entity.MemberEntity;
import com.guiwoo.stock_dividend.persist.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Can't find username -> " + username));
    }

    public MemberEntity memberRegister(Auth.SignUp signUp){
        boolean exists = memberRepository.existsByUsername(signUp.getUsername());
        if(exists){
            throw new AlreadyHasTakenUserException();
        }
        signUp.setPassword(passwordEncoder.encode(signUp.getPassword()));
        return memberRepository.save(signUp.toEntity());
    }
    public MemberEntity Authenticate(Auth.SignIn signIn){
        MemberEntity m = memberRepository.findByUsername(signIn.getUsername())
                .orElseThrow(() ->new CanNotFindUsername(signIn.getUsername()));
        if(!passwordEncoder.matches(signIn.getPassword(),m.getPassword())){
            throw new PasswordDoesNotMatch();
        }
        return m;
    }
}
