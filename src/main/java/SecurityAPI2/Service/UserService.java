package SecurityAPI2.Service;

import SecurityAPI2.Dto.PasswordChangeDto;
import SecurityAPI2.Dto.SkillDto;
import SecurityAPI2.Exceptions.IncorrectPassword;
import SecurityAPI2.Exceptions.TokenExceptions.InvalidTokenClaimsException;
import SecurityAPI2.Exceptions.TokenExceptions.TokenExpiredException;
import SecurityAPI2.Exceptions.TokenExceptions.TokenInvalidException;
import SecurityAPI2.Exceptions.UserDoesntExistException;
import SecurityAPI2.Exceptions.*;
import SecurityAPI2.Model.*;
import SecurityAPI2.Repository.*;

import SecurityAPI2.Security.JwtUtils;
import SecurityAPI2.Service.Storage.IStorageService;

import SecurityAPI2.Service.Email.EmailService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import SecurityAPI2.Dto.RegisterDto;
import SecurityAPI2.Model.Enum.Role;
import SecurityAPI2.Model.Enum.Status;
import SecurityAPI2.Model.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
        
        if(user == null){
            user = new User(registerDto.getEmail(), encoder.encode(registerDto.getPassword()), registerDto.getName(),
                    registerDto.getSurname(), registerDto.getPhoneNumber(), registerDto.getRole(), registerDto.getAddress());
            user.setFirstLogged(user.getRole() == Role.ADMIN);
            user.setStatus(Status.PENDING);
            return userRepository.save(user);
        } else if(user.getStatus() == Status.DISAPPROVED) {
            User updateUser = new User(registerDto.getEmail(), encoder.encode(registerDto.getPassword()), registerDto.getName(),
                    registerDto.getSurname(), registerDto.getPhoneNumber(), registerDto.getRole(), registerDto.getAddress());
            updateUser.setId(user.getId());
            updateUser.setFirstLogged(updateUser.getRole() == Role.ADMIN);
            updateUser.setStatus(Status.PENDING);
            return userRepository.save(updateUser);
        }
        throw new UserAlreadyExistsException();
    }
    
    public Page<User> findAll(int pageNumber, int pageSize) {
        return userRepository.findAll(PageRequest.of(pageSize, pageNumber));
    }
    public void approve(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) throw new UserDoesntExistException();
        User user = optionalUser.get();
        user.setStatus(Status.APPROVED);
        UUID registerUUID = UUID.randomUUID();
        String registerToken = jwtUtils.generateRegisterToken(user.getEmail(),registerUUID);
        RegistrationApproval registrationApproval = new RegistrationApproval(String.valueOf(registerUUID.toString().hashCode()));
        registrationApproval = save(registrationApproval);
        emailService.sendApprovedMail(user.getEmail(),registerToken);
    }
    public void activateAccount(String registerToken){
        try{
            jwtUtils.validateRegisterToken(registerToken);
        }catch(final ExpiredJwtException e){
            throw new TokenExpiredException("Your account link expired.");
        }catch(final Exception e){
            throw new TokenInvalidException("Your account link invalid");
        }
        Claims registerClaims = jwtUtils.getClaimsFromRegisterToken(registerToken);
        RegistrationApproval registrationApproval = findRegistrationApprovalByHashedUuid(
                String.valueOf(registerClaims.get("uuid").toString().hashCode()));
        User user = findByEmail(registerClaims.getSubject());
        if(user == null) throw new InvalidTokenClaimsException();
        user.setStatus(Status.ACTIVATED);
        if(user.getRole() == Role.ENGINEER){
            Engineer engineer = new Engineer(user);
            engineerRepository.save(engineer);
        }
        userRepository.save(user);
        delete(registrationApproval);
    }

    public void disapprove(Long id, String reason) {
        boolean doesUserExists = userRepository.findById(id).isPresent();
        if (!doesUserExists) throw new UserDoesntExistException();
        final User user = userRepository.findById(id).get();
        user.setStatus(Status.DISAPPROVED);
        userRepository.save(user);
        save(new RegistrationDisapproval(0L,user.getEmail(),LocalDateTime.now()));
        emailService.sendDisapprovedMail(reason,user.getEmail());
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
            skills.add(new Skill(s.getSkill(), s.getStrength(), engineer));
        }
        engineer.setSkills(skills);
        userRepository.save(user);
    }

    public void uploadCv(MultipartFile file, User user) throws IOException {
        String url = storageService.uploadFile(file);
        Engineer engineer = engineerRepository.findByUser(user);
        engineer.setCvUrl(url);
        engineerRepository.save(engineer);
    }
}
