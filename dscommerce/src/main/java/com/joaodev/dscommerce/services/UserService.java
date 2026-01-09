package com.joaodev.dscommerce.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.joaodev.dscommerce.dto.UserDTO;
import com.joaodev.dscommerce.entities.Role;
import com.joaodev.dscommerce.entities.User;
import com.joaodev.dscommerce.projections.UserDetailsProjection;
import com.joaodev.dscommerce.repositories.UserRepository;
import com.joaodev.dscommerce.util.CustomUserUtil;

@Service
public class UserService implements UserDetailsService {

	@Autowired
	private UserRepository repository;

	@Autowired
	private CustomUserUtil customUserUtil;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		List<UserDetailsProjection> result = repository.searchUserAndRolesByEmail(username);
		if (result.size() == 0) {
			throw new UsernameNotFoundException("Email not found");
		}
		
		User user = new User();
		user.setEmail(result.get(0).getUsername());
		user.setPassword(result.get(0).getPassword());
		for (UserDetailsProjection projection : result) {
			user.addRole(new Role(projection.getRoleId(), projection.getAuthority()));
		}
		
		return user;
	}

	protected User autenticated(){

		try {
			String username = customUserUtil.getLoggedUserName();
		    return repository.findByEmail(username).get();

		} catch (Exception e) {
			throw new UsernameNotFoundException("Email not found"); 
		}
		
	}

	@Transactional(readOnly = true)
	public UserDTO getMe(){
		User user = autenticated();
		return new UserDTO(user);
	}
}
