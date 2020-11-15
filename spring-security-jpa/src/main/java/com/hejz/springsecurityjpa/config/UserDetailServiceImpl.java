package com.hejz.springsecurityjpa.config;

import com.hejz.springsecurityjpa.entity.User;
import com.hejz.springsecurityjpa.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public MyUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        //如果用户不存在抛出异常
        user.orElseThrow(() ->  new UsernameNotFoundException("用户不存在:"+username));
        return user.map(MyUserDetails::new).get();
    }
}
