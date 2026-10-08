package com.tritonptms.ptms.feature.user;

import com.tritonptms.ptms.common.exception.BadRequestException;
import com.tritonptms.ptms.common.exception.ConflictException;
import com.tritonptms.ptms.common.exception.ResourceNotFoundException;
import com.tritonptms.ptms.feature.user.dto.CreateUserRequest;
import com.tritonptms.ptms.feature.user.dto.CreateUserResponse;
import com.tritonptms.ptms.feature.user.dto.UpdateUserRequest;
import com.tritonptms.ptms.feature.user.dto.UserResponse;
import com.tritonptms.ptms.feature.user.dto.ResetPasswordResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
@Transactional(readOnly = true)
@PreAuthorize("hasRole('ADMIN')")
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            UserMapper userMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toResponse);
    }

    public UserResponse getUserById(Long id) {
        return userMapper.toResponse(findUser(id));
    }

    @Transactional
    public ResetPasswordResponse resetPassword(Long id) {
        User user = userRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        String initialPassword = UserPasswordGenerator.generate(14);
        user.setPassword(passwordEncoder.encode(initialPassword));
        user.setAuthVersion(user.getAuthVersion() + 1);
        return new ResetPasswordResponse(user.getUsername(), initialPassword);
    }

    @Transactional
    public CreateUserResponse createUser(CreateUserRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        String nic = request.nic().trim();
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("A user with this email already exists.");
        }
        if (userRepository.existsByNic(nic)) {
            throw new ConflictException("A user with this NIC already exists.");
        }

        User user = userMapper.fromCreateRequest(request);
        String username = generateUsername(request.firstName(), request.lastName());
        String initialPassword = UserPasswordGenerator.generate(14);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(initialPassword));
        user.setRoles(resolveRoles(request.roles()));
        userRepository.save(user);

        return new CreateUserResponse(username, initialPassword);
    }

    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        String nic = request.nic().trim();

        userRepository.findByEmail(email)
                .filter(other -> !other.getId().equals(id))
                .ifPresent(other -> { throw new ConflictException("A user with this email already exists."); });
        userRepository.findByNic(nic)
                .filter(other -> !other.getId().equals(id))
                .ifPresent(other -> { throw new ConflictException("A user with this NIC already exists."); });

        Set<Role> roles = resolveRoles(request.roles());
        boolean rolesChanged = !user.getRoles().stream().map(Role::getName).collect(java.util.stream.Collectors.toSet())
                .equals(roles.stream().map(Role::getName).collect(java.util.stream.Collectors.toSet()));
        boolean removingOwnAdmin = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal() instanceof com.tritonptms.ptms.infrastructure.security.AuthenticatedUser principal
                && principal.id().equals(id)
                && user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN"))
                && roles.stream().noneMatch(role -> role.getName().equals("ROLE_ADMIN"));
        if (removingOwnAdmin) {
            throw new BadRequestException("You cannot remove your own administrator role.");
        }
        userMapper.applyUpdate(user, request);
        user.setRoles(roles);
        if (rolesChanged) {
            user.setAuthVersion(user.getAuthVersion() + 1);
        }
        return userMapper.toResponse(userRepository.save(user));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') and #id != authentication.principal.id()")
    public void deleteUser(Long id) {
        userRepository.delete(findUser(id));
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    private Set<Role> resolveRoles(List<String> requestedRoles) {
        Set<Role> roles = new HashSet<>();
        for (String roleName : requestedRoles) {
            String normalized = roleName.trim().toUpperCase(Locale.ROOT);
            if (!normalized.startsWith("ROLE_")) {
                normalized = "ROLE_" + normalized;
            }
            final String role = normalized;
            roles.add(roleRepository.findByName(role)
                    .orElseThrow(() -> new BadRequestException("Unknown role: " + role)));
        }
        return roles;
    }

    private String generateUsername(String firstName, String lastName) {
        String base = "ptms." + slug(firstName) + "." + slug(lastName);
        base = base.substring(0, Math.min(base.length(), 45));
        String candidate = base;
        int suffix = 2;
        while (userRepository.existsByUsername(candidate)) {
            String suffixText = String.valueOf(suffix++);
            int baseLength = Math.min(base.length(), 50 - suffixText.length());
            candidate = base.substring(0, baseLength) + suffixText;
        }
        return candidate;
    }

    private String slug(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]", "");
        return normalized.isBlank() ? "user" : normalized;
    }

}
