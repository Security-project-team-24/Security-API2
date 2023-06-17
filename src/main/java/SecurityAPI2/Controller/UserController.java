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
import net.bytebuddy.asm.Advice;
import org.apache.coyote.Response;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Page;
import org.w3c.dom.Document;


import javax.validation.Valid;
import java.io.Console;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    @PatchMapping("/block/{id}")
    @PreAuthorize("isAuthenticated() and hasAuthority('block_user')")
    public ResponseEntity<Void> block(@PathVariable Long id) {
        userService.block(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/unblock/{id}")
    @PreAuthorize("isAuthenticated() and hasAuthority('unblock_user')")
    public ResponseEntity<Void> unblock(@PathVariable Long id) {
        userService.unblock(id);
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

    @PatchMapping("/forgot-password/{email}")
    public ResponseEntity forgotPassword(@PathVariable String email) {
        userService.forgotPassword(email);
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
    public ResponseEntity uploadCv(@RequestParam("file") MultipartFile file, @RequestHeader(HttpHeaders.AUTHORIZATION) final String authHeader) throws IOException {
        final User user = authService.getUserFromToken(authHeader);
        System.out.println("YOY!");
        userService.uploadCv(file, user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/cv/download")
    @PreAuthorize("isAuthenticated() and hasAuthority('download_cv')")
    public ResponseEntity<?> getCv(@RequestHeader(HttpHeaders.AUTHORIZATION) final String authHeader) throws IOException {
        final User user = authService.getUserFromToken(authHeader);
        MultipartFile cv = userService.findCVForEngineer(user);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(cv.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + cv.getName() + "\"")
                .body(cv.getBytes());
    }

    @GetMapping("/cv/download/{fileName}")
    @PreAuthorize("isAuthenticated() and hasAuthority('download_cv')")
    public ResponseEntity<?> getCvByFileName(@PathVariable String fileName, @RequestHeader(HttpHeaders.AUTHORIZATION) final String authHeader) throws IOException {
        MultipartFile cv = userService.findCVByName(fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(cv.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + cv.getName() + "\"")
                .body(cv.getBytes());
    }

    @GetMapping("/engineers")
    @PreAuthorize("isAuthenticated() and hasAuthority('all')")
    public ResponseEntity<PageDto<EngineerDto>> getAllEngineers(@Valid @RequestParam int pageNumber,
                                                                @Valid @RequestParam String email,
                                                                @Valid @RequestParam String name,
                                                                @Valid @RequestParam String surname,
                                                                @Valid @RequestParam String fromDate,
                                                                @Valid @RequestParam String toDate){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        PageDto<EngineerDto> page = userService.getEngineers(pageNumber, email.trim(), name.trim(), surname.trim(), LocalDate.parse(fromDate, formatter), LocalDate.parse(toDate, formatter));
        return ResponseEntity.ok(page);
    }


}
