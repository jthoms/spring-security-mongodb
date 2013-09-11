package com.sustia;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.Index.Duplicates;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sustia.domain.Role;
import com.sustia.domain.UserAccount;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:META-INF/spring/ApplicationContext.xml")
public abstract class AbstractMongoTest {

	@Autowired private MongoTemplate mongoTemplate;
	
    @Autowired protected PasswordEncoder encoder;
    
	protected Role userRole, adminRole, superAdminRole;
	
	protected String testPasswordEncoded;

	@Before
	public void setUp() {
		//clear all collections
		mongoTemplate.dropCollection("role");
		mongoTemplate.dropCollection("userAccount");
		testPasswordEncoded = encoder.encode("test");
		//see UserAccount.username definition: @Indexed does not work, but indexOps does
		//see RepositoryTest.userDuplicate()
		mongoTemplate.indexOps(UserAccount.class).ensureIndex(new Index().on("username", Direction.DESC).unique(Duplicates.DROP));
		
		//establish roles
		userRole = new Role();
		userRole.setId("ROLE_USER");
		mongoTemplate.insert(userRole, "role");
		
		adminRole = new Role();
		adminRole.setId("ROLE_ADMIN");
		mongoTemplate.insert(adminRole, "role");

		superAdminRole = new Role();
		superAdminRole.setId("ROLE_SUPERADMIN");
		mongoTemplate.insert(superAdminRole, "role");
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
