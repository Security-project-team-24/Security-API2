package SecurityAPI2.Security.UserDetails;

import SecurityAPI2.Repository.IUserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	@Autowired
	private IUserRepository userRepository;
	@Override
	public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
		final var user = userRepository.findByEmail(email);
		if (user == null) {
			throw new UsernameNotFoundException(email);
		}
		return UserDetailsImpl.build(user);
	}
}
