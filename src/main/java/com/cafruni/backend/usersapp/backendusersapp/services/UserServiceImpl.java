package com.cafruni.backend.usersapp.backendusersapp.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cafruni.backend.usersapp.backendusersapp.models.dto.UserDto;
import com.cafruni.backend.usersapp.backendusersapp.models.dto.mapper.DtoMapperUser;
import com.cafruni.backend.usersapp.backendusersapp.models.entities.Role;
import com.cafruni.backend.usersapp.backendusersapp.models.entities.User;
import com.cafruni.backend.usersapp.backendusersapp.models.request.UserRequest;
import com.cafruni.backend.usersapp.backendusersapp.repositories.RoleRepository;
import com.cafruni.backend.usersapp.backendusersapp.repositories.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        List<User> user = (List<User>) repository.findAll();

        return user
                .stream()
                .map(u -> DtoMapperUser.builder().setUser(u).build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDto> findById(Long id) {
        return repository.findById(id).map(u -> DtoMapperUser
                .builder()
                .setUser(u)
                .build());
    }

    @Override
    @Transactional
    public UserDto save(User user) {
        String passwordBCrypt = passwordEncoder.encode(user.getPassword());
        user.setPassword(passwordBCrypt);

        Optional<Role> optional = roleRepository.findByName("ROLE_USER");
        List<Role> roles = new ArrayList<>();
        if (optional.isPresent()) {
            roles.add(optional.orElseThrow());
        }
        user.setRoles(roles);

        return DtoMapperUser.builder().setUser(repository.save(user)).build();
    }

    @Override
    @Transactional
    public void remove(Long id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public Optional<UserDto> update(UserRequest user, Long id) {
        Optional<User> optional = repository.findById(id);
        User userOptional = null;
        if (optional.isPresent()) {
            User userDB = optional.orElseThrow();
            userDB.setEmail(user.getEmail());
            userDB.setUsername(user.getUsername());
            userOptional = repository.save(userDB);
        }
        return Optional.ofNullable(DtoMapperUser.builder().setUser(userOptional).build());
    }

}
