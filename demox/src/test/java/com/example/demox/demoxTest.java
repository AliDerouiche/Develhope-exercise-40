package com.example.demox;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@ActiveProfiles(value = "test")
@AutoConfigureMockMvc
class demoxTest {

    @Autowired
    UserController userController;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
    }

    private UserEntity createUserEntity(String emailSuffix) {
        UserEntity user = new UserEntity();
        user.setPhoneNumber("+39333123456" + emailSuffix);
        user.setEmail("user" + emailSuffix + "@example.com");
        user.setFullName("Mario Rossi" + emailSuffix);
        user.setBirthDate("1990-01-01");
        return user;
    }

    @Test
    void createUser() throws Exception {
        UserEntity user = createUserEntity("1");

        MvcResult result = mvc.perform(post("/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        UserEntity createdUser = objectMapper.readValue(responseContent, UserEntity.class);

        assertNotNull(createdUser.getId());
        assertEquals(user.getFullName(), createdUser.getFullName());
        assertEquals(user.getEmail(), createdUser.getEmail());
        assertEquals(user.getBirthDate(), createdUser.getBirthDate());
        assertEquals(user.getPhoneNumber(), createdUser.getPhoneNumber());
    }

    @Test
    void getSingleUser() throws Exception {
        UserEntity user = userRepository.save(createUserEntity("2"));

        MvcResult result = mvc.perform(get("/user/get/{id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        UserEntity retrievedUser = objectMapper.readValue(responseContent, UserEntity.class);

        assertNotNull(retrievedUser.getId());
        assertEquals(user.getId(), retrievedUser.getId());
        assertEquals(user.getEmail(), retrievedUser.getEmail());
    }

    @Test
    void getAllUsers() throws Exception {
        UserEntity user1 = userRepository.save(createUserEntity("3"));
        UserEntity user2 = userRepository.save(createUserEntity("4"));

        MvcResult result = mvc.perform(get("/user/getAll")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        UserEntity[] usersArray = objectMapper.readValue(responseContent, UserEntity[].class);
        List<UserEntity> users = Arrays.asList(usersArray);

        assertEquals(2, users.size());
        assertTrue(users.contains(user1));
        assertTrue(users.contains(user2));
    }

    @Test
    void updateUser() throws Exception {
        UserEntity user = userRepository.save(createUserEntity("5"));
        UserEntity updatedUser = new UserEntity();
        updatedUser.setEmail("updated@example.com");
        updatedUser.setPhoneNumber("+393339876543");

        MvcResult result = mvc.perform(put("/user/update/{id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        UserEntity retrievedUser = objectMapper.readValue(responseContent, UserEntity.class);

        assertEquals(user.getId(), retrievedUser.getId());
        assertEquals(updatedUser.getEmail(), retrievedUser.getEmail());
        assertEquals(updatedUser.getPhoneNumber(), retrievedUser.getPhoneNumber());
    }

    @Test
    void deleteUser() throws Exception {
        UserEntity user = userRepository.save(createUserEntity("6"));

        mvc.perform(delete("/user/delete/{id}", user.getId()))
                .andExpect(status().isNoContent());

        assertFalse(userRepository.existsById(user.getId()));
    }
}
