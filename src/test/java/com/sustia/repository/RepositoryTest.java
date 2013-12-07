package com.sustia.repository;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sustia.AbstractMongoTest;
import com.sustia.domain.Role;
import com.sustia.domain.UserAccount;

public class RepositoryTest extends AbstractMongoTest {
	
	@Autowired private UserAccountRepository userRepository;

	@Autowired private RoleRepository roleRepository;
	
	private UserAccount jdoe;
	private String id;
	
	@Before
	public void setUp() {
		super.setUp();
		this.jdoe = super.buildJdoe();

		userRepository.save(this.jdoe);
		this.id = this.jdoe.getId();
	}
	
	@Test
	public void rolesCount() throws Exception {
		List<Role> roles = roleRepository.findAll();
		assertThat(roles.size(), is(3));
	}
	
	@Test
	public void rolesDuplicate() throws Exception {
		List<Role> roles = roleRepository.findAll();
		assertThat(roles.size(), is(3));
		
		//attempt to add duplicate
		Role role = new Role();
		role.setId(super.userRole.getId());
		roleRepository.save(role);
		
		roles = roleRepository.findAll();
		assertThat(roles.size(), is(3));
	}

	@Test
	public void user() throws Exception {	
		List<UserAccount> users = userRepository.findAll();
		assertThat(users.size(), is(1));
		
		UserAccount user = userRepository.findByUsername("jdoe");
		assertThat(user, is(user));
		
		user = userRepository.findOne(this.id);
		assertThat(user, is(user));
	}
	
	@Test
	public void userRoles() throws Exception {
		UserAccount user = userRepository.findByUsername("jdoe");
		assertThat(user.getRoles().size(), is(2));
		assertThat(user.getRolesCSV(), is("ROLE_ADMIN,ROLE_USER"));
		
		//remove 1 role
		user.removeRole(super.adminRole);
		assertThat(user.getRoles().size(), is(1));
		userRepository.save(user);
		assertThat(user.getRoles().size(), is(1));
		assertThat(user.getRoles().get(0), is(super.userRole));
		assertThat(user.getRolesCSV(), is("ROLE_USER"));

		//remove role already removed
		user.removeRole(super.adminRole);
		userRepository.save(user);
		assertThat(user.getRoles().size(), is(1));
		assertThat(user.getRolesCSV(), is("ROLE_USER"));
		
		//remove other role
		user.removeRole(super.userRole);
		userRepository.save(user);
		assertThat(user.getRoles().size(), is(0));
		assertThat(user.getRolesCSV(), is(""));
	}
	
	@Test
	public void userDuplicate() throws Exception {
		UserAccount jdoe2 = new UserAccount();
		jdoe2.setUsername("jdoe");
		userRepository.save(jdoe2);

		List<UserAccount> users = userRepository.findAll();
		assertThat(users.size(), is(1));
		UserAccount user = userRepository.findByUsername("jdoe");
		assertThat(user, is(this.jdoe));
	}
	
	@Test
	public void userFind() throws Exception {	
		List<UserAccount> users = userRepository.findByUsernameLike("jd*");
		assertThat(users.size(), is(1));
		users = userRepository.findByUsernameLike("*jd*");
		assertThat(users.size(), is(1));
		users = userRepository.findByUsernameLike("*jd*");
		assertThat(users.size(), is(1));
		users = userRepository.findByUsernameLike("*jdx*");
		assertThat(users.size(), is(0));
		
		users = userRepository.findByUsernameAndPassword("jdoe", super.testPasswordEncoded);
		assertThat(users.get(0), is(this.jdoe));
		
		users = userRepository.findByUsernameAndPassword("jdoe", "toast");
		assertThat(users.size(), is(0));	
		
		users = userRepository.findByUsernameAndPasswordQuery("jd", super.testPasswordEncoded);
		assertThat(users.size(), is(0));
		users = userRepository.findByPasswordAndUsername(super.testPasswordEncoded, "jd");
		assertThat(users.size(), is(0));

		users = userRepository.findByUsernameAndPassword("jd", super.testPasswordEncoded);
		assertThat(users.size(), is(0));
		
		UserAccount user = userRepository.findByUsername("jdoe");
		assertThat(user, is(this.jdoe));
		user = userRepository.findByUsername("jdo");
		assertThat(user, is(nullValue()));
		
		user = userRepository.findOne(this.id);
		assertThat(user, is(user));
		
		user = userRepository.findByUsername("jdo");
		assertThat(user, is(nullValue()));
		
	}

	@Test
	public void userDelete() throws Exception {	
		List<UserAccount> users = userRepository.findAll();
		assertThat(users.size(), is(1));
		
		userRepository.delete(id);
		UserAccount user = userRepository.findOne(id);
		assertThat(user, is(nullValue()));
		
		users = userRepository.findAll();
		assertThat(users.size(), is(0));
	}

}
