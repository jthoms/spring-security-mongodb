package com.sustia.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.sustia.UserAccountStatus;
import com.sustia.domain.Role;
import com.sustia.domain.UserAccount;
import com.sustia.repository.RoleRepository;
import com.sustia.repository.UserAccountRepository;

@Service
public class UserService {

	@Autowired
	private UserAccountRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	public Role getRole(String role) {
		return roleRepository.findOne(role);
	}
	
	boolean create(UserAccount user) {
		Assert.isNull(user.getId());
//		user.setId(UUID.randomUUID().toString().replace("-", ""));
		// duplicate username
		if (userRepository.findByUsername(user.getUsername()) != null) {
			return false;
		}
		user.setEnabled(false);
		user.setStatus(UserAccountStatus.STATUS_DISABLED.name());
		userRepository.save(user);
		return true;
	}
	
	void save(UserAccount user) {
		Assert.notNull(user.getId());
		userRepository.save(user);
	}
	
	void delete(UserAccount user) {
		Assert.notNull(user.getId());
		userRepository.delete(user);
	}

	public UserAccount getByUsernameAndPassword(String username, String password) {
		List<UserAccount> users = userRepository.findByUsernameAndPassword(username, password);
		if (users.size() == 1) {
			return users.get(0);
		}
		return null;
	}
	
	public UserAccount getByUsername(String username) {
		return userRepository.findByUsername(username);
	}
	
}
