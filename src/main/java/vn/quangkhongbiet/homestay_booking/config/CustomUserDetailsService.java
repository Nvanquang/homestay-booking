package vn.quangkhongbiet.homestay_booking.config;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import vn.quangkhongbiet.homestay_booking.service.user.UserService;



@Component("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService{

    private final UserService userService;   

    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        vn.quangkhongbiet.homestay_booking.domain.user.entity.User user = this.userService.findUserByEmail(email);
        
        if (user == null) {
            throw new UsernameNotFoundException("Invalid User");

        }

        return new User(
            user.getEmail(),
            user.getPassword(),
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }
   
}
