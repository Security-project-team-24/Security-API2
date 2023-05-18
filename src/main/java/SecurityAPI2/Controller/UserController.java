package SecurityAPI2.Controller;

import SecurityAPI2.Dto.PageDto;
import SecurityAPI2.Dto.PasswordChangeDto;
import SecurityAPI2.Dto.SkillDto;
import SecurityAPI2.Dto.UserDto;
import SecurityAPI2.Exceptions.InvalidPasswordFormatException;
import SecurityAPI2.Exceptions.SkillValueInvalid;
import SecurityAPI2.Mapper.UserMapper;
import SecurityAPI2.Model.User;
import SecurityAPI2.Service.AuthService;
import SecurityAPI2.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Page;


import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;
    private final AuthService authService;

    @GetMapping("/employees/{pageSize}/{pageNumber}")
    @PreAuthorize("isAuthenticated() and hasAuthority('ADMIN')")
    public ResponseEntity<PageDto<UserDto>> findAll(@Valid @PathVariable int pageSize, @Valid @PathVariable int pageNumber) {
       Page<User> userPage = userService.findAll(pageSize, pageNumber);
       PageDto<UserDto> dto = new PageDto<>();
       dto.setContent(userMapper.usersToUserDtos(userPage.getContent()));
       dto.setTotalPages(userPage.getTotalPages());
       return ResponseEntity.ok(dto);
    }
    @GetMapping("/pending/{pageSize}/{pageNumber}")
    @PreAuthorize("isAuthenticated() and hasAuthority('ADMIN')")
    public ResponseEntity<PageDto<UserDto>> findPendingUsers(@Valid @PathVariable int pageSize, @Valid @PathVariable int pageNumber) {
        Page<User> userPage = userService.findPendingUsers(pageSize, pageNumber);
        PageDto<UserDto> dto = new PageDto<>();
        dto.setContent(userMapper.usersToUserDtos(userPage.getContent()));
        dto.setTotalPages(userPage.getTotalPages());
        return ResponseEntity.ok(dto);
    }
    @PatchMapping("/update")
    @PreAuthorize("isAuthenticated() and hasAuthority('ADMIN') or hasAuthority('ENGINEER') or hasAuthority('PROJECTMANAGER') or hasAuthority('HRMANAGER')")
    public ResponseEntity<UserDto> update(@RequestBody final UserDto userDto) {
        return ResponseEntity.ok(userMapper.userToUserDto(userService.update(userMapper.userDtoToUser(userDto))));
    }

    @PatchMapping("/approve/{id}")
    @PreAuthorize("isAuthenticated() and hasAuthority('ADMIN')")
    public ResponseEntity<Void> approve(@PathVariable Long id) {
        userService.approve(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/disapprove/{id}/{reason}")
    @PreAuthorize("isAuthenticated() and hasAuthority('ADMIN')")
    public ResponseEntity<Void> disapprove(@PathVariable Long id, @PathVariable final String reason) {
        userService.disapprove(id,reason);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/activate/{hmacToken}")
    public ResponseEntity<Void> activateAccount(@PathVariable String hmacToken) {
        hmacToken = hmacToken + " ";
        userService.activateAccount(hmacToken);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/change-password")
    @PreAuthorize("isAuthenticated() and hasAuthority('ADMIN')")
    public ResponseEntity changePassword(@Valid @RequestBody final PasswordChangeDto passwordChangeDto, Errors errors, @RequestHeader(HttpHeaders.AUTHORIZATION) final String authHeader) {
        if(errors.hasErrors()){
            throw new InvalidPasswordFormatException();
        }
        final User user = authService.getUserFromToken(authHeader);
        userService.changePassword(user, passwordChangeDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/skill")
    @PreAuthorize("isAuthenticated() and hasAuthority('ENGINEER')")
    public ResponseEntity addSkill(@Valid @RequestBody SkillDto skillDto, Errors errors, @RequestHeader(HttpHeaders.AUTHORIZATION) final String authHeader){
        if(errors.hasErrors()){
            throw new SkillValueInvalid();
        }
        final User user = authService.getUserFromToken(authHeader);
        userService.addSkill(skillDto, user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/cv/upload")
    @PreAuthorize("isAuthenticated() and hasAuthority('ENGINEER')")
    public ResponseEntity uploadCv(@RequestParam("file") MultipartFile file,  @RequestHeader(HttpHeaders.AUTHORIZATION) final String authHeader) throws IOException {
        final User user = authService.getUserFromToken(authHeader);
        userService.uploadCv(file, user);
        return ResponseEntity.ok().build();
    }

}
