package me.artemiyulyanov.uptodate.services;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import me.artemiyulyanov.uptodate.minio.MinioService;
import me.artemiyulyanov.uptodate.minio.resources.UserResourceManager;
import me.artemiyulyanov.uptodate.models.Role;
import me.artemiyulyanov.uptodate.models.User;
import me.artemiyulyanov.uptodate.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Primary
@Transactional
public class UserService implements UserDetailsService, ResourceService<UserResourceManager> {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleService roleService;

    @Autowired
    private MinioService minioService;

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        if (userRepository.count() > 0) return;

        User testUser = User.builder()
                .username("Artemiy")
                .email("artemij.honor@gmail.com")
                .firstName("Artemiy")
                .lastName("Ulyanov")
                .password(passwordEncoder.encode("HelloBro31"))
                .roles(Set.of(roleService.findRoleByName("USER"), roleService.findRoleByName("ADMIN")))
                .build();

        userRepository.save(testUser);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Collection<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }

    public boolean userExistsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public void createNewUser(User user) {
        Role basicRole = roleService.findRoleByName("USER");
        user.setRoles(Set.of(basicRole));

        userRepository.save(user);
    }

    public void editUser(Long id, String username, String firstName, String lastName, MultipartFile icon) {
        User newUser = userRepository.findById(id).get();

        newUser.setUsername(username);
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);

        String iconObjectKey = getResourceManager().getResourceFolder(newUser) + File.separator + icon.getOriginalFilename();
        getResourceManager().updateResources(newUser, List.of(icon));

        newUser.setIcon(iconObjectKey);
        userRepository.save(newUser);
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean userExists(String username, String email) {
        return existsByEmail(email) || existsByUsername(username);
    }

    public boolean isUserVaild(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.filter(value -> passwordEncoder.matches(password, value.getPassword())).isPresent();
    }

    @Override
    public UserResourceManager getResourceManager() {
        return UserResourceManager
                .builder()
                .userRepository(userRepository)
                .minioService(minioService)
                .build();
    }
}