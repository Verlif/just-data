package idea.verlif.justdata.special.login.auth;

import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * 登录身份
 *
 * @author Verlif
 * @version 1.0
 * @date 2022/1/7 9:46
 */
public class StationAuthentication implements Authentication, CredentialsContainer {

    private final Collection<GrantedAuthority> authorities;
    private Object details;
    private boolean authenticated = false;
    private Object principal;
    private Object credentials;

    public StationAuthentication() {
        this(null);
    }

    public StationAuthentication(AuthInfo authInfo) {
        this(authInfo, null);
    }

    public StationAuthentication(AuthInfo authInfo, Collection<? extends GrantedAuthority> authorities) {
        if (authorities == null) {
            this.authorities = AuthorityUtils.NO_AUTHORITIES;
        } else {
            for (GrantedAuthority a : authorities) {
                Assert.notNull(a, "Authorities collection cannot contain any null elements");
            }
            this.authorities = Collections.unmodifiableCollection(authorities);
        }
        if (authInfo != null) {
            this.principal = authInfo.getId();
            this.credentials = authInfo.getToken();
        }
        setAuthenticated(true);
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getName() {
        if (this.getPrincipal() instanceof UserDetails) {
            return ((UserDetails) this.getPrincipal()).getUsername();
        } else if (this.getPrincipal() instanceof AuthenticatedPrincipal) {
            return ((AuthenticatedPrincipal) this.getPrincipal()).getName();
        } else if (this.getPrincipal() instanceof Principal) {
            return ((Principal) this.getPrincipal()).getName();
        } else {
            return this.getPrincipal() == null ? "" : this.getPrincipal().toString();
        }
    }

    @Override
    public boolean isAuthenticated() {
        return this.authenticated;
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    @Override
    public Object getDetails() {
        return this.details;
    }

    public void setDetails(Object details) {
        this.details = details;
        this.principal = details;
    }

    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    @Override
    public void eraseCredentials() {
        credentials = null;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof StationAuthentication)) {
            return false;
        } else {
            StationAuthentication test = (StationAuthentication) obj;
            if (!this.authorities.equals(test.authorities)) {
                return false;
            } else if (this.details == null && test.getDetails() != null) {
                return false;
            } else if (this.details != null && test.getDetails() == null) {
                return false;
            } else if (this.details != null && !this.details.equals(test.getDetails())) {
                return false;
            } else if (this.getCredentials() == null && test.getCredentials() != null) {
                return false;
            } else if (this.getCredentials() != null && !this.getCredentials().equals(test.getCredentials())) {
                return false;
            } else if (this.getPrincipal() == null && test.getPrincipal() != null) {
                return false;
            } else if (this.getPrincipal() != null && !this.getPrincipal().equals(test.getPrincipal())) {
                return false;
            } else {
                return this.isAuthenticated() == test.isAuthenticated();
            }
        }
    }

    @Override
    public int hashCode() {
        int code = 31;

        GrantedAuthority authority;
        for (Iterator<GrantedAuthority> var2 = this.authorities.iterator(); var2.hasNext(); code ^= authority.hashCode()) {
            authority = var2.next();
        }

        if (this.getPrincipal() != null) {
            code ^= this.getPrincipal().hashCode();
        }

        if (this.getCredentials() != null) {
            code ^= this.getCredentials().hashCode();
        }

        if (this.getDetails() != null) {
            code ^= this.getDetails().hashCode();
        }

        if (this.isAuthenticated()) {
            code ^= -37;
        }

        return code;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " [" +
                "Principal=" + this.getPrincipal() + ", " +
                "Credentials=[PROTECTED], " +
                "Authenticated=" + this.isAuthenticated() + ", " +
                "Details=" + this.getDetails() + ", " +
                "Granted Authorities=" + this.authorities +
                "]";
    }
}
