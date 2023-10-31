package com.berriesoft.springsecurity.user;

import com.berriesoft.springsecurity.auth.AuthorisationService;
import com.berriesoft.springsecurity.product.Product;
import com.berriesoft.springsecurity.status.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.lang.IllegalAccessException;
import java.net.URI;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;
    private final AuthorisationService authorisationService;
    private final String privilegeRole = "ROLE_ADMIN";
    Logger logger = LoggerFactory.getLogger(getClass());

    @PatchMapping
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequest request,
            Principal connectedUser
    ) {
        service.changePassword(request, connectedUser);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @ResponseBody
    public List<User> getAllUsers() {
        logger.debug("in getAllUser");
        return service.getAllUsers();
    }

    @GetMapping(path = "/{id}")
    @ResponseBody
    public ResponseEntity<Object> getUserById(@PathVariable int id) {
        logger.debug("in getUserById for id: " + id);
        SpringStatus status = service.getUserById(id);

        if (status.getErrorCode() != SpringStatus.SUCCESSCODE) {
            throw new UserNotFoundException((ErrorInfoList) status.getPayload());
        } else {
            return ResponseEntity.status(SpringStatus.SUCCESSCODE).body((User) status.getPayload());
        }
    }

    @PostMapping
    public ResponseEntity<Object> addUser(@Valid @RequestBody User user, HttpServletRequest request) throws IllegalAccessException {
        logger.debug("in addUser");
        //User curUser = authorisationService.getUser();

        if (user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            logger.debug("New user is an admin");
            authorisationService.authorisePrivilege(request, privilegeRole);
        }

        SpringStatus status = service.addUser(user);
        if (status.getErrorCode() != SpringStatus.SUCCESSCODE) {
            throw new UserNotCreatedException((ErrorInfoList) status.getPayload());
        } else {
            User savedUser = (User)status.getPayload();
            URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedUser.getId())
                    .toUri();
            return ResponseEntity.created(location).build();
        }
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<Object> updateUser(@RequestBody @Valid User user, @PathVariable int id, HttpServletRequest request) throws IllegalAccessException {
        logger.debug("in UpdateUser");
        boolean adminUser = false;

        User curUser = authorisationService.getUser();
        adminUser = authorisationService.checkPrivilege(request, privilegeRole);

        if ((curUser.getId() != id) && !adminUser) {
            List<ErrorInfo> errorInfo = new ArrayList<ErrorInfo>();
            errorInfo.add(new ErrorInfo(ErrorInfo.ILLEGAL_ACCESS_CODE, ErrorInfo.ILLEGAL_ACCESS_MESSAGE + " Target user id[" + id + "] not equal to authenticated user[" + curUser.getId() + "] : " + curUser.getRole().getAuthorities()));
            throw new IllegalAccessException(errorInfo.toString());
        }

        SpringStatus status = service.updateUser(user, adminUser);

        if (status.getErrorCode() != SpringStatus.SUCCESSCODE) {
            throw new UserNotFoundException((ErrorInfoList) status.getPayload());
        } else {
            URI location = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
            return ResponseEntity.created(location).build();
        }
    }

    @DeleteMapping(path = "/{id}")
    @PreAuthorize("hasAuthority('admin:delete')")
    public ResponseEntity<Object> deleteUserById(@PathVariable int id) {
        logger.debug("in deleteUserById for id: " + id);

        SpringStatus status = service.deleteUserById(id);

        if (status.getErrorCode() != SpringStatus.SUCCESSCODE) {
            throw new UserNotFoundException((ErrorInfoList) status.getPayload());
        } else {
            return ResponseEntity.status(SpringStatus.SUCCESSCODE).build();
        }
    }


}