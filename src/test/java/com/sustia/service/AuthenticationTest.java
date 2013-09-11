package com.sustia.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.sustia.AbstractMongoTest;
import com.sustia.UserAccountStatus;
import com.sustia.domain.UserAccount;

public class AuthenticationTest extends AbstractMongoTest {

	@Autowired private LocalAuthenticationProvider localAuthenticationProvider;
	
	@Autowired private UserService userService;
	
	private UserAccount jdoe;
	
	@Before
	public void setUp() {
		super.setUp();
		this.jdoe = super.buildJdoe();
		
		assertThat(this.jdoe.getId(), is(nullValue()));
		boolean succeeded = userService.create(this.jdoe);
		assertThat(succeeded, is(true));
		assertThat(this.jdoe.getId(), is(notNullValue()));
		this.jdoe.setEnabled(true);
		this.jdoe.setStatus(UserAccountStatus.STATUS_APPROVED.name());
		userService.save(this.jdoe);
	}

	@Test
	public void validAuthentication() throws Exception {
		assertThat(localAuthenticationProvider, is(notNullValue()));
		assertThat(encoder, is(notNullValue()));
		assertThat(encoder.matches("test", super.testPasswordEncoded), is(true));

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("jdoe", "test");

        UserDetails user = localAuthenticationProvider.retrieveUser("jdoe", token);
        assertThat(user, is(notNullValue()));
        assertThat(user.getAuthorities().size(), is(2));
	}
	
	@Test(expected = BadCredentialsException.class)
    public void invalidAuthenticationPassword() {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("jdoe", "tes");
        UserDetails user = localAuthenticationProvider.retrieveUser("jdoe", token);
        assertThat(user, is(nullValue()));
	}
	
	@Test(expected = UsernameNotFoundException.class)
    public void invalidAuthenticationUsername() {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("jdo", "test");
        UserDetails user = localAuthenticationProvider.retrieveUser("jdo", token);
        assertThat(user, is(nullValue()));
	}
	
	@Test(expected = BadCredentialsException.class)
	public void invalidAuthenticationStatus() throws Exception {		
        
        this.jdoe.setStatus(UserAccountStatus.STATUS_PENDING.name());
        userService.save(this.jdoe);
        
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("jdoe", "test");
        UserDetails user = localAuthenticationProvider.retrieveUser("jdoe", token);
        assertThat(user, is(nullValue()));
	}
	
	@Test(expected = BadCredentialsException.class)
	public void invalidAuthenticationEnabled() throws Exception {		
        
        this.jdoe.setEnabled(false);
        userService.save(this.jdoe);
        
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("jdoe", "test");
        UserDetails user = localAuthenticationProvider.retrieveUser("jdoe", token);
        assertThat(user, is(nullValue()));
	}

}
