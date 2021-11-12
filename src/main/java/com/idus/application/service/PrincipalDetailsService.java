package com.idus.application.service;

import com.idus.domain.user.entity.User;
import com.idus.domain.user.service.UserService;
import com.idus.application.security.dto.PrincipalDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class PrincipalDetailsService implements UserDetailsService {

	@Autowired
	private UserService userService;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userService.getByEmail(email);
		if(user == null) {
			return null;
		}else {
			return new PrincipalDetails(user);
		}
	}

}
