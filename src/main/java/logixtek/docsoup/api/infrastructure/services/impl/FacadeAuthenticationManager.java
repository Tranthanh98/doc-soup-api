package logixtek.docsoup.api.infrastructure.services.impl;

import logixtek.docsoup.api.infrastructure.services.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class FacadeAuthenticationManager implements AuthenticationManager{

    @Override
    public Authentication getAuthentication() {
       
        return SecurityContextHolder.getContext().getAuthentication();
       
    }
    
}
