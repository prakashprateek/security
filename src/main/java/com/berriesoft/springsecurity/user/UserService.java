package com.berriesoft.springsecurity.user;

import com.berriesoft.springsecurity.auth.AuthenticationService;
import com.berriesoft.springsecurity.auth.RegisterRequest;
import com.berriesoft.springsecurity.status.ErrorInfo;
import com.berriesoft.springsecurity.status.ErrorInfoList;
import com.berriesoft.springsecurity.status.SpringStatus;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//import static com.berriesoft.springsecurity.user.Role.ADMIN;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository repository;
    Logger logger = LoggerFactory.getLogger(getClass());

    //    public UserService(PasswordEncoder passwordEncoder, UserRepository repository, AuthenticationService authenticationService)
//    {
//        this.passwordEncoder = passwordEncoder;
//        this.repository = repository;
//        this.authenticationService = authenticationService;
//    }
    public void changePassword(ChangePasswordRequest request, Principal connectedUser) {

        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        // check if the current password is correct
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalStateException("Wrong password");
        }
        // check if the two new passwords are the same
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new IllegalStateException("Password are not the same");
        }

        // update the password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        // save the new password
        repository.save(user);
    }

    public SpringStatus deleteUserById(int id) {
        //repository.deleteById(id);

        Integer userID = id;
        SpringStatus requestStatus = getUserById(userID);
        if (requestStatus.getErrorCode() == SpringStatus.SUCCESSCODE) {
            repository.deleteById(id);
            logger.debug("deleted user id " + id);
            return new SpringStatus(SpringStatus.SUCCESSCODE, SpringStatus.SUCCESS, null);
        } else {
            List<ErrorInfo> errorInfo = new ArrayList<ErrorInfo>();
            errorInfo.add(new ErrorInfo(ErrorInfo.USER_NOT_FOUND_ERRORCODE, "id:" + userID.toString(), ErrorInfo.USER_NOT_FOUND_MESSAGE));
            ErrorInfoList errorInfoList = new ErrorInfoList(errorInfo);
            return new SpringStatus(SpringStatus.ERRORCODE, SpringStatus.ERROR,
                    errorInfoList);
        }


    }

    public SpringStatus getUserById(Integer userID) {

        Optional<User> user = repository.findById(userID);

        if (user.isPresent()) {
            logger.debug("found user " + user);
            User curUser = user.get();// user.stream().filter(u -> u.getId() == userID).findFirst().get();
            return new SpringStatus(SpringStatus.SUCCESSCODE, SpringStatus.SUCCESS,
                    curUser);
        } else {
            logger.debug("user not found");
            List<ErrorInfo> errorInfo = new ArrayList<ErrorInfo>();
            errorInfo.add(new ErrorInfo(ErrorInfo.USER_NOT_FOUND_ERRORCODE, "id:" + userID.toString(), ErrorInfo.USER_NOT_FOUND_MESSAGE));
            ErrorInfoList errorInfoList = new ErrorInfoList(errorInfo);
            return new SpringStatus(SpringStatus.ERRORCODE, SpringStatus.ERROR,
                    errorInfoList);
        }
    }

    public List<User> getAllUsers() {
        return repository.findAll();
    }

    public SpringStatus addUser(User user) {
        Optional<User> findUser = repository.findByEmail(user.getEmail());

        if (findUser.isPresent()) {
            logger.debug("Error: username (email) already exists" + user);
            List<ErrorInfo> errorInfo = new ArrayList<ErrorInfo>();
            errorInfo.add(new ErrorInfo(ErrorInfo.DUPLICATE_USERNAME_ERRORCODE, ErrorInfo.DUPLICATE_USERNAME_MESSAGE + ": {" + user.toString() + "}"));
            ErrorInfoList errorInfoList = new ErrorInfoList(errorInfo);
            return new SpringStatus(SpringStatus.ERRORCODE, SpringStatus.ERROR,
                    errorInfoList);

        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = repository.save(user);
        if (savedUser.equals(user)) {
            logger.debug("added user " + user);
            return new SpringStatus(SpringStatus.SUCCESSCODE, SpringStatus.SUCCESS,
                    savedUser);
        } else {
            logger.debug("Error: couldnt add user " + user);
            List<ErrorInfo> errorInfo = new ArrayList<ErrorInfo>();
            errorInfo.add(new ErrorInfo(ErrorInfo.USER_NOT_CREATED_ERRORCODE, ErrorInfo.USER_NOT_CREATED_MESSAGE + ": {" + user.toString() + "}"));
            ErrorInfoList errorInfoList = new ErrorInfoList(errorInfo);
            return new SpringStatus(SpringStatus.ERRORCODE, SpringStatus.ERROR,
                    errorInfoList);
        }
    }

    public SpringStatus updateUser(User user, boolean adminUser) {

        Optional<User> findUser = repository.findByEmail(user.getEmail());
        if (findUser.isPresent()) {
            User existingUser = findUser.get();
            user.setId(existingUser.getId());
            user.setPassword(existingUser.getPassword());
            if (!adminUser)
            {
                user.setRole(existingUser.getRole());
            }

            User savedUser = repository.save(user);
            logger.debug("updated user " + user);
            return new SpringStatus(SpringStatus.SUCCESSCODE, SpringStatus.SUCCESS,
                    savedUser);
        } else {
            List<ErrorInfo> errorInfo = new ArrayList<ErrorInfo>();
            errorInfo.add(new ErrorInfo(ErrorInfo.USER_NOT_FOUND_ERRORCODE, "email:" + user.getEmail(), ErrorInfo.USER_NOT_FOUND_MESSAGE));
            ErrorInfoList errorInfoList = new ErrorInfoList(errorInfo);
            return new SpringStatus(SpringStatus.ERRORCODE, SpringStatus.ERROR,
                    errorInfoList);
        }
    }
}