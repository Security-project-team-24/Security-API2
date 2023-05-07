package SecurityAPI2.Service;

import SecurityAPI2.Model.User;
import SecurityAPI2.Repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private IUserRepository userRepository;
    public User findByEmail(final String email) {
        return userRepository.findByEmail(email);
    }
}
