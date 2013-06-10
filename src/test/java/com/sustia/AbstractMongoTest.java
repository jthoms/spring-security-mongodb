package com.sustia;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.Index.Duplicates;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sustia.domain.Role;
import com.sustia.domain.UserAccount;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:META-INF/spring/ApplicationContext.xml")
public abstract class AbstractMongoTest {

	@Autowired
	private MongoTemplate mongoTemplate;
	
	protected Role userRole, adminRole, superAdminRole;
	
	//sha256 encoded, i.e. >echo -n test | sha256sum
	protected String testPasswordEncoded = "9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08";

	@Before
	public void setUp() {
		//clear all collections
		mongoTemplate.dropCollection("role");
		mongoTemplate.dropCollection("userAccount");
		//see UserAccount.username definition: @Indexed does not work, but indexOps does (sdm 1.3.0.M1)
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
		// long string id
		// String id = UUID.randomUUID().toString().replace("-", "") +
		// UUID.randomUUID().toString().replace("-", "");
		// this.jdoe.setId(id);
		jdoe.setFirstname("John");
		jdoe.setLastname("Doe");
		jdoe.setPassword(testPasswordEncoded);
		jdoe.addRole(adminRole);
		jdoe.addRole(userRole);
		jdoe.setUsername("jdoe");
		return jdoe;
	}
}
