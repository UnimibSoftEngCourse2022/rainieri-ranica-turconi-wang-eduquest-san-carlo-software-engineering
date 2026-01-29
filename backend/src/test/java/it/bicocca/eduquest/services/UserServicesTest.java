package it.bicocca.eduquest.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import it.bicocca.eduquest.domain.users.Role;
import it.bicocca.eduquest.domain.users.Student;
import it.bicocca.eduquest.domain.users.Teacher;
import it.bicocca.eduquest.domain.users.User;
import it.bicocca.eduquest.dto.user.UserInfoDTO;
import it.bicocca.eduquest.dto.user.UserLoginDTO;
import it.bicocca.eduquest.dto.user.UserLoginResponseDTO;
import it.bicocca.eduquest.dto.user.UserRegistrationDTO;
import it.bicocca.eduquest.repository.UsersRepository;
import it.bicocca.eduquest.security.JwtUtils;

@ExtendWith(MockitoExtension.class)
class UserServicesTest {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private UserServices userServices;

    @Test
    void testRegisterUser_Success_Student() {
        UserRegistrationDTO regDto = new UserRegistrationDTO();
        regDto.setName("Mario");
        regDto.setSurname("Rossi");
        regDto.setEmail("mario@test.com");
        regDto.setPassword("plainPassword");
        regDto.setRole(Role.STUDENT);

        when(usersRepository.findByEmail(regDto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(regDto.getPassword())).thenReturn("encodedPassword123");

        Student savedUser = new Student("Mario", "Rossi", "mario@test.com", "encodedPassword123");
        savedUser.setId(1L);

        when(usersRepository.save(any(Student.class))).thenReturn(savedUser);

        UserInfoDTO result = userServices.registerUser(regDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(Role.STUDENT, result.getRole());
        verify(usersRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterUser_Success_Teacher() {
        UserRegistrationDTO regDto = new UserRegistrationDTO();
        regDto.setName("Prof");
        regDto.setSurname("Bianchi");
        regDto.setEmail("prof@test.com");
        regDto.setPassword("secure");
        regDto.setRole(Role.TEACHER);

        when(usersRepository.findByEmail(regDto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode("secure")).thenReturn("encoded");

        Teacher savedTeacher = new Teacher("Prof", "Bianchi", "prof@test.com", "encoded");
        savedTeacher.setId(2L);

        when(usersRepository.save(any(Teacher.class))).thenReturn(savedTeacher);

        UserInfoDTO result = userServices.registerUser(regDto);

        assertEquals(Role.TEACHER, result.getRole());
        assertEquals("Prof", result.getName());
    }

    @Test
    void testRegisterUser_Fail_EmailAlreadyExists() {
        UserRegistrationDTO regDto = new UserRegistrationDTO();
        regDto.setEmail("mario@test.com");

        when(usersRepository.findByEmail("mario@test.com")).thenReturn(Optional.of(new User()));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userServices.registerUser(regDto);
        });

        assertEquals("Email already registered!", exception.getMessage());
        verify(usersRepository, never()).save(any());
    }

    @Test
    void testLoginUser_Success() {
        UserLoginDTO loginDto = new UserLoginDTO("mario@test.com", "secretPassword");

        User foundUser = new User("Mario", "Rossi", "mario@test.com", "encodedPassword");
        foundUser.setId(10L);
        foundUser.setRole(Role.TEACHER);

        when(usersRepository.findByEmail("mario@test.com")).thenReturn(Optional.of(foundUser));
        when(passwordEncoder.matches("secretPassword", "encodedPassword")).thenReturn(true);
        when(jwtUtils.generateToken(10L)).thenReturn("fake-jwt-token");

        UserLoginResponseDTO response = userServices.loginUser(loginDto);

        assertNotNull(response);
        assertEquals("fake-jwt-token", response.getToken());
        assertEquals(10L, response.getUserId());
    }

    @Test
    void testLoginUser_Fail_UserNotFound() {
        UserLoginDTO loginDto = new UserLoginDTO("ghost@test.com", "any");

        when(usersRepository.findByEmail("ghost@test.com")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userServices.loginUser(loginDto);
        });

        assertEquals("Utente non trovato", exception.getMessage());
    }

    @Test
    void testLoginUser_Fail_WrongPassword() {
        UserLoginDTO loginDto = new UserLoginDTO("mario@test.com", "wrongPassword");
        User foundUser = new User();
        foundUser.setPassword("encodedPassword");

        when(usersRepository.findByEmail("mario@test.com")).thenReturn(Optional.of(foundUser));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userServices.loginUser(loginDto);
        });

        assertEquals("Password errata", exception.getMessage());
    }

    @Test
    void testGetUserInfo_Success() {
        User user = new User("Luigi", "Verdi", "luigi@test.com", "pass");
        user.setId(5L);
        user.setRole(Role.STUDENT);

        when(usersRepository.findById(5L)).thenReturn(Optional.of(user));

        UserInfoDTO info = userServices.getUserInfo(5L);

        assertEquals("Luigi", info.getName());
        assertEquals(5L, info.getId());
    }

    @Test
    void testGetUserInfo_NotFound() {
        when(usersRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userServices.getUserInfo(99L);
        });

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testGetUserInfoFromJwt() {
        String fakeJwt = "bearer-token";
        long userId = 123L;

        User user = new User("Jwt", "User", "jwt@test.com", "pass");
        user.setId(userId);
        user.setRole(Role.STUDENT);

        when(jwtUtils.getUserIdFromToken(fakeJwt)).thenReturn(userId);
        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));

        UserInfoDTO info = userServices.getUserInfoFromJwt(fakeJwt);

        assertEquals(userId, info.getId());
        assertEquals("Jwt", info.getName());
    }

    @Test
    void testGetAllUsers() {
        User u1 = new User("A", "B", "a@b.com", "p"); u1.setId(1L);
        User u2 = new User("C", "D", "c@d.com", "p"); u2.setId(2L);

        when(usersRepository.findAll()).thenReturn(Arrays.asList(u1, u2));

        List<UserInfoDTO> results = userServices.getAllUsers();

        assertEquals(2, results.size());
        assertEquals(1L, results.get(0).getId());
        assertEquals(2L, results.get(1).getId());
    }
}