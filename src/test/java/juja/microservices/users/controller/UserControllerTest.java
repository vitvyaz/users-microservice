package juja.microservices.users.controller;

import juja.microservices.users.entity.User;
import juja.microservices.users.exceptions.UserException;
import juja.microservices.users.entity.UserSearchRequest;
import juja.microservices.users.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.inject.Inject;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;

/**
 * @author Denis Tantsev (dtantsev@gmail.com)
 */

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    private static final String ALL_USERS = "[" +
            "{\"uuid\":\"AAAA123\",\"firstName\":\"Vasya\",\"lastName\":\"Ivanoff\"," +
            "\"email\":\"vasya@mail.ru\",\"gmail\":\"vasya@gmail.com\",\"slack\":\"vasya\",\"skype\":\"vasya.ivanoff\"," +
            "\"linkedin\":\"linkedin/vasya\",\"facebook\":\"facebook/vasya\",\"twitter\":\"twitter/vasya\"}," +

            "{\"uuid\":\"AAAA456\",\"firstName\":\"Kolya\",\"lastName\":\"Sidoroff\"," +
            "\"email\":\"kolya@mail.ru\",\"gmail\":\"kolya@gmail.com\",\"slack\":\"kolya\",\"skype\":\"kolya.sidoroff\"," +
            "\"linkedin\":\"linkedin/kolya\",\"facebook\":\"facebook/kolya\",\"twitter\":\"twitter/kolya\"}," +

            "{\"uuid\":\"AAAA789\",\"firstName\":\"Lena\",\"lastName\":\"Petrova\"," +
            "\"email\":\"lena@mail.ru\",\"gmail\":\"lena@gmail.com\",\"slack\":\"lena\",\"skype\":\"lena.petrova\"," +
            "\"linkedin\":\"linkedin/lena\",\"facebook\":\"facebook/lena\",\"twitter\":\"twitter/lena\"}" +
            "]";

    private static final String USER_VASYA = "[{\"uuid\":\"AAAA123\",\"firstName\":\"Vasya\",\"lastName\":\"Ivanoff\"," +
            "\"email\":\"vasya@mail.ru\",\"gmail\":\"vasya@gmail.com\",\"slack\":\"vasya\",\"skype\":\"vasya.ivanoff\"," +
            "\"linkedin\":\"linkedin/vasya\",\"facebook\":\"facebook/vasya\",\"twitter\":\"twitter/vasya\"}]";

    @Inject
    private MockMvc mockMvc;

    @MockBean
    private UserService service;

    @Test
    public void getAllUsers() throws Exception {
        List<User> users = new ArrayList<>();
        users.add(new User("AAAA123", "Vasya", "Ivanoff", "vasya@mail.ru", "vasya@gmail.com", "vasya", "vasya.ivanoff",
                "linkedin/vasya", "facebook/vasya", "twitter/vasya"));
        when(service.getUsers(0,1)).thenReturn(users);
        String result = mockMvc.perform(get("/users?page=0&pageSize=1")
                .contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThatJson(result).isEqualTo(USER_VASYA);
    }

    @Test
    public void getAllUsersFilterFields() throws Exception {
        List<User> users = new ArrayList<>();
        users.add(new User("AAAA123", "Vasya", "Ivanoff", "vasya@mail.ru", "vasya@gmail.com", "vasya", "vasya.ivanoff",
                "linkedin/vasya", "facebook/vasya", "twitter/vasya"));
        when(service.getAllUsers()).thenReturn(users);
        String result = mockMvc.perform(get("/users?fields=uuid,firstName,lastName")
                .contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThatJson(result).isEqualTo(USER_VASYA);
    }

    @Test
    public void getAllUsersWithoutPageAndPageSize() throws Exception {
        List<User> users = new ArrayList<>();
        users.add(new User("AAAA123", "Vasya", "Ivanoff", "vasya@mail.ru", "vasya@gmail.com", "vasya", "vasya.ivanoff",
                "linkedin/vasya", "facebook/vasya", "twitter/vasya"));
        users.add(new User("AAAA456", "Kolya", "Sidoroff", "kolya@mail.ru", "kolya@gmail.com", "kolya", "kolya.sidoroff",
                "linkedin/kolya", "facebook/kolya", "twitter/kolya"));
        users.add(new User("AAAA789", "Lena", "Petrova", "lena@mail.ru", "lena@gmail.com", "lena", "lena.petrova",
                "linkedin/lena", "facebook/lena", "twitter/lena"));

        when(service.getAllUsers()).thenReturn(users);
        String result = mockMvc.perform(get("/users")
                .contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThatJson(result).isEqualTo(ALL_USERS);
    }

    @Test
    public void searchUserByEmailTest() throws Exception {
        List<User> users = new ArrayList<>();
        users.add(new User("AAAA123", "Vasya", "Ivanoff", "vasya@mail.ru", "vasya@gmail.com", "vasya", "vasya.ivanoff",
                "linkedin/vasya", "facebook/vasya", "twitter/vasya"));

        UserSearchRequest request = new UserSearchRequest();
        request.email = "vasya@mail.ru";
        when(service.searchUser(request)).thenReturn(users);
        String result = mockMvc.perform(get("/users/search")
                .param("email", "vasya@mail.ru")
                .contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThatJson(result).isEqualTo(USER_VASYA);

    }

    @Test
    public void searchUserByUuid() throws Exception {
        User user = new User("AAAA123", "Vasya", "Ivanoff", "vasya@mail.ru", "vasya@gmail.com", "vasya", "vasya.ivanoff",
                "linkedin/vasya", "facebook/vasya", "twitter/vasya");
        when(service.searchUser("AAAA123")).thenReturn(user);
        String result = mockMvc.perform(get("/users/AAAA123")
                .contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThatJson(result).isEqualTo(user);
    }

    @Test
    public void shouldThrowBadRequestIfNonExistentUuid() throws Exception {
        when(service.searchUser("nonExistentUuid")).thenThrow(new UserException("No users found by your request!"));
        String result = mockMvc.perform(get("/users/nonExistentUuid")
                .contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();
        assertThatJson("{\"httpStatus\":400,\"internalErrorCode\":0,\"clientMessage\":\"Oops something went wrong :(\"," +
                "\"developerMessage\":\"General exception for this service\",\"exceptionMessage\":\"No users found by your request!\"," +
                "\"detailErrors\":[]}").isEqualTo(result);
    }
}