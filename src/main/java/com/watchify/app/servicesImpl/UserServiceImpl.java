package com.watchify.app.servicesImpl;

import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.watchify.app.dao.UserRepository;
import com.watchify.app.enums.Role;
import com.watchify.app.exceptions.EmailAlreadyExistsException;
import com.watchify.app.exceptions.InvalidRoleException;
import com.watchify.app.models.User;
import com.watchify.app.requests.UserRequest;
import com.watchify.app.responses.MessageResponse;
import com.watchify.app.responses.PageResponse;
import com.watchify.app.responses.UserResponse;
import com.watchify.app.services.EmailService;
import com.watchify.app.services.UserService;
import com.watchify.app.utils.PaginationUtils;
import com.watchify.app.utils.ServiceUtils;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ServiceUtils serviceUtils;

    @Autowired
    private EmailService emailService;

    @Override
    public MessageResponse createUser(UserRequest userRequest) {
        if (userRepository.findByEmail(userRequest.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email Already exists");
        }
        validateRole(userRequest.getRole());
        User user = new User();
        user.setEmail(userRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setFullName(userRequest.getFullName());
        user.setRole(com.watchify.app.enums.Role.valueOf(userRequest.getRole().toUpperCase()));
        user.setActive(true);
        String verificationToken = UUID.randomUUID().toString();
        user.setVerificationToken(verificationToken);
        user.setVerificationTokenExpiry(Instant.now().plusSeconds(86400));
        userRepository.save(user);
        emailService.sendVerificationEmail(userRequest.getEmail(), verificationToken);
        return new MessageResponse("User created successfully");
    }

    private void validateRole(String role) {

        if (Arrays.stream(Role.values()).noneMatch(r -> r.name().equalsIgnoreCase(role))) {
            throw new InvalidRoleException("Invalid role exception " + role);
        }
    }

    @Override
    public MessageResponse updateUser(Long id, UserRequest userRequest) {

        User user = serviceUtils.getUserByIdOrThrow(id);
        ensureNotLastActiveAdmin(user);
        validateRole(userRequest.getRole());
        user.setFullName(userRequest.getFullName());
        user.setRole(Role.valueOf(userRequest.getRole().toUpperCase()));
        userRepository.save(user);
        return new MessageResponse("User updated successfully");
    }

    private void ensureNotLastActiveAdmin(User user) {
        if (user.isActive() && user.getRole() == Role.ADMIN) {
            long activeAdminCount = userRepository.countByRoleAndActive(Role.ADMIN, true);
            if (activeAdminCount <= 1) {
                throw new RuntimeException("Cannot deactivate the last active admin user");
            }

        }
    }

    @Override
    public PageResponse<UserResponse> getUsers(int page, int size, String search) {

        Pageable pageable = PaginationUtils.createPageRequest(page, size, "id");
        Page<User> userPage;
        if (search != null && !search.trim().isEmpty()) {
            userPage = userRepository.searchUsers(search.trim(), pageable);
        } else {
            userPage = userRepository.findAll(pageable);
        }
        return PaginationUtils.toPageResponse(userPage, UserResponse::fromEntity);
    }

    @Override
    public MessageResponse deleteUser(Long id, String currentUserEmail) {

        User user = serviceUtils.getUserByIdOrThrow(id);

        if (user.getEmail().equals(currentUserEmail)) {
            throw new RuntimeException("You cannot delete your own account");

        }
        ensureNotLastAdmin(user, "delete");
        userRepository.deleteById(id);
        return new MessageResponse("User deleted successfully");
    }

    private void ensureNotLastAdmin(User user, String operation) {
        if (user.getRole() == Role.ADMIN) {
            long adminCount = userRepository.countByRole(Role.ADMIN);
            if (adminCount <= 1) {
                throw new RuntimeException("Cannot " + operation + "the last admin user");
            }
        }
    }

    @Override
    public MessageResponse toggleUserStatus(Long id, String currentUserEmail) {
        User user = serviceUtils.getUserByIdOrThrow(id);

        if (user.getEmail().equals(currentUserEmail)) {
            throw new RuntimeException("You cannot deactivate your own account");
        }
        ensureNotLastActiveAdmin(user);

        user.setActive(!user.isActive());
        userRepository.save(user);

        return new MessageResponse("User status updated successfully");
    }

    @Override
    public MessageResponse changeUserRole(Long id, UserRequest userRequest) {
        User user = serviceUtils.getUserByIdOrThrow(id);
        validateRole(userRequest.getRole());

        Role newRole = Role.valueOf(userRequest.getRole().toUpperCase());
        if (user.getRole() == Role.ADMIN && newRole == Role.USER) {
            ensureNotLastAdmin(user, "change the role of");
        }

        user.setRole(newRole);
        userRepository.save(user);
        return new MessageResponse("User role updated successfully");
    }

}
