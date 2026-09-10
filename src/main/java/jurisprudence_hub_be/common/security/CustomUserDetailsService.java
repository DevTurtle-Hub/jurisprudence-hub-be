package jurisprudence_hub_be.common.security;

import jurisprudence_hub_be.common.constant.SecurityConstants;
import jurisprudence_hub_be.module.auth.entity.User;
import jurisprudence_hub_be.module.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(SecurityConstants.MSG_USER_NOT_FOUND_BY_EMAIL + email));

        return UserPrincipal.create(user);
    }
}
