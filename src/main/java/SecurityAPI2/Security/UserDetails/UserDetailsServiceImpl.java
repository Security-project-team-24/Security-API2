package SecurityAPI2.Security.UserDetails;

import SecurityAPI2.Exceptions.UserBlockedException;
import SecurityAPI2.Repository.IUserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	private final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
	@Autowired
	private IUserRepository userRepository;
	@Override
	public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
		final var user = userRepository.findByEmail(email);
		if (user == null) {
			throw new UsernameNotFoundException(email);
		}
		if (user.isBlocked()) {
			throw new UserBlockedException();
		}
		return UserDetailsImpl.build(user);
	}

}
