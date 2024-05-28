package com.example.demox;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.List;

@SpringBootTest
@ActiveProfiles(value = "test")
@AutoConfigureMockMvc
class UnitTestApplicationTests {

    @Autowired
    UserController userController;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void contextLoads() {
        assertThat(userController).isNotNull();
    }

    private UserEntity userCreation() {
        UserEntity user = new UserEntity();
        user.setPhoneNumber("123456");
        user.setEmail("email@gmail.com");
        user.setFullName("marco");
        user.setBirthDate("12/12/1990");
        return user;
    }

    @Test
    void createUser() throws Exception {
        UserEntity user = userCreation();

        String userJSON =objectMapper.writeValueAsString(user);

        MvcResult result = this.mvc.perform(post("/user/")
                        .contentType(MediaType.APPLICATION_JSON).content(userJSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        UserEntity userResult = objectMapper.readValue(result.getResponse().getContentAsString(), UserEntity.class);

        assertThat(userResult.getId()).isNotNull();

        assertThat(userResult.getEmail()).isEqualTo(user.getEmail());
        assertThat(userResult.getPhoneNumber()).isEqualTo(user.getPhoneNumber());

    }

    @Test
    void updateUser() throws Exception {
        UserEntity user = userCreation();
        UserEntity user1 = userCreation();
        user1.setEmail("email@gmail.com");
        user1.setPhoneNumber("123456");

        userRepository.save(user);

        String userJSON = objectMapper.writeValueAsString(user1);

        MvcResult result = this.mvc.perform(put("/user/" + user.getId()).contentType(MediaType.APPLICATION_JSON).content(userJSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        UserEntity resultUser = objectMapper.readValue(result.getResponse().getContentAsString(), UserEntity.class);

        assertThat(resultUser.getId()).isNotNull();
    }

    @Test
    void deleteUser() throws Exception {
        UserEntity user = userCreation();

        userRepository.save(user);

        this.mvc.perform(delete("/user/" + user.getId()))
                .andExpect(status().isOk());

        assertThat(userRepository.findById(user.getId())).isEmpty();
    }

    @Test
    void getSingleUser() throws Exception {
        UserEntity user = userCreation();

        userRepository.save(user);

        MvcResult result = this.mvc.perform(get("/user/" + user.getId()).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        UserEntity userResult = objectMapper.readValue(result.getResponse().getContentAsString(), UserEntity.class);

        assertThat(userResult.getId()).isNotNull();

        assertThat(userResult.getId()).isEqualTo(user.getId());
        assertThat(userResult.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void getAllUsers() throws Exception {
        UserEntity user = userCreation();
        UserEntity user1 = userCreation();
        user1.setEmail("email@gmail.com");

        userRepository.save(user);
        userRepository.save(user1);

        MvcResult result = this.mvc.perform(get("/user/").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        List usersResult = objectMapper.readValue(result.getResponse().getContentAsString(), List.class);

        assertThat(usersResult).hasSize(2);

        assertThat(usersResult).extracting("fullName").contains("marco", "marc");
        assertThat(usersResult).extracting("email").contains("email@gmail.com", "email");
    }

}