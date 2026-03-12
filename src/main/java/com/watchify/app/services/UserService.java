package com.watchify.app.services;

import com.watchify.app.requests.UserRequest;
import com.watchify.app.responses.MessageResponse;
import com.watchify.app.responses.PageResponse;
import com.watchify.app.responses.UserResponse;

public interface UserService {

    MessageResponse createUser(UserRequest userRequest);

    MessageResponse updateUser(Long id, UserRequest userRequest);

    PageResponse<UserResponse> getUsers(int page, int size, String search);

    MessageResponse deleteUser(Long id, String currentUserEmail);

    MessageResponse toggleUserStatus(Long id, String currentUserEmail);

    MessageResponse changeUserRole(Long id, UserRequest userRequest);

}
