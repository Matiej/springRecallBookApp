package com.testaarosa.springRecallBookApp.security;

import com.testaarosa.springRecallBookApp.user.dataBase.UserEntityJpaRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@AllArgsConstructor
class BookAppUserDetailService implements UserDetailsService {

    private final UserEntityJpaRepository userEntityJpaRepository;
    private final DefaultAdmin defaultAdmin;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(defaultAdmin.getUsername().equalsIgnoreCase(username)) {
            return defaultAdmin.adminToUser();
        }
        return userEntityJpaRepository.findByUsernameIgnoreCase(username)
                .map(UserEntityDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("Cannot find user: " + username));
    }
}
