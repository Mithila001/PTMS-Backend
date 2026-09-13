package com.tritonptms.ptms.user;

import com.tritonptms.ptms.common.exception.BadRequestException;
import com.tritonptms.ptms.common.exception.ConflictException;
import com.tritonptms.ptms.common.exception.ResourceNotFoundException;
import com.tritonptms.ptms.user.dto.CreateUserRequest;
import com.tritonptms.ptms.user.dto.CreateUserResponse;
import com.tritonptms.ptms.user.dto.UpdateUserRequest;
import com.tritonptms.ptms.user.dto.UserResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.text.Normalizer;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
@Transactional(readOnly = true)
@PreAuthorize("hasRole('ADMIN')")
public class UserService {

    private static final SecureRandom RANDOM = new SecureRandom();
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

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toResponse).toList();
    }

    public UserResponse getUserById(Long id) {
        return userMapper.toResponse(findUser(id));
    }

    @Transactional
    public CreateUserResponse createUser(CreateUserRequest request) {
        String email = request.email().trim().toLowerCase();
        String nic = request.nic().trim();
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("A user with this email already exists.");
        }
        if (userRepository.existsByNic(nic)) {
            throw new ConflictException("A user with this NIC already exists.");
        }

        User user = userMapper.fromCreateRequest(request);
        String username = generateUsername(request.firstName(), request.lastName());
        String temporaryPassword = generateTemporaryPassword(14);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(temporaryPassword));
        user.setRoles(resolveRoles(request.roles()));
        userRepository.save(user);

        return new CreateUserResponse(username, temporaryPassword);
    }

    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = findUser(id);
        String email = request.email().trim().toLowerCase();
        String nic = request.nic().trim();

        userRepository.findByEmail(email)
                .filter(other -> !other.getId().equals(id))
                .ifPresent(other -> { throw new ConflictException("A user with this email already exists."); });
        userRepository.findByNic(nic)
                .filter(other -> !other.getId().equals(id))
                .ifPresent(other -> { throw new ConflictException("A user with this NIC already exists."); });

        userMapper.applyUpdate(user, request);
        user.setRoles(resolveRoles(request.roles()));
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

    private String generateTemporaryPassword(int length) {
        String upper = "ABCDEFGHJKLMNPQRSTUVWXYZ";
        String lower = "abcdefghijkmnopqrstuvwxyz";
        String digits = "23456789";
        String symbols = "!@#$%";
        String all = upper + lower + digits + symbols;

        char[] password = new char[length];
        password[0] = upper.charAt(RANDOM.nextInt(upper.length()));
        password[1] = lower.charAt(RANDOM.nextInt(lower.length()));
        password[2] = digits.charAt(RANDOM.nextInt(digits.length()));
        password[3] = symbols.charAt(RANDOM.nextInt(symbols.length()));
        for (int i = 4; i < length; i++) {
            password[i] = all.charAt(RANDOM.nextInt(all.length()));
        }
        for (int i = password.length - 1; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);
            char tmp = password[i];
            password[i] = password[j];
            password[j] = tmp;
        }
        return new String(password);
    }
}
