package com.example.demo.service;

import com.example.demo.dao.GenericDao;
import com.example.demo.dto.ProfileDto;
import com.example.demo.dto.RoleDto;
import com.example.demo.dto.UserDto;
import com.example.demo.model.SpProfile;
import com.example.demo.model.SpRole;
import com.example.demo.model.SpUser;
import com.example.demo.repository.ProfileRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.model.LoginRequest;
import com.example.demo.model.LoginResponse;
import com.example.demo.security.JWTUtils;
import com.example.demo.security.MyUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;

import net.bytebuddy.utility.RandomString;


@Service
public class UserService {
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtils jwTutils;
    private final AuthenticationManager authenticationManager;
    private MyUserDetails myUserDetails;
    private final JavaMailSender mailSender;

    @Autowired
    public UserService(UserRepository userRepository,
                       ProfileRepository profileRepository,
                       RoleRepository roleRepository,
                       @Lazy PasswordEncoder passwordEncoder,
                       JWTUtils jwTutils,
                       @Lazy AuthenticationManager authenticationManager,
                       @Lazy MyUserDetails myUserDetails,
                       JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwTutils = jwTutils;
        this.authenticationManager = authenticationManager;
        this.myUserDetails = myUserDetails;
        this.mailSender = mailSender;
    }

    public UserDto getById(Long uid, Boolean details) {
        return userRepository.findById(uid).map(user -> new UserDto(user, details)).orElse(null);
    }

    public UserDto getUserByEmail(String email, Boolean details) {
        return userRepository.findByEmail(email).map(user -> new UserDto(user, details)).orElse(null);
    }

    public List<UserDto> getByStatus(Boolean status, Boolean details) {
        return userRepository.findByStatus(status).stream().map(user -> new UserDto(user, details)).toList();
    }

    public List<UserDto> getAll(Boolean details) {
        return userRepository.findAll().stream().map(user -> new UserDto(user, details)).toList();
    }

    public GenericDao<UserDto> createUser(UserDto dto) throws MessagingException, UnsupportedEncodingException {
        GenericDao<UserDto> returnDao = new GenericDao<>();
        List<String> errors = new ArrayList<>();

        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            errors.add("Email cannot be empty");
        }

        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            errors.add("Password cannot be empty");
        }

        Optional<SpRole> role = Optional.empty();
        if (dto.getRole().getId() == null) {
            errors.add("Role cannot be empty");
        } else {
            role = roleRepository.findById(dto.getRole().getId());

            if (role.isEmpty()) {
                errors.add("Role does not exist");
            }
        }

        if (errors.isEmpty()) {
            Optional<SpUser> retrievedUser = userRepository.findByEmail(dto.getEmail());

            if (retrievedUser.isEmpty()) {
                dto.setStatus(true);
                SpProfile profile = new SpProfile();
                profile.setFirstName("");
                profile.setLastName("");
                profile.setProfilePic("");
                profile = profileRepository.save(profile);  // Save profile first to avoid detached entity issue
                dto.setPassword(passwordEncoder.encode(dto.getPassword()));
                dto.setProfile(new ProfileDto(profile, false));
                dto.setRole(new RoleDto(role.get(), false));
                String randomCode = RandomString.make(64);
                dto.setVerificationCode(randomCode);
                dto.setEnabled(false);
                SpUser user = new SpUser(dto);
                user.setProfile(profile);  // Attach the managed profile entity
                user.setRole(role.get());  // Attach the managed role entity
                SpUser savedUser = userRepository.save(user);
                returnDao.setObject(new UserDto(savedUser, false));
                sendVerificationEmail(savedUser);
            } else {
                errors.add("User already exists");
            }
        }
        if (!errors.isEmpty()) {
            returnDao.setErrors(errors);
        }
        return returnDao;
    }


    public GenericDao<UserDto> editUser(UserDto dto) {
        GenericDao<UserDto> returnDao = new GenericDao<>();

        List<String> errors = new ArrayList<>();

        if (dto.getId() == null) {
            errors.add("User ID cannot be empty");
        }

        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            errors.add("Email cannot be empty");
        }

        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            errors.add("Password cannot be empty");
        }

        if (errors.isEmpty()) {
            Optional<SpUser> retrievedUser = userRepository.findById(dto.getId());

            if (retrievedUser.isPresent()) {
                retrievedUser.get().setEmail(dto.getEmail());
                retrievedUser.get().setPassword(dto.getPassword());

                SpUser savedUser = userRepository.save(retrievedUser.get());

                returnDao.setObject(new UserDto(savedUser, false));
            } else {
                errors.add("User does not exist");
            }
        }

        if (!errors.isEmpty()) {
            returnDao.setErrors(errors);
        }

        return returnDao;
    }

    public GenericDao<Boolean> deleteUser(Long uid) {
        Optional<SpUser> retrievedUser = userRepository.findById(uid);

        if (retrievedUser.isPresent()) {
            retrievedUser.get().setStatus(false);
            userRepository.save(retrievedUser.get());
            return new GenericDao<>(true, null);
        } else {
            return new GenericDao<>(false, List.of("User does not exist"));
        }
    }

    public Optional<SpUser> findUserByEmailAddress(String email) {
        return userRepository.findUserByEmail(email);
    }

    public ResponseEntity<?> loginUser(LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken
                (loginRequest.getEmail(), loginRequest.getPassword());
        try {
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            myUserDetails = (MyUserDetails) authentication.getPrincipal();

            // Retrieve the user from the database
            Optional<SpUser> user = userRepository.findUserByEmail(loginRequest.getEmail());

            // Check if the user is enabled and the account is not deactivated
            if (user.isPresent()) {
                SpUser spUser = user.get();
                if (!spUser.isEnabled()) {
                    return ResponseEntity.status(403).body(new LoginResponse("Account is not verified. Please check your email for the verification link."));
                } else if (!spUser.getStatus()) {
                    return ResponseEntity.status(403).body(new LoginResponse("Account is deactivated. Please contact support."));
                } else {
                    final String jwt = jwTutils.generateJwtToken(myUserDetails);
                    return ResponseEntity.ok(new LoginResponse(jwt));
                }
            } else {
                // User is not enabled
                return ResponseEntity.status(403).body(new LoginResponse("Account is not verified. Please check your email for the verification link."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(401).body(new LoginResponse("Error! Username or Password is incorrect"));
        }
    }

    public static SpUser getCurrentLoggedInUser() {
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUser();
    }

    private void sendVerificationEmail(SpUser user)
            throws MessagingException, UnsupportedEncodingException {
        String toAddress = user.getEmail();
        String fromAddress = "spendingapp1@gmail.com";
        String senderName = "Spending App";
        String subject = "Please verify your registration";
        String content = "Dear [[name]],<br>"
                + "Please use the following code to verify your registration:<br>"
                + "<h3>[[CODE]]</h3>"
                + "Thank you,<br>"
                + "Spending App";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        content = content.replace("[[name]]", user.getEmail());
        content = content.replace("[[CODE]]", user.getVerificationCode());

        helper.setText(content, true);

        mailSender.send(message);
    }

    public boolean verify(String verificationCode) {
        Optional<SpUser> user = userRepository.findByVerificationCode(verificationCode);

        if (user.isEmpty() || user.get().isEnabled()) {
            return false;
        } else {
            user.get().setVerificationCode(null);
            user.get().setEnabled(true);
            userRepository.save(user.get());

            return true;
        }

    }

    public void sendPasswordResetEmail(SpUser user, String resetToken) throws MessagingException, UnsupportedEncodingException {
        String toAddress = user.getEmail();
        String fromAddress = "spendingapp1@gmail.com";
        String senderName = "Spending App";
        String subject = "Password Reset Request";
        String content = "Dear [[name]],<br>"
                + "We received a request to reset your password. Use the following code to reset your password:<br>"
                + "<h3>Reset Code: [[CODE]]</h3>"
                + "If you did not request this, please ignore this email.<br>"
                + "Thank you,<br>"
                + "Spending App";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        content = content.replace("[[name]]", user.getEmail());
        content = content.replace("[[CODE]]", resetToken);

        helper.setText(content, true);

        mailSender.send(message);
    }


    public String generatePasswordResetToken() {
        return RandomString.make(64);
    }

    public boolean resetPassword(String token, String newPassword) {
        Optional<SpUser> user = userRepository.findByResetToken(token);
        if (user.isEmpty() || user.get().getTokenExpirationTime().isBefore(LocalDateTime.now())) {
            return false;
        }
        user.get().setPassword(passwordEncoder.encode(newPassword));
        user.get().setResetToken(null);
        user.get().setTokenExpirationTime(null);
        userRepository.save(user.get());
        return true;
    }

    public boolean requestPasswordReset(String email) {
        Optional<SpUser> user = findUserByEmailAddress(email);
        if (user.isEmpty()) {
            return false;
        }

        String resetToken = generatePasswordResetToken();
        user.get().setResetToken(resetToken);
        user.get().setTokenExpirationTime(LocalDateTime.now().plusHours(1)); // Token valid for 1 hour
        userRepository.save(user.get());

        try {
            sendPasswordResetEmail(user.get(), resetToken);
        } catch (MessagingException | UnsupportedEncodingException e) {
            return false;
        }

        return true;
    }


    public boolean changePassword(String oldPassword, String newPassword) {
        SpUser currentUser = getCurrentLoggedInUser();
        if (!passwordEncoder.matches(oldPassword, currentUser.getPassword())) {
            return false;
        }

        currentUser.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(currentUser);
        return true;
    }
}
