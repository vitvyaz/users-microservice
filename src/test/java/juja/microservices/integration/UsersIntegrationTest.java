package juja.microservices.integration;

import juja.microservices.users.dao.UserRepository;
import juja.microservices.users.entity.User;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
public class UsersIntegrationTest extends BaseIntegrationTest{

    private static final String USERS_URL = "/users";
    private static final String NAME_BY_UUID_URL = "/users/nameByUuid";
    private static final String UUID_BY_SLACK_URL = "/users/uuidBySlack";

    private MockMvc mockMvc;

    @MockBean
    private UserRepository repository;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void getAllUsers() throws Exception {

        //given
        List<User> users = new ArrayList<>();
        User user = new User("AAAA123", "Vasya","Ivanoff", "vasya@mail.ru",
                "vasya@gmail.com","vasya","vasya.ivanoff",
                "linkedin/vasya","facebook/vasya","twitter/vasya");
        users.add(user);
        String expected =
                "[{\"uuid\":\"AAAA123\",\"name\":\"Ivanoff Vasya\",\"skype\":\"vasya.ivanoff\",\"slack\":\"vasya\"}]";

        //when
        when(repository.getAllUsers()).thenReturn(users);
        String result = mockMvc.perform(get(USERS_URL)
                .contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        //then
        assertThatJson(result).isEqualTo(expected);
    }

    @Test
    public void getUserNameByUuid() throws Exception {

        //given
        User user = new User("AAAA123", "Vasya","Ivanoff", "vasya@mail.ru",
                "vasya@gmail.com","vasya","vasya.ivanoff",
                "linkedin/vasya","facebook/vasya","twitter/vasya");
        String jsonRequest = "{\"uuid\":[\"AAAA123\"]}";
        String expected =
                "[{\"uuid\":\"AAAA123\",\"name\":\"Ivanoff Vasya\"}]";

        //when
        when(repository.getUserByUuid("AAAA123")).thenReturn(user);
        String result = mockMvc.perform(post(NAME_BY_UUID_URL)
                .content(jsonRequest)
                .contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        //then
        assertThatJson(result).isEqualTo(expected);
    }

    @Test
    public void getUsersUuidBySlack() throws Exception {
        //given
        User user = new User("AAAA123", "Vasya","Ivanoff", "vasya@mail.ru",
                "vasya@gmail.com","vasya","vasya.ivanoff",
                "linkedin/vasya","facebook/vasya","twitter/vasya");
        String jsonRequest = "{\"slackNames\":[\"vasya\"]}";
        String expected =
                "[{\"uuid\":\"AAAA123\",\"slack\":\"vasya\"}]";

        //when
        when(repository.getUserBySlack("vasya")).thenReturn(user);
        String result = mockMvc.perform(post(UUID_BY_SLACK_URL)
                .content(jsonRequest)
                .contentType(APPLICATION_JSON_UTF8))
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        //then
        assertThatJson(result).isEqualTo(expected);
    }


}