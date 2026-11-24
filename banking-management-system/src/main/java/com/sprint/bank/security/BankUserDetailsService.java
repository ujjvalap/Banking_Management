package com.sprint.bank.security;

import java.util.Locale;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.sprint.bank.entity.AppUser;
import com.sprint.bank.repository.UserRepository;

@Service
public class BankUserDetailsService implements UserDetailsService {
	private final UserRepository userRepository;

	public BankUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		AppUser user = userRepository.findByEmail(email.trim().toLowerCase(Locale.ROOT))
				.orElseThrow(() -> new UsernameNotFoundException("User not found."));
		return User.withUsername(user.getEmail())
				.password(user.getPasswordHash())
				.roles(user.getRole().name())
				.build();
	}
}
