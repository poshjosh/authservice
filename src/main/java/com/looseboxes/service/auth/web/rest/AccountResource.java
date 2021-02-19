package com.looseboxes.service.auth.web.rest;

import com.looseboxes.service.auth.domain.User;
import com.looseboxes.service.auth.service.AccountService;
import com.looseboxes.service.auth.ext.web.Endpoints;
import com.looseboxes.service.auth.ext.web.rest.vm.Message;
import com.looseboxes.service.auth.service.dto.PasswordChangeDTO;
import com.looseboxes.service.auth.service.dto.UserDTO;
import com.looseboxes.service.auth.web.rest.errors.*;
import com.looseboxes.service.auth.web.rest.vm.KeyAndPasswordVM;
import com.looseboxes.service.auth.web.rest.vm.ManagedUserVM;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping(Endpoints.API)
public class AccountResource{

    private final Logger log = LoggerFactory.getLogger(AccountResource.class);

    private final AccountService accountService;
    
    public AccountResource(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * {@code POST  /register} : register and activate the user.
     * 
     * This method obviates the need for a verification step (e.g via email) in 
     * the activation process.
     *
     * @param managedUserVM the managed user View Model.
     * @return {"message": [MESSAGE_FOR_THE_NEW_USER]}
     * @throws InvalidPasswordLengthException {@code 400 (Bad Request)} if the password is incorrect.
     * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is already used.
     * @throws LoginAlreadyUsedException {@code 400 (Bad Request)} if the login is already used.
     */
    @PostMapping("/register/activate")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO registerAccountAndActivate(@Valid @RequestBody ManagedUserVM managedUserVM) {
        User user = accountService.registerAccountAndActivate(managedUserVM, managedUserVM.getPassword());
        return managedUserVM;
    }
    
    /**
     * {@code POST  /register} : register the user.
     *
     * @param managedUserVM the managed user View Model.
     * @return {"message": [MESSAGE_FOR_THE_NEW_USER]}
     * @throws InvalidPasswordLengthException {@code 400 (Bad Request)} if the password is incorrect.
     * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is already used.
     * @throws LoginAlreadyUsedException {@code 400 (Bad Request)} if the login is already used.
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO registerAccount(@Valid @RequestBody ManagedUserVM managedUserVM) {
        this.validatePasswordLength(managedUserVM.getPassword());
        User user = accountService.registerAccount(managedUserVM, managedUserVM.getPassword());
        return managedUserVM;
    }

    /**
     * {@code GET  /activate} : activate the registered user.
     *
     * @param key the activation key.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user couldn't be activated.
     */
    @GetMapping("/activate")
    public void activateAccount(@RequestParam(value = "key") String key) {
        accountService.activateAccount(key);
    }

    /**
     * {@code GET  /authenticate} : check if the user is authenticated, and return its login.
     *
     * @param request the HTTP request.
     * @return the login if the user is authenticated.
     */
    @GetMapping("/authenticate")
    public String isAuthenticated(HttpServletRequest request) {
        log.debug("REST request to check if the current user is authenticated");
        return request.getRemoteUser();
    }

    /**
     * {@code GET  /account} : get the current user.
     *
     * @return the current user.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user couldn't be returned.
     */
    @GetMapping("/account")
    public UserDTO getAccount() {
        return accountService.getAccount();
    }

    /**
     * {@code POST  /account} : update the current user information.
     *
     * @param userDTO the current user information.
     * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is already used.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user login wasn't found.
     */
    @PostMapping("/account")
    public void saveAccount(@Valid @RequestBody UserDTO userDTO) {
        accountService.saveAccount(userDTO);
    }

    /**
     * {@code POST  /account/change-password} : changes the current user's password.
     *
     * @param passwordChangeDto current and new password.
     * @throws InvalidPasswordLengthException {@code 400 (Bad Request)} if the new password is incorrect.
     */
    @PostMapping(path = "/account/change-password")
    public void changePassword(@RequestBody PasswordChangeDTO passwordChangeDto) {
        this.validatePasswordLength(passwordChangeDto.getNewPassword());
        accountService.changePassword(passwordChangeDto);
    }

    /**
     * {@code POST   /account/reset-password/init} : Send an email to reset the password of the user.
     *
     * @param mail the mail of the user.
     * @return {"message": [MESSAGE_FOR_THE_USER]}
     */
    @PostMapping(path = "/account/reset-password/init")
    public Message requestPasswordReset(@RequestBody String mail) {
        log.debug("REST request to begin reset password for: {}", mail);
        return accountService.requestPasswordReset(mail);
    }

    /**
     * {@code POST   /account/reset-password/finish} : Finish to reset the password of the user.
     *
     * @param keyAndPassword the generated key and the new password.
     * @return {"message": [MESSAGE_FOR_THE_USER]}
     * @throws InvalidPasswordLengthException {@code 400 (Bad Request)} if the password is incorrect.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the password could not be reset.
     */
    @PostMapping(path = "/account/reset-password/finish")
    public Message finishPasswordReset(@RequestBody KeyAndPasswordVM keyAndPassword) {
        log.debug("REST request to finish reset password");
        return accountService.finishPasswordReset(keyAndPassword.getNewPassword(), keyAndPassword.getKey());
    }

    private void validatePasswordLength(String password) {
        if (!accountService.checkPasswordLength(password)) {
            throw new InvalidPasswordLengthException();
        }
    }    
}
