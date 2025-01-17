package com.study.mms.auth;

import java.beans.Customizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import org.springframework.data.querydsl.binding.QuerydslBinderCustomizerDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.study.mms.model.User;

import lombok.Getter;

@Getter
public class PrincipalDetail implements UserDetails {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private User user;

    public PrincipalDetail(User user) {
        this.user = user;
    }

    @Override
    public String getPassword() {
        // TODO Auto-generated method stub
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    public String getNickname() {
        return user.getNickname();
    }

    public Integer getPrimaryKey() {
        return user.getId();
    }

    public String getEmail() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean isEnabled() {
        // TODO Auto-generated method stub
        return true;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collect = new ArrayList<>();
        collect.add(new GrantedAuthority() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getAuthority() {
                return user.getRole();
            }
        });
        return collect;
    }

    //세션 중복 로그인 처리 시 비교를 위해 아래 코드 추가
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PrincipalDetail)) return false;
        PrincipalDetail that = (PrincipalDetail) o;
        return this.getUsername().equals(that.getUsername());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getUsername());
    }

}
