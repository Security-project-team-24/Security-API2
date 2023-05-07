package SecurityAPI2.Controller;

import SecurityAPI2.Dto.UserDto;
import SecurityAPI2.Mapper.UserMapper;
import SecurityAPI2.Model.User;
import SecurityAPI2.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;
    @GetMapping("/employees")
    public ResponseEntity<List<UserDto>> findAll() {
       return ResponseEntity.ok(userMapper.usersToUserDtos(userService.findAll()));
    }
    @PatchMapping("/update")
    public ResponseEntity<User> update(@RequestBody final UserDto userDto) {
        return ResponseEntity.ok(userService.update(userMapper.userDtoToUser(userDto)));
    }
}
