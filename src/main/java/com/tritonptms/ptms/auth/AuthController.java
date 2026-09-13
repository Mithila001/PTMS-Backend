
package com.tritonptms.ptms.auth;

import com.tritonptms.ptms.user.Role;
import com.tritonptms.ptms.user.User;
import com.tritonptms.ptms.user.ERole;
import com.tritonptms.ptms.user.RoleRepository;
import com.tritonptms.ptms.user.UserRepository;
import com.tritonptms.ptms.user.UserService;
import com.tritonptms.ptms.auth.dto.LoginRequest;
import com.tritonptms.ptms.auth.dto.UserInfoResponse;
import com.tritonptms.ptms.user.dto.RegisterRequestDto;
import com.tritonptms.ptms.user.dto.RegisterResponseDto;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

        private final AuthenticationManager authenticationManager;
        private final UserRepository userRepository;
        private final RoleRepository roleRepository;
        private final PasswordEncoder encoder;
        private final UserService userService;

        public AuthController(AuthenticationManager authenticationManager,
                        UserRepository userRepository,
                        RoleRepository roleRepository,
                        PasswordEncoder encoder, UserService userService) {
                this.authenticationManager = authenticationManager;
                this.userRepository = userRepository;
                this.roleRepository = roleRepository;
                this.encoder = encoder;
                this.userService = userService;
        }

        // @PostMapping("/login")
        // public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest,
        // HttpServletRequest request) {
        // Authentication authentication = authenticationManager.authenticate(
        // new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
        // loginRequest.getPassword()));
        // SecurityContextHolder.getContext().setAuthentication(authentication);
        // // Ensure session is created and SecurityContext is stored so JSESSIONID is
        // set
        // HttpSession session = request.getSession(true);
        // session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
        // SecurityContextHolder.getContext());

        // User user = (User) authentication.getPrincipal();
        // List<String> roles = user.getRoles().stream()
        // .map(role -> role.getName())
        // .collect(Collectors.toList());

        // return ResponseEntity.ok(new UserInfoResponse(user.getId(),
        // user.getUsername(), roles));
        // }

        /**
         * Retrieves the current authenticated user's information.
         * 
         * @return ResponseEntity with the user's information or 401 Unauthorized.
         */
        @GetMapping("/me") // NEW ENDPOINT
        public ResponseEntity<?> getCurrentUser() {
                // In AuthController.java, within the /me method
                // Get the principal from the SecurityContext
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if (authentication == null || !authentication.isAuthenticated()
                                || authentication.getPrincipal() instanceof String
                                                && authentication.getPrincipal().equals("anonymousUser")) {
                        return ResponseEntity.status(401).body("Not authenticated");
                }
                // Assume that the principal is our custom User object
                com.tritonptms.ptms.user.User currentUser = (com.tritonptms.ptms.user.User) authentication
                                .getPrincipal();

                List<String> roles = currentUser.getRoles().stream()
                                .map(role -> role.getName().toString())
                                .collect(Collectors.toList());

                return ResponseEntity.ok(new UserInfoResponse(currentUser.getId(), currentUser.getUsername(), roles));
        }

}
