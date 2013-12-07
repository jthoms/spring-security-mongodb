package com.sustia;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sustia.domain.Role;
import com.sustia.domain.UserAccount;
import com.sustia.service.DbService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:META-INF/spring/ApplicationContext.xml")
public abstract class AbstractMongoTest {

	@Autowired private MongoOperations operations;
	
    @Autowired protected PasswordEncoder encoder;
    
    @Autowired protected DbService dbService;
    
	protected Role userRole, adminRole, superAdminRole;
	
	protected String testPasswordEncoded;

	@Before
	public void setUp() {
		//clear all collections, but leave indexes intact
		dbService.cleanUp();
		testPasswordEncoded = encoder.encode("test");
		
//	alternative to @Indexed in UserAccount.class 
//		operations.indexOps(UserAccount.class).ensureIndex(new Index().on("username", Direction.DESC).unique(Duplicates.DROP));
		
		//establish roles
		userRole = new Role();
		userRole.setId("ROLE_USER");
		operations.insert(userRole, "role");
		
		adminRole = new Role();
		adminRole.setId("ROLE_ADMIN");
		operations.insert(adminRole, "role");

		superAdminRole = new Role();
		superAdminRole.setId("ROLE_SUPERADMIN");
		operations.insert(superAdminRole, "role");
	}

	protected UserAccount buildJdoe() {
		UserAccount jdoe = new UserAccount();
		jdoe.setFirstname("John");
		jdoe.setLastname("Doe");
		jdoe.setPassword(testPasswordEncoded);
		jdoe.addRole(adminRole);
		jdoe.addRole(userRole);
		jdoe.setUsername("jdoe");
		return jdoe;
	}
}
