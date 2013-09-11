package com.sustia.service;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sustia.AbstractMongoTest;
import com.sustia.domain.Role;
import com.sustia.domain.UserAccount;

public class UserServiceTest extends AbstractMongoTest {

	@Autowired private UserService service;
	
	@Before
	public void setUp() {
		super.setUp();
	}

	@Test
	public void testRoles() throws Exception {
		Role role = service.getRole("ROLE_USER");
		assertThat(role, is(super.userRole));
		
		role = service.getRole("ROLE_ADMIN");
		assertThat(role, is(super.adminRole));
		
		role = service.getRole("ROLE_SUPERADMIN");
		assertThat(role, is(super.superAdminRole));
	}
	
	@Test
	public void createUser() throws Exception {
		UserAccount jdoe = super.buildJdoe();
		assertThat(jdoe.getId(), is(nullValue()));
		boolean succeeded = service.create(jdoe);
		assertThat(succeeded, is(true));
		assertThat(jdoe.getId(), is(notNullValue()));
		
		// duplicate username blocked by create
		UserAccount jdoe2 = super.buildJdoe();
		assertThat(jdoe2.getId(), is(nullValue()));
		succeeded = service.create(jdoe2);
		assertThat(succeeded, is(false));
		assertThat(jdoe2.getId(), is(nullValue()));
		
		service.delete(jdoe);
		UserAccount user = service.getByUsername("jdoe");
		assertThat(user, is(nullValue()));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void saveUser() throws Exception {
		UserAccount jdoe = super.buildJdoe();
		assertThat(jdoe.getId(), is(nullValue()));
		boolean succeeded = service.create(jdoe);
		assertThat(succeeded, is(true));
		assertThat(jdoe.getId(), is(notNullValue()));
		
		UserAccount user = service.getByUsername("jdoe");
		assertThat(user, is(jdoe));
		assertThat(user.getEnabled(), is(false));
		user.setEnabled(true);
		service.save(user);
		user = service.getByUsername("jdoe");
		assertThat(user.getEnabled(), is(true));
		
		UserAccount jdoe2 = super.buildJdoe();
		assertThat(jdoe2.getId(), is(nullValue()));
		
		//null id blocked by save
		service.save(jdoe2);
	}

}
