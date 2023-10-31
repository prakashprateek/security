package com.berriesoft.springsecurity.auth;

import com.berriesoft.springsecurity.status.ErrorInfo;
import com.berriesoft.springsecurity.user.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorisationService {

    public User getUser()
    {
        User curUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return curUser;
    }

    public boolean checkPrivilege(HttpServletRequest request, String role){
        if (request.isUserInRole(role)){
            return true;
        }
        return false;
    }

    public boolean authorisePrivilege(HttpServletRequest request, String role) throws IllegalAccessException{
        if (request.isUserInRole(role)){
            return true;
        }
        else{
            List<ErrorInfo> errorInfo = new ArrayList<ErrorInfo>();
            errorInfo.add(new ErrorInfo(ErrorInfo.ILLEGAL_ACCESS_CODE, ErrorInfo.ILLEGAL_ACCESS_MESSAGE + " Only " + role + " can execute this operation"));
            throw new IllegalAccessException(errorInfo.toString());
        }
    }



}
