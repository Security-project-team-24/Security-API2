package SecurityAPI2.Service;

import SecurityAPI2.Crypto.SymetricKeyEncription;
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
import SecurityAPI2.Service.CVFile.ICVFileService;

import SecurityAPI2.Service.Email.EmailService;

import SecurityAPI2.utils.CryptoHelper;
import io.github.cdimascio.dotenv.Dotenv;
import SecurityAPI2.utils.CV.CVEncryption;
import SecurityAPI2.utils.CV.DocumentConverter;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import SecurityAPI2.Dto.RegisterDto;
import SecurityAPI2.Model.Enum.Status;
import SecurityAPI2.Model.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;

import javax.xml.bind.DatatypeConverter;
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
    private final EmailService emailService;
    private final IRegistrationApprovalRepository registrationApprovalRepository;
    private final IRegistrationDisapprovalRepository registrationDisapprovalRepository;
    private final JwtUtils jwtUtils;
    private final CVEncryption cvEncryption;
    private final ICVFileService cvFileService;
    private final DocumentConverter documentConverter;

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
        user = CryptoHelper.encryptUser(user);
        user.setFirstLogged(user.hasRole(UserRole.ADMIN));
        user.setStatus(user.hasRole(UserRole.ADMIN) ? Status.ACTIVATED : Status.PENDING);
        return user;
    }


    public Page<User> findAll(int pageNumber, int pageSize) {
        Page<User> users = userRepository.findAllByStatus(PageRequest.of(pageSize, pageNumber), Status.ACTIVATED);
        for(User user : users) {
           CryptoHelper.decryptUser(user);
        }
        return users;
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
        userRepository.save(user);
        emailService.sendApprovedMail(user.getEmail(),registerToken);
    }
    public void block(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) throw new UserDoesntExistException();
        User user = optionalUser.get();
        user.setBlocked(true);
        userRepository.save(user);
    }
    public void unblock(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) throw new UserDoesntExistException();
        User user = optionalUser.get();
        user.setBlocked(false);
        userRepository.save(user);
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
        Page<User> users = userRepository.findAllByStatus(PageRequest.of(pageSize, pageNumber),Status.PENDING);
        for(User user : users) {
            CryptoHelper.decryptUser(user);
        }
        return users;
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
        CryptoHelper.encryptUser(user);
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

    public void forgotPassword(String email) {
        User user = findByEmail(email);
        if(user == null) throw new UserDoesntExistException();
        String password = "A!" + UUID.randomUUID().toString().replace("-", "");
        emailService.sendForgotPasswordMail(password,user.getEmail());
        user.setPassword(encoder.encode(password));
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

    public void uploadCv(MultipartFile multipartFile, User user) {
        Engineer engineer = engineerRepository.findByUser(user);
        
        if(engineer.getCvName() != null) cvFileService.deleteDocument(engineer.getCvName());
        
        String CVName = UUID.randomUUID().toString().replace("-", "");
        engineer.setCvName(CVName);

        Document file = null;
        try {
            file = documentConverter.convertMultipartFileToDocument(multipartFile);
        } catch (Exception e) {
            throw new DocumentConversionException();
        }
        Document encryptedFile = cvEncryption.encrypt(file);
        cvFileService.saveDocument(encryptedFile,CVName);
        engineerRepository.save(engineer);
    }

    public MultipartFile findCVForEngineer(User user) {
        Engineer engineer = engineerRepository.findByUser(user);
        String CVName = engineer.getCvName();
        if(CVName == null) throw new CVDoesntExistsException("You haven't uploaded CV yet!");
        Document encryptedFile = cvFileService.loadDocument(CVName);
        Document file = cvEncryption.decrypt(encryptedFile);
        return documentConverter.convertDocumentToMultipartFile(file);
    }

    public MultipartFile findCVByName(String fileName) {
        Document encryptedFile = cvFileService.loadDocument(fileName);
        Document file = cvEncryption.decrypt(encryptedFile);
        return documentConverter.convertDocumentToMultipartFile(file);
    }

    public Engineer getEngineer(User user){
        return engineerRepository.findByUser(user);
    }

    public Page<Engineer> getEngineers(int pageNumber,
                                       String email,
                                       String name,
                                       String surname,
                                       LocalDate fromDate,
                                       LocalDate toDate) {
        Page<Engineer> page =  engineerRepository.findByUserEmailContainingIgnoreCaseAndUserNameContainingIgnoreCaseAndUserSurnameContainingIgnoreCaseAndHireDateBetween(email, name, surname, fromDate, toDate, PageRequest.of(pageNumber, 10));
        CryptoHelper.decryptEngineers(page.getContent());
        return page;
    }
}
