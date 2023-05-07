package SecurityAPI2.Service;

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

@Service
public class UserService {
    @Autowired
    private BCryptPasswordEncoder encoder;
    @Autowired
    IUserRepository repository;
    public RegisterDto register(RegisterDto registerDto) {
        if(!registerDto.getConfirmPassword().equals(registerDto.getPassword())){
            throw new InvalidConfirmPassword();
        }
        User user = new User(registerDto.getEmail(), encoder.encode(registerDto.getPassword()), registerDto.getName(), registerDto.getSurname(),
                registerDto.getPhoneNumber(), registerDto.getRole(), registerDto.getAddress());
        user.setStatus(Status.PENDING);
        repository.save(user);
        return registerDto;
    }
}
