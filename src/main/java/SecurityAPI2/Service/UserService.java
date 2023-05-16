package SecurityAPI2.Service;

import SecurityAPI2.Dto.PasswordChangeDto;
import SecurityAPI2.Dto.SkillDto;
import SecurityAPI2.Exceptions.IncorrectPassword;
import SecurityAPI2.Exceptions.TokenExceptions.HmacTokenExpiredException;
import SecurityAPI2.Exceptions.UserDoesntExistException;
import SecurityAPI2.Exceptions.*;
import SecurityAPI2.Model.*;
import SecurityAPI2.Repository.IEngineerRepository;
import SecurityAPI2.Repository.ISkillRepository;
import SecurityAPI2.Repository.IUserRepository;

import SecurityAPI2.Service.Interfaces.IStorageService;

import SecurityAPI2.utils.Email.EmailSender;
import SecurityAPI2.utils.hmac.HmacGenerator;

import org.springframework.beans.factory.annotation.Autowired;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private ISkillRepository skillRepository;
    @Autowired
    private IEngineerRepository engineerRepository;
    @Autowired
    private BCryptPasswordEncoder encoder;
    @Autowired
    private IStorageService storageService;
    @Autowired
    private EmailSender emailSender;
    @Autowired
    private RegistrationDisapprovalService registrationDisapprovalService;
    @Autowired
    private RegistrationApprovalService registrationApprovalService;
    

    public User findByEmail(final String email) {
        return userRepository.findByEmail(email);
    }

    public User register(RegisterDto registerDto) {
        if(!registerDto.getConfirmPassword().equals(registerDto.getPassword())){
            throw new InvalidConfirmPassword();
        }
        if(registrationDisapprovalService.isRegistrationDisapprovedInNearPast(registerDto.getEmail())){
            throw new RegistrationDisapprovedInNearPastException();
        }
        User user = new User(registerDto.getEmail(), encoder.encode(registerDto.getPassword()), registerDto.getName(),
                registerDto.getSurname(), registerDto.getPhoneNumber(), registerDto.getRole(), registerDto.getAddress());
        
        user.setFirstLogged(user.getRole() == Role.ADMIN);
        user.setStatus(Status.PENDING);
        if(user.getRole() == Role.ENGINEER){
            engineerRepository.save(new Engineer(user));
        }
        return userRepository.save(user);
    }
    
    public Page<User> findAll(int pageNumber, int pageSize) {
        return userRepository.findAll(PageRequest.of(pageSize, pageNumber));
    }
    public void approve(Long id) {
        boolean doesUserExists = userRepository.findById(id).isPresent();
        if (!doesUserExists) throw new UserDoesntExistException();
        final User user = userRepository.findById(id).get();
        user.setStatus(Status.APPROVED);
        userRepository.save(user);
        RegistrationApproval registrationApproval = new RegistrationApproval(user);
        String hmacToken = HmacGenerator.generate(generateApprovalHMACString(registrationApproval));
        registrationApproval.setHMACHash(hmacToken);
        registrationApproval = registrationApprovalService.save(registrationApproval);
        emailSender.sendApprovedMail(user.getEmail(),hmacToken);
    }
    public void activateAccount(String hmacToken){
        RegistrationApproval registrationApproval = registrationApprovalService.findById(hmacToken);
        if(registrationApproval.getDate().plusDays(1).isBefore(LocalDateTime.now())) throw new HmacTokenExpiredException();
        User user = findByEmail(registrationApproval.getEmail());
        user.setStatus(Status.ACTIVATED);
        if(user.getRole() == Role.ENGINEER){
            Engineer engineer = engineerRepository.findByUser(user);
            engineer.setSeniority(LocalDate.now());
            engineerRepository.save(engineer);
        }
        userRepository.save(user);
        registrationApprovalService.delete(registrationApproval);
    }
    private String generateApprovalHMACString(RegistrationApproval registrationApproval){
        return  registrationApproval.getEmail() + "|" + registrationApproval.getPhoneNumber()+ "|" + registrationApproval.getName() 
                + "|" + registrationApproval.getSurname() + "|" + registrationApproval.getRole();
    }

    public void disapprove(Long id, String reason) {
        boolean doesUserExists = userRepository.findById(id).isPresent();
        if (!doesUserExists) throw new UserDoesntExistException();
        final User user = userRepository.findById(id).get();
        user.setStatus(Status.DISAPPROVED);
        userRepository.save(user);
        registrationDisapprovalService.Create(new RegistrationDisapproval(0L,user.getEmail(),LocalDateTime.now()));
        emailSender.sendDisapprovedMail(reason,user.getEmail());
    }

    public List<User> findPendingUsers() {
        return userRepository.findAllByStatus(Status.PENDING);
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
