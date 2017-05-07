package juja.microservices.users.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import juja.microservices.users.entity.User;
import juja.microservices.users.entity.UserSearchRequest;
import juja.microservices.users.exceptions.UserException;
import juja.microservices.users.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@RestController
@Validated
public class UserController {

    private static final String DEFAULT_PAGE_SIZE = "20";
    private static final String DEFAULT_FIELDS = "all";

    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> getUsers(@RequestParam(required = false) Integer page,
                                      @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize,
                                      @RequestParam(defaultValue = DEFAULT_FIELDS) String fields)
                                      throws JsonProcessingException {
        List<User> users;
        if (page != null) {
            users = userService.getUsers(page, pageSize);
            logger.info(String.format("Got users with parameters: page = %s, pageSize = %s", page, pageSize));
        } else {
            users = userService.getAllUsers();
            logger.info("Got all users");
        }

        if (fields.equals(DEFAULT_FIELDS)) {
            logger.info("Return all users");
            return ResponseEntity.ok(users);
        } else {
            List<String> requiredFields = Arrays.asList(fields.split(","));
            validateFields(requiredFields);

            String usersJson = getJson(users, requiredFields);
            logger.info(String.format("Return usersJson with filtered fields: %s", page, pageSize));
            return ResponseEntity.ok(usersJson);
        }

    }

    @RequestMapping(value = "/users/search", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> searchUser(@Validated UserSearchRequest request){
        List<User> users = userService.searchUser(request);
        logger.info("Search for users by: {} completed", request.toString());
        return ResponseEntity.ok(users);
    }


    @RequestMapping(value = "/users/{uuid}", method = RequestMethod.GET, produces = "application/json" )
    @ResponseBody
    public ResponseEntity<?> searchUserByUuid(@PathVariable("uuid") String uuid){
        User user = userService.searchUser(uuid);
        logger.info("Search for users by: {} completed", user.toString());
        return ResponseEntity.ok(user);
    }

    private String getJson(List<User> users, List<String> requiredFields) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        SimpleBeanPropertyFilter theFilter = SimpleBeanPropertyFilter
                .filterOutAllExcept(new HashSet<>(requiredFields));
        FilterProvider filters = new SimpleFilterProvider()
                .addFilter("fieldsFilter", theFilter);

        return mapper.writer(filters).writeValueAsString(users);
    }

    private void validateFields(List<String> requiredFields) {
        Field[] allFields = User.class.getDeclaredFields();
        List<String> allFieldsNames = new ArrayList<>();
        for (Field field : allFields) {
            allFieldsNames.add(field.getName());
        }

        if (!allFieldsNames.containsAll(requiredFields)) {
            throw new UserException("Wrong parameter 'fields', some field's name is wrong");
        }
    }
}
