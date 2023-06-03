package SecurityAPI2.Controller;

import SecurityAPI2.Dto.*;
import SecurityAPI2.Exceptions.InvalidPasswordFormatException;
import SecurityAPI2.Exceptions.SkillValueInvalid;
import SecurityAPI2.Model.Engineer;
import SecurityAPI2.Model.Skill;
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
    private final AuthService authService;

    @GetMapping("/employees/{pageSize}/{pageNumber}")
    @PreAuthorize("isAuthenticated() and hasAuthority('read_all_users')")
    public ResponseEntity<PageDto<UserDto>> findAll(@Valid @PathVariable int pageSize, @Valid @PathVariable int pageNumber) {
       Page<User> userPage = userService.findAll(pageSize, pageNumber);
       PageDto<UserDto> dto = new PageDto<>();
       dto.setContent(UserDto.toDtos(userPage.getContent()));
       dto.setTotalPages(userPage.getTotalPages());
       return ResponseEntity.ok(dto);
    }
    @GetMapping("/pending/{pageSize}/{pageNumber}")
    @PreAuthorize("isAuthenticated() and hasAuthority('read_pending_users')")
    public ResponseEntity<PageDto<UserDto>> findPendingUsers(@Valid @PathVariable int pageSize, @Valid @PathVariable int pageNumber) {
        Page<User> userPage = userService.findPendingUsers(pageSize, pageNumber);
        PageDto<UserDto> dto = new PageDto<>();
        dto.setContent(UserDto.toDtos(userPage.getContent()));
        dto.setTotalPages(userPage.getTotalPages());
        return ResponseEntity.ok(dto);
    }
    @PatchMapping("/update")
    @PreAuthorize("isAuthenticated() and hasAuthority('all')")
    public ResponseEntity<UserDto> update(@RequestBody final UserDto userDto) {
        User user = userService.update(userDto.toModel());
        UserDto dto = new UserDto(user);
        return ResponseEntity.ok(dto);
    }

    @PatchMapping("/approve/{id}")
    @PreAuthorize("isAuthenticated() and hasAuthority('update_users_approval')")
    public ResponseEntity<Void> approve(@PathVariable Long id) {
        userService.approve(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/disapprove/{id}/{reason}")
    @PreAuthorize("isAuthenticated() and hasAuthority('update_users_approval')")
    public ResponseEntity<Void> disapprove(@PathVariable Long id, @PathVariable final String reason) {
        userService.disapprove(id,reason);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/activate/{registerToken}")
    public ResponseEntity<Void> activateAccount(@PathVariable String registerToken) {
        userService.activateAccount(registerToken);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/change-password")
    @PreAuthorize("isAuthenticated() and hasAuthority('all')")
    public ResponseEntity changePassword(@Valid @RequestBody final PasswordChangeDto passwordChangeDto, Errors errors, @RequestHeader(HttpHeaders.AUTHORIZATION) final String authHeader) {
        if(errors.hasErrors()){
            throw new InvalidPasswordFormatException();
        }
        final User user = authService.getUserFromToken(authHeader);
        userService.changePassword(user, passwordChangeDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/skill")
    @PreAuthorize("isAuthenticated() and hasAuthority('crud_skill')")
    public ResponseEntity addSkill(@Valid @RequestBody SkillDto skillDto, Errors errors, @RequestHeader(HttpHeaders.AUTHORIZATION) final String authHeader){
        if(errors.hasErrors()){
            throw new SkillValueInvalid();
        }
        final User user = authService.getUserFromToken(authHeader);
        userService.addSkill(skillDto, user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{engineerId}/skills")
    @PreAuthorize("isAuthenticated() and hasAuthority('crud_skill')")
    public ResponseEntity<List<EngineerSkillDto>> getEngineerSkills(@PathVariable Long engineerId){
        List<Skill> skills = userService.getEngineerSkills(engineerId);
        return ResponseEntity.ok(EngineerSkillDto.mapSkillToDto(skills));
    }

    @PatchMapping("/skill")
    @PreAuthorize("isAuthenticated() and hasAuthority('crud_skill')")
    public ResponseEntity updateSkill(@RequestBody EngineerSkillDto skillDto, @RequestHeader(HttpHeaders.AUTHORIZATION) final String authHeader){
        final User user = authService.getUserFromToken(authHeader);
        userService.updateSkill(skillDto, user);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/engineer/skill/{skillId}")
    @PreAuthorize("isAuthenticated() and hasAuthority('crud_skill')")
    public ResponseEntity deleteEngineerSkill( @PathVariable Long skillId){
        userService.deleteEngineerSkill(skillId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/cv/upload")
    @PreAuthorize("isAuthenticated() and hasAuthority('create_cv')")
    public ResponseEntity uploadCv(@RequestParam("file") MultipartFile file,  @RequestHeader(HttpHeaders.AUTHORIZATION) final String authHeader) throws IOException {
        final User user = authService.getUserFromToken(authHeader);
        userService.uploadCv(file, user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/engineers/{pageNumber}")
    @PreAuthorize("isAuthenticated() and hasAuthority('all')")
    public ResponseEntity<PageDto<EngineerDto>> getAllEngineers(@Valid @PathVariable int pageNumber){
        Page<Engineer> engineerPage = userService.getEngineers(pageNumber);
        List<EngineerDto> engineers = EngineerDto.engineerDtosFromEngineers(engineerPage.getContent());
        PageDto<EngineerDto> dto = new PageDto<>();
        dto.setContent(engineers);
        dto.setTotalPages(engineerPage.getTotalPages());
        return ResponseEntity.ok(dto);
    }


}
