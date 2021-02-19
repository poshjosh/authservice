package com.looseboxes.service.auth.service;

import com.bc.service.util.SecurityUtil;
import com.looseboxes.service.auth.domain.User;
import com.looseboxes.service.auth.ext.web.UserMessages;
import com.looseboxes.service.auth.ext.web.rest.vm.Message;
import com.looseboxes.service.auth.ext.web.rest.vm.MessageVM;
import com.looseboxes.service.auth.repository.UserRepository;
import com.looseboxes.service.auth.service.dto.PasswordChangeDTO;
import com.looseboxes.service.auth.service.dto.UserDTO;
//import com.looseboxes.service.auth.web.rest.errors.EmailAlreadyUsedException;
//import com.looseboxes.service.auth.web.rest.errors.LoginAlreadyUsedException;
import com.looseboxes.service.auth.web.rest.vm.ManagedUserVM;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author hp
 */
@Service
@Transactional
public class AccountService{

    public static class AccountException extends RuntimeException {
        private AccountException(String message) {
            super(message);
        }
    }

    private final Logger log = LoggerFactory.getLogger(AccountService.class);

    private final UserRepository userRepository;

    private final UserService userService;
    
    private final UserMessages userMessages;
    
    public AccountService(
            UserRepository userRepository, 
            UserService userService, 
            UserMessages userMessages) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.userMessages = userMessages;
    }

    public User registerAccountAndActivate(UserDTO userDTO, String password) {
        final User user = this.registerAccount(userDTO, password);
        String key = Objects.requireNonNull(user.getActivationKey());
        return this.activateAccount(key);
    }

    /**
     * Register the user.
     *
     * @param userDTO the managed user View Model.
     * @param password
     * @return {"message": [MESSAGE_FOR_THE_NEW_USER]}
     * @throws AccountException {@code 400 (Bad Request)} if the password is incorrect.
     * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is already used.
     * @throws LoginAlreadyUsedException {@code 400 (Bad Request)} if the login is already used.
     */
    public User registerAccount(UserDTO userDTO, String password) {
        this.validatePasswordLength(password);
        return userService.registerUser(userDTO, password);
    }
    
    /**
     * Activate the registered user.
     *
     * @param key the activation key.
     * @return The activated user
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user couldn't be activated.
     */
    public User activateAccount(String key) throws AccountException{
        User user = userService.activateRegistration(key)
                .orElseThrow(() -> new AccountException("No user was found for this activation key"));
        return user;
    }

    /**
     * Get the current user.
     *
     * @return the current user.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user couldn't be returned.
     */
    public UserDTO getAccount() {
        return userService.getUserWithAuthorities()
            .map(UserDTO::new)
            .orElseThrow(() -> new AccountException("User could not be found"));
    }

    /**
     * Update the current user information.
     *
     * @param userDTO the current user information.
     * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is already used.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user login wasn't found.
     */
    @Transactional
    public void saveAccount(UserDTO userDTO) {
        String userLogin = SecurityUtil.getCurrentUserLogin().orElseThrow(() -> new AccountException("Current user login not found"));
        Optional<User> existingUser = userRepository.findOneByEmailIgnoreCase(userDTO.getEmail());
        if (existingUser.isPresent() && (!existingUser.get().getLogin().equalsIgnoreCase(userLogin))) {
            throw new EmailAlreadyUsedException();
        }
        Optional<User> user = userRepository.findOneByLogin(userLogin);
        if (!user.isPresent()) {
            throw new AccountException("User could not be found");
        }
        
        userService.updateUser(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail(),
            userDTO.getLangKey(), userDTO.getImageUrl());
    }

    /**
     * Changes the current user's password.
     *
     * @param passwordChangeDto current and new password.
     * @throws AccountException {@code 400 (Bad Request)} if the new password is incorrect.
     */
    public void changePassword(PasswordChangeDTO passwordChangeDto) {
        this.checkPasswordLength(passwordChangeDto.getNewPassword());
        userService.changePassword(passwordChangeDto.getCurrentPassword(), passwordChangeDto.getNewPassword());
    }
    
    /**
     * This is a temp fix to remove quotes if present
     * @param mail
     * @return 
     */
    private String applyTempBugFix(String mail) {
        if(mail.startsWith("\"") && mail.endsWith("\"")) {
            log.debug("Found quotes around email address. Applying temp bug fix");
            mail = mail.substring(1, mail.length() - 1);
        }
        return mail;
    }

    /**
     * Send an email to reset the password of the user.
     *
     * @param mail the mail of the user.
     * @return {"message": [MESSAGE_FOR_THE_USER]}
     */
    public Message requestPasswordReset(String mail) {
        log.debug("Begin reset password for: {}", mail);
        
        //////////////////////////// @TODO ///////////////////////////
        // This is a temp fix to remove quotes if present
        mail = this.applyTempBugFix(mail);
        //////////////////////////////////////////////////////////////
        
        Optional<User> user = userService.requestPasswordReset(mail);
        if (!user.isPresent()) {
            // Pretend the request has been successful to prevent checking which emails really exist
            // but log that an invalid attempt has been made
            log.warn("Password reset requested for non existing mail '{}'", mail);
        }
        
        return new MessageVM().message("A password rest was successfully requested");
    }

    /**
     * Finish to reset the password of the user.
     *
     * @param newPassword - The new password
     * @param key - The key that confirms this is a valid request.
     * @return {"message": [MESSAGE_FOR_THE_USER]}
     * @throws AccountException {@code 400 (Bad Request)} if the password is incorrect.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the password could not be reset.
     */
    public Message finishPasswordReset(String newPassword, String key) {
        log.debug("Finish reset password");
        this.validatePasswordLength(newPassword);
        
        Optional<User> user = userService.completePasswordReset(newPassword, key);

        if (!user.isPresent()) {
            throw new AccountException("No user was found for this reset key");
        }
        
        return userMessages.getPasswordResetFinishMessage(user.get());
    }
    
    private void validatePasswordLength(String password) {
        if (!checkPasswordLength(password)) {
            throw new AccountException("Invalid password");
        }
    }    
    
    public boolean checkPasswordLength(String password) {
        return !StringUtils.isEmpty(password) &&
            password.length() >= ManagedUserVM.PASSWORD_MIN_LENGTH &&
            password.length() <= ManagedUserVM.PASSWORD_MAX_LENGTH;
    }
}
