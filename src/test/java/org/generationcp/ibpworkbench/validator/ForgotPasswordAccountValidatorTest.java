
package org.generationcp.ibpworkbench.validator;

import org.generationcp.ibpworkbench.model.UserAccountModel;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.Errors;

@RunWith(MockitoJUnitRunner.class)
public class ForgotPasswordAccountValidatorTest {

	@Mock
	private Errors errors;

	@Mock
	private WorkbenchDataManager workbenchDataManager;

	@InjectMocks
	private ForgotPasswordAccountValidator validator;

	@Test
	public void testValidate() throws Exception {
		ForgotPasswordAccountValidator validatorDUT = Mockito.spy(this.validator);

		UserAccountModel userAccount = new UserAccountModel();
		userAccount.setFirstName("firstName");
		userAccount.setLastName("lastName");
		userAccount.setEmail("email@email.com");
		userAccount.setRole("ADMIN");
		userAccount.setUsername("username");
		userAccount.setPassword("password");
		userAccount.setPasswordConfirmation("password");

		Mockito.when(this.workbenchDataManager.isUsernameExists(userAccount.getUsername())).thenReturn(false);
		Mockito.when(this.workbenchDataManager.isPersonExists(userAccount.getFirstName(), userAccount.getLastName())).thenReturn(false);

		validatorDUT.validate(userAccount, this.errors);

		Mockito.verify(validatorDUT).validatePasswordConfirmationIfEquals(this.errors, userAccount);

		Mockito.verify(validatorDUT).validateUsernameAndEmailIfNotExists(this.errors, userAccount);

	}

	@Test
	public void testValidateUsernameAndEmailIfNotExists() throws Exception {
		ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> arg2 = ArgumentCaptor.forClass(String.class);

		UserAccountModel userAccount = new UserAccountModel();
		userAccount.setUsername("username");

		Mockito.when(this.workbenchDataManager.isPersonWithUsernameAndEmailExists(userAccount.getUsername(), userAccount.getEmail()))
				.thenReturn(false);

		ForgotPasswordAccountValidator validatorDUT = Mockito.spy(this.validator);
		validatorDUT.validateUsernameAndEmailIfNotExists(this.errors, userAccount);

		Mockito.verify(this.errors).rejectValue(arg1.capture(), arg2.capture(), Matchers.any(String[].class), Matchers.anyString());
		Assert.assertEquals("error should output username field", UserAccountFields.USERNAME, arg1.getValue());
		Assert.assertEquals("show correct error code", ForgotPasswordAccountValidator.SIGNUP_FIELD_USERNAME_EMAIL_COMBO_NOT_EXISTS,
				arg2.getValue());
	}

	@Test
	public void testValidateUsernameAndEmailIfNotExistsDatabaseError() throws Exception {
		ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> arg2 = ArgumentCaptor.forClass(String.class);

		UserAccountModel userAccount = new UserAccountModel();
		userAccount.setUsername("username");

		Mockito.when(this.workbenchDataManager.isPersonWithUsernameAndEmailExists(userAccount.getUsername(), userAccount.getEmail()))
				.thenThrow(MiddlewareQueryException.class);

		ForgotPasswordAccountValidator validatorDUT = Mockito.spy(this.validator);
		validatorDUT.validateUsernameAndEmailIfNotExists(this.errors, userAccount);

		Mockito.verify(this.errors).rejectValue(arg1.capture(), arg2.capture());
		Assert.assertEquals("error should output username field", UserAccountFields.USERNAME, arg1.getValue());
		Assert.assertEquals("show correct error code", UserAccountValidator.DATABASE_ERROR, arg2.getValue());
	}

	@Test
	public void testValidateUsernameAndEmailIfNotExistsNoError() throws Exception {
		UserAccountModel userAccount = new UserAccountModel();
		userAccount.setUsername("username");

		Mockito.when(this.workbenchDataManager.isPersonWithUsernameAndEmailExists(userAccount.getUsername(), userAccount.getEmail()))
				.thenReturn(true);

		ForgotPasswordAccountValidator validatorDUT = Mockito.spy(this.validator);
		validatorDUT.validateUsernameAndEmailIfNotExists(this.errors, userAccount);

		Mockito.verify(this.errors, Mockito.never()).rejectValue(Matchers.anyString(), Matchers.anyString());
	}
}
