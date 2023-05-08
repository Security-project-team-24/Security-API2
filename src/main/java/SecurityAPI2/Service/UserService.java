package SecurityAPI2.Service;

import SecurityAPI2.Dto.PasswordChangeDto;
import SecurityAPI2.Dto.UserDto;
import SecurityAPI2.Exceptions.IncorrectPassword;
import SecurityAPI2.Model.User;
import SecurityAPI2.Repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import SecurityAPI2.Dto.RegisterDto;
import SecurityAPI2.Exceptions.InvalidConfirmPassword;
import SecurityAPI2.Model.Address;
import SecurityAPI2.Model.Enum.Role;
import SecurityAPI2.Model.Enum.Status;
import SecurityAPI2.Model.User;
import SecurityAPI2.Repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder encoder;
    public User findByEmail(final String email) {
        return userRepository.findByEmail(email);
    }

    public RegisterDto register(RegisterDto registerDto) {
        if(!registerDto.getConfirmPassword().equals(registerDto.getPassword())){
            throw new InvalidConfirmPassword();
        }
        User user = new User(registerDto.getEmail(), encoder.encode(registerDto.getPassword()), registerDto.getName(), registerDto.getSurname(),
                registerDto.getPhoneNumber(), registerDto.getRole(), registerDto.getAddress());
        if (user.getRole() == Role.ADMIN)
            user.setFirstLogged(true);
        else
            user.setFirstLogged(false);
        user.setStatus(Status.PENDING);
        userRepository.save(user);
        return registerDto;
    }

    public List<User> findAll() {
       return userRepository.findAll();
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
}
