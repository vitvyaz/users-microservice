package juja.microservices.users.controller;

import juja.microservices.users.entity.User;
import juja.microservices.users.entity.UserSearchRequest;
import juja.microservices.users.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;

@RestController
@Validated
public class UserController {

    private static final String DEFAULT_PAGE_SIZE = "20";

    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Inject
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> getUsers(@RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize,
                                      @RequestParam(required=false) Integer page) {
        List<User> users;
        if(page != null) {
            users = userService.getUsers(page, pageSize);
            logger.info(String.format("Successfully got users with parameters: page = %s, pageSize = %s", page, pageSize));
        } else {
            users = userService.getAllUsers();
            logger.info("Successfully got all users");
        }

        return ResponseEntity.ok(users);
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

}