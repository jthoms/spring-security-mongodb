package com.sustia.service;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.Index.Duplicates;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.stereotype.Component;

import com.sustia.UserAccountStatus;
import com.sustia.domain.Role;
import com.sustia.domain.UserAccount;

@Component
@Profile(value="DEV")
public class DataInitializer {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Autowired
	private UserService userService;
	
	String demoPasswordEncoded = "2a97516c354b68848cdbd8f54a226a0a55b21ed138e207ad6c5cbb9c00aa5aea";
	
	@PostConstruct
	public void init() {
		logger.debug("initializing data");
		
		//clear all collections
		mongoTemplate.dropCollection("role");
		mongoTemplate.dropCollection("userAccount");
		mongoTemplate.indexOps(UserAccount.class).ensureIndex(new Index().on("username", Order.DESCENDING).unique(Duplicates.DROP));
		
		//establish roles
		mongoTemplate.insert(new Role("ROLE_USER"), "role");
		mongoTemplate.insert(new Role("ROLE_ADMIN"), "role");
		
		UserAccount user = new UserAccount();
		user.setFirstname("Bob");
		user.setLastname("Doe");
		user.setPassword(demoPasswordEncoded);
		user.addRole(userService.getRole("ROLE_USER"));
		user.setUsername("bob");		
		userService.create(user);
		//simulate account activation
		user.setEnabled(true);
		user.setStatus(UserAccountStatus.STATUS_APPROVED.name());		
		userService.save(user);
		
		user = new UserAccount();
		user.setFirstname("Jim");
		user.setLastname("Doe");
		user.setPassword(demoPasswordEncoded);
		user.addRole(userService.getRole("ROLE_ADMIN"));
		user.setUsername("jim");	
		userService.create(user);
		user.setEnabled(true);
		user.setStatus(UserAccountStatus.STATUS_APPROVED.name());
		userService.save(user);
		
		user = new UserAccount();
		user.setFirstname("Ted");
		user.setLastname("Doe");
		user.setPassword(demoPasswordEncoded);
		user.addRole(userService.getRole("ROLE_USER"));
		user.addRole(userService.getRole("ROLE_ADMIN"));
		user.setUsername("ted");	
		userService.create(user);
		user.setEnabled(true);
		user.setStatus(UserAccountStatus.STATUS_APPROVED.name());
		userService.save(user);
		
	}
}
