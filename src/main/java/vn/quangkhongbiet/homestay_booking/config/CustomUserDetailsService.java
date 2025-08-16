package vn.quangkhongbiet.homestay_booking.config;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import vn.quangkhongbiet.homestay_booking.service.user.UserService;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.EntityNotFoundException;
import vn.quangkhongbiet.homestay_booking.web.rest.errors.UnauthorizedException;



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
            throw new EntityNotFoundException("Invalid User", "login", "email_not_found");
        }

        if(!user.getVerified()){
            throw new UnauthorizedException("User is not verified", "user", "not_verified");
        }

        return new User(
            user.getEmail(),
            user.getPassword(),
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }
   
}
