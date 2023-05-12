package SecurityAPI2.Controller;

import SecurityAPI2.Dto.PageDto;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import javax.validation.Valid;
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
    @GetMapping("/employees/{pageSize}/{pageNumber}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<PageDto<UserDto>> findAll(@Valid @PathVariable int pageSize, @Valid @PathVariable int pageNumber) {
       Page<User> userPage = userService.findAll(pageSize, pageNumber);
       PageDto<UserDto> dto = new PageDto<>();
       dto.setContent(userMapper.usersToUserDtos(userPage.getContent()));
       dto.setTotalPages(userPage.getTotalPages());
       return ResponseEntity.ok(dto);
    }
    @GetMapping("/pending")
    public ResponseEntity<List<UserDto>> findPendingUsers() {
        return ResponseEntity.ok(userMapper.usersToUserDtos(userService.findPendingUsers()));
    }
    @PatchMapping("/update")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('ENGINEER') or hasAuthority('PROJECTMANAGER') or hasAuthority('HRMANAGER')")
    public ResponseEntity<UserDto> update(@RequestBody final UserDto userDto) {
        return ResponseEntity.ok(userMapper.userToUserDto(userService.update(userMapper.userDtoToUser(userDto))));
    }

    @PatchMapping("/approve/{id}")
    public ResponseEntity<Void> approve(@PathVariable Long id) {
        userService.approve(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/disapprove/{id}/{reason}")
    public ResponseEntity<Void> disapprove(@PathVariable Long id, @PathVariable final String reason) {
        userService.disapprove(id,reason);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/activate/{hmacToken}/{approvalId}")
    public ResponseEntity<Void> activateAccount(@PathVariable Long approvalId, @PathVariable final String hmacToken) {
        userService.activateAccount(hmacToken,approvalId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/change-password")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity changePassword(@Valid @RequestBody final PasswordChangeDto passwordChangeDto, Errors errors, @RequestHeader(HttpHeaders.AUTHORIZATION) final String authHeader) {
        if(errors.hasErrors()){
            throw new InvalidPasswordFormatException();
        }
        final User user = jwtUtils.getUserFromToken(authHeader);
        userService.changePassword(user, passwordChangeDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/skill")
    @PreAuthorize("hasAuthority('ENGINEER')")
    public ResponseEntity addSkill(@Valid @RequestBody SkillDto skillDto, Errors errors, @RequestHeader(HttpHeaders.AUTHORIZATION) final String authHeader){
        System.out.println(errors);
        if(errors.hasErrors()){
            throw new SkillValueInvalid();
        }
        final User user = jwtUtils.getUserFromToken(authHeader);
        userService.addSkill(skillDto, user);
        return ResponseEntity.ok().build();
    }

}
