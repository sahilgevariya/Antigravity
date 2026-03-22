package com.medicalinventory.auth.service;

import com.medicalinventory.auth.dto.UserResponse;
import com.medicalinventory.common.dto.PagedResponse;

/**
 * User management service contract.
 */
public interface UserService {

    UserResponse getCurrentUser(String username);

    PagedResponse<UserResponse> getAllUsers(int page, int size);

    UserResponse updateUserRole(Long userId, String roleName);

    UserResponse deactivateUser(Long userId);
}
