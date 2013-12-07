package com.sustia.service;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sustia.domain.Role;
import com.sustia.domain.UserAccount;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:META-INF/spring/ApplicationContext.xml")
@ActiveProfiles("DEV")
public class DataInitializerTest {

	@Autowired private DataInitializer initializer;
	
	@Autowired private UserService service;

	@Test
	public void testInitializer() throws Exception {
		initializer.init();
		
		Role role = service.getRole("ROLE_USER");
		assertThat(role.getId(), is("ROLE_USER"));
		
		role = service.getRole("ROLE_ADMIN");
		assertThat(role.getId(), is("ROLE_ADMIN"));
		
		UserAccount user = service.getByUsername("bob");
		assertThat(user.getRolesCSV(), is("ROLE_USER"));
		
		user = service.getByUsername("jim");
		assertThat(user.getRolesCSV(), is("ROLE_ADMIN"));
		
		user = service.getByUsername("ted");
		assertThat(user.getRolesCSV(), is("ROLE_USER,ROLE_ADMIN"));

	}
}
