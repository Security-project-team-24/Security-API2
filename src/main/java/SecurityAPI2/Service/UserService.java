package SecurityAPI2.Service;

import SecurityAPI2.Dto.EngineerSkillDto;
import SecurityAPI2.Dto.PasswordChangeDto;
import SecurityAPI2.Dto.SkillDto;
import SecurityAPI2.Exceptions.IncorrectPassword;
import SecurityAPI2.Exceptions.TokenExceptions.InvalidTokenClaimsException;
import SecurityAPI2.Exceptions.TokenExceptions.TokenExpiredException;
import SecurityAPI2.Exceptions.TokenExceptions.TokenInvalidException;
import SecurityAPI2.Exceptions.UserDoesntExistException;
import SecurityAPI2.Exceptions.*;
import SecurityAPI2.Model.*;
import SecurityAPI2.Model.Enum.UserRole;
import SecurityAPI2.Repository.*;

import SecurityAPI2.Security.JwtUtils;
import SecurityAPI2.Service.Storage.IStorageService;

import SecurityAPI2.Service.Email.EmailService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import SecurityAPI2.Dto.RegisterDto;
import SecurityAPI2.Model.Enum.Status;
import SecurityAPI2.Model.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final IUserRepository userRepository;
    private final ISkillRepository skillRepository;
    private final IEngineerRepository engineerRepository;
    private final BCryptPasswordEncoder encoder;
    private final IStorageService storageService;
    private final EmailService emailService;
    private final IRegistrationApprovalRepository registrationApprovalRepository;
    private final IRegistrationDisapprovalRepository registrationDisapprovalRepository;
    private final JwtUtils jwtUtils;

    private RegistrationDisapproval save(RegistrationDisapproval registrationDisapproval) {
        return registrationDisapprovalRepository.save(registrationDisapproval);
    }
    public boolean isRegistrationDisapprovedInNearPast(String email){
        return registrationDisapprovalRepository.FindInLast2Weeks(email,LocalDateTime.now().minus(2, ChronoUnit.WEEKS)).size() != 0;
    }
    private RegistrationApproval findRegistrationApprovalByHashedUuid(String hashedUuid){
        RegistrationApproval registrationApproval = registrationApprovalRepository.findByHashedUuid(hashedUuid);
        if(registrationApproval != null) return registrationApproval;
        throw new RegistrationApprovalNonExistingException();
    }
    private RegistrationApproval save(RegistrationApproval registrationApproval){
        return registrationApprovalRepository.save(registrationApproval);
    }
    private void delete(RegistrationApproval registrationApproval){
        registrationApprovalRepository.delete(registrationApproval);
    }
    
    public User findByEmail(final String email) {
        return userRepository.findByEmail(email);
    }

    public User register(RegisterDto registerDto) {
        if(!registerDto.getConfirmPassword().equals(registerDto.getPassword())) throw new InvalidConfirmPassword();
        if(isRegistrationDisapprovedInNearPast(registerDto.getEmail())) throw new RegistrationDisapprovedInNearPastException();
        
        User user = findByEmail(registerDto.getEmail());

        if(user != null) {
            throw new UserAlreadyExistsException();
        }

        user = initializeUser(registerDto);
        user = userRepository.save(user);
        if(user.hasRole(UserRole.ENGINEER)){
            Engineer engineer = new Engineer(user, registerDto.getSeniority());
            engineerRepository.save(engineer);
        }
        return user;
    }

    private User initializeUser(RegisterDto registerDto) {
        List<Role> roles = new ArrayList<Role>();
        List<String> dtoRoles = registerDto.getRoles();
        if(dtoRoles.isEmpty()) {
            throw new RolesEmptyException();
        }
        roles.add(new Role(dtoRoles.get(0)));

        User user = new User(
                registerDto.getEmail(), encoder.encode(registerDto.getPassword()),
                registerDto.getName(),
                registerDto.getSurname(), registerDto.getPhoneNumber(), registerDto.getAddress(), roles
        );
        user.setFirstLogged(user.hasRole(UserRole.ADMIN));
        user.setStatus(user.hasRole(UserRole.ADMIN) ? Status.ACTIVATED : Status.PENDING);
        return user;
    }
    
    public Page<User> findAll(int pageNumber, int pageSize) {
        return userRepository.findAllByStatus(PageRequest.of(pageSize, pageNumber), Status.ACTIVATED);
    }
    public void approve(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) throw new UserDoesntExistException();
        User user = optionalUser.get();
        user.setStatus(Status.APPROVED);
        UUID registerUUID = UUID.randomUUID();
        String registerToken = jwtUtils.generateRegisterToken(user.getEmail(),registerUUID);
        RegistrationApproval registrationApproval = new RegistrationApproval(String.valueOf(registerUUID.toString().hashCode()));
        save(registrationApproval);
        emailService.sendApprovedMail(user.getEmail(),registerToken);
    }
    public void activateAccount(String registerToken){
        Claims registerClaims;
        try{
            jwtUtils.validateRegisterToken(registerToken);
            registerClaims = jwtUtils.getClaimsFromRegisterToken(registerToken);
        }catch(final ExpiredJwtException e){
            throw new TokenExpiredException("Your account link expired.");
        }catch(final Exception e){
            throw new TokenInvalidException("Your account link invalid");
        }
        RegistrationApproval registrationApproval = findRegistrationApprovalByHashedUuid(
                String.valueOf(registerClaims.get("uuid").toString().hashCode()));
        User user = findByEmail(registerClaims.getSubject());
        if(user == null) throw new InvalidTokenClaimsException();

        if(user.hasRole(UserRole.ENGINEER)) {
            Engineer engineer = engineerRepository.findByUser(user);
            engineer.setHireDate(LocalDate.now());
            engineerRepository.save(engineer);
        }
        user.setStatus(Status.ACTIVATED);
        userRepository.save(user);
        delete(registrationApproval);
    }

    public void disapprove(Long id, String reason) {
        User user = userRepository.findById(id)
                .orElseThrow(UserDoesntExistException::new);

        save(new RegistrationDisapproval(0L,user.getEmail(),LocalDateTime.now()));
        emailService.sendDisapprovedMail(reason,user.getEmail());

        if(user.hasRole(UserRole.ENGINEER)) {
            Engineer engineer = engineerRepository.findByUser(user);
            engineerRepository.delete(engineer);
        }
        userRepository.delete(user);
    }

    public Page<User> findPendingUsers(int pageNumber, int pageSize) {
        return userRepository.findAllByStatus(PageRequest.of(pageSize, pageNumber),Status.PENDING);
    }
    public User update(final User newUser) {
        final User user = userRepository.findById(newUser.getId()).get();
        user.setName(newUser.getName());
        user.setSurname(newUser.getSurname());
        user.setPhoneNumber(newUser.getPhoneNumber());
        user.getAddress().setStreet(newUser.getAddress().getStreet());
        user.getAddress().setStreetNumber(newUser.getAddress().getStreetNumber());
        user.getAddress().setCity(newUser.getAddress().getCity());
        user.getAddress().setZipCode(newUser.getAddress().getZipCode());
        user.getAddress().setCountry(newUser.getAddress().getCountry());

        return userRepository.save(user);
    }

    public void changePassword(final User user, final PasswordChangeDto passwordChangeDto) {
        if (!encoder.matches(passwordChangeDto.getOldPassword(), user.getPassword())) {
            throw new IncorrectPassword();
        }
        if(!passwordChangeDto.getConfirmPassword().equals(passwordChangeDto.getNewPassword())){
            throw new InvalidConfirmPassword();
        }
        user.setPassword(encoder.encode(passwordChangeDto.getNewPassword()));
        user.setFirstLogged(false);
        userRepository.save(user);
    }

    public void addSkill(SkillDto skillDto, User user){
        Engineer engineer = engineerRepository.findByUser(user);
        ArrayList<Skill> skills = new ArrayList<>();
        for(Skill s : skillDto.getSkills()){
            if(skillRepository.findSkillByEngineerIdAndSkill(engineer.getId(), s.getSkill()) != null){
                throw new EngineerAlreadyHasSkillException();
            }
            skills.add(new Skill(s.getSkill(), s.getStrength(), engineer));
        }
        engineer.setSkills(skills);
        userRepository.save(user);
    }

    public List<Skill> getEngineerSkills(Long engineerId){
        return skillRepository.findAllByEngineerId(engineerId);
    }

    public void deleteEngineerSkill(Long skillId){
        skillRepository.deleteById(skillId);
    }

    public void updateSkill(EngineerSkillDto skillDto, User user){
        Engineer engineer = engineerRepository.findByUser(user);
        Skill skill = skillRepository.findSkillByEngineerIdAndSkill(engineer.getId(), skillDto.getSkill());
        skill.setStrength(skillDto.getStrength());
        skillRepository.save(skill);
    }

    public void uploadCv(MultipartFile file, User user) throws IOException {
        String url = storageService.uploadFile(file);
        Engineer engineer = engineerRepository.findByUser(user);
        engineer.setCvUrl(url);
        engineerRepository.save(engineer);
    }

    public Engineer getEngineer(User user){
        return engineerRepository.findByUser(user);
    }
}
