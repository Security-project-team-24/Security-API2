package SecurityAPI2.Controller;

import SecurityAPI2.Dto.PasswordChangeDto;
import SecurityAPI2.Dto.SkillDto;
import SecurityAPI2.Dto.UserDto;
import SecurityAPI2.Exceptions.InvalidPasswordFormatException;
import SecurityAPI2.Exceptions.SkillValueInvalid;
import SecurityAPI2.Mapper.UserMapper;
import SecurityAPI2.Model.User;
import SecurityAPI2.Security.JwtUtils;
import SecurityAPI2.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    JwtUtils jwtUtils;

    @GetMapping("/employees")
    public ResponseEntity<List<UserDto>> findAll() {
       return ResponseEntity.ok(userMapper.usersToUserDtos(userService.findAll()));
    }
    @PatchMapping("/update")
    public ResponseEntity<UserDto> update(@RequestBody final UserDto userDto) {
        return ResponseEntity.ok(userMapper.userToUserDto(userService.update(userMapper.userDtoToUser(userDto))));
    }

    @PutMapping("/change-password")
    public ResponseEntity changePassword(@Valid @RequestBody final PasswordChangeDto passwordChangeDto, Errors errors, @RequestHeader(HttpHeaders.AUTHORIZATION) final String authHeader) {
        if(errors.hasErrors()){
            throw new InvalidPasswordFormatException();
        }
        final User user = jwtUtils.getUserFromToken(authHeader);
        userService.changePassword(user, passwordChangeDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/skill")
    public ResponseEntity addSkill(@Valid @RequestBody SkillDto skillDto, Errors errors, @RequestHeader(HttpHeaders.AUTHORIZATION) final String authHeader){
        if(errors.hasErrors()){
            throw new SkillValueInvalid();
        }
        final User user = jwtUtils.getUserFromToken(authHeader);
        userService.addSkill(skillDto, user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/cv/upload")
    public ResponseEntity uploadCv(@RequestParam("file") MultipartFile file,  @RequestHeader(HttpHeaders.AUTHORIZATION) final String authHeader) throws IOException {
        final User user = jwtUtils.getUserFromToken(authHeader);
        userService.uploadCv(file, user);
        return ResponseEntity.ok().build();
    }

}
