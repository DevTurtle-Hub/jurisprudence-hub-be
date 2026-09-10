package jurisprudence_hub_be.common.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jurisprudence_hub_be.common.constant.SecurityConstants;
import jurisprudence_hub_be.module.auth.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserPrincipal implements UserDetails {

    private final String id;
    private final String email;
    @JsonIgnore
    private final String password;
    private final String name;
    private final String role;
    private final String unit;
    private final Collection<? extends GrantedAuthority> authorities;
    private final boolean enabled;
    private final boolean accountNonLocked;

    public UserPrincipal(
            String id,
            String email,
            String password,
            String name,
            String role,
            String unit,
            Collection<? extends GrantedAuthority> authorities,
            boolean enabled,
            boolean accountNonLocked
    ) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
        this.unit = unit;
        this.authorities = authorities;
        this.enabled = enabled;
        this.accountNonLocked = accountNonLocked;
    }

    /**
     * Chuyển đổi trực tiếp từ Entity User sang UserPrincipal
     */
    public static UserPrincipal create(User user) {
        String roleName = user.getRole() != null ? user.getRole().name() : SecurityConstants.DEFAULT_ROLE;
        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority(SecurityConstants.ROLE_PREFIX + roleName)
        );

        return new UserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getName(),
                roleName,
                user.getUnit(),
                authorities,
                user.isActive(),
                true
        );
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String email;
        private String password;
        private String name;
        private String role;
        private String unit;
        private Collection<? extends GrantedAuthority> authorities;
        private boolean enabled = true;
        private boolean accountNonLocked = true;

        public Builder id(String id) { this.id = id; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder password(String password) { this.password = password; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder role(String role) { this.role = role; return this; }
        public Builder unit(String unit) { this.unit = unit; return this; }
        public Builder authorities(Collection<? extends GrantedAuthority> authorities) { this.authorities = authorities; return this; }
        public Builder enabled(boolean enabled) { this.enabled = enabled; return this; }
        public Builder accountNonLocked(boolean accountNonLocked) { this.accountNonLocked = accountNonLocked; return this; }

        public UserPrincipal build() {
            return new UserPrincipal(id, email, password, name, role, unit, authorities, enabled, accountNonLocked);
        }
    }

    public String getId() { return id; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getRole() { return role; }
    public String getUnit() { return unit; }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
