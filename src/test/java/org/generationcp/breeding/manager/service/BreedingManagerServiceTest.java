
package org.generationcp.breeding.manager.service;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.breeding.manager.application.Message;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.service.api.user.UserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Created by IntelliJ IDEA. User: Daniel Villafuerte Date: 9/26/2014 Time: 2:09 PM
 */

@RunWith(MockitoJUnitRunner.class)
public class BreedingManagerServiceTest {

	private static final Integer DUMMY_USER_ID = 1;
	private static final Integer DUMMY_PERSON_ID = 1;
	private static final String DUMMY_PROGRAM_UUID = "a7433c01-4f46-4bc8-ae3a-678f0b62ac23";
	private static final String SAMPLE_SEARCH_STRING = "a sample search string";
	private static final String CONTAINS_SEARCH_STRING = "%a_s_%string";
	private static final Operation CONTAINS_MATCH = Operation.LIKE;
	@Mock
	private GermplasmDataManager germplasmDataManager;

	@Mock
	private GermplasmListManager germplasmListManager;

	@Mock
	private WorkbenchDataManager workbenchDataManager;

	@Mock
	private UserService userService;

	@Mock
	private ContextUtil contextUtil;

	@InjectMocks
	private BreedingManagerServiceImpl breedingManagerService;

	@Before
	public void setUp() {
		Mockito.when(this.contextUtil.getCurrentProgramUUID()).thenReturn(DUMMY_PROGRAM_UUID);
	}

	@Test
	public void testDoGermplasmListSearchStartsWith() throws Exception {
		final List<GermplasmList> expectedResult = new ArrayList<>();
		expectedResult.add(new GermplasmList());

		Mockito.when(
				this.germplasmListManager.searchForGermplasmList(BreedingManagerServiceTest.SAMPLE_SEARCH_STRING,
						BreedingManagerServiceTest.DUMMY_PROGRAM_UUID, BreedingManagerServiceTest.CONTAINS_MATCH)).thenReturn(
				expectedResult);

		// assume we have a search result
		final List<GermplasmList> result =
				this.breedingManagerService.doGermplasmListSearch(BreedingManagerServiceTest.SAMPLE_SEARCH_STRING,
						BreedingManagerServiceTest.CONTAINS_MATCH);

		Mockito.verify(this.germplasmListManager).searchForGermplasmList(BreedingManagerServiceTest.SAMPLE_SEARCH_STRING,
				BreedingManagerServiceTest.DUMMY_PROGRAM_UUID, BreedingManagerServiceTest.CONTAINS_MATCH);

		Assert.assertTrue("expects the result size is equal to the expectedResult size", result.size() == expectedResult.size());
	}

	@Test(expected = BreedingManagerSearchException.class)
	public void testDoGermplasmListSearchContainsUnderADifferentProgram() throws Exception {
		final List<GermplasmList> expectedResult = new ArrayList<>();
		expectedResult.add(new GermplasmList());

		this.breedingManagerService.doGermplasmListSearch(BreedingManagerServiceTest.CONTAINS_SEARCH_STRING,
				BreedingManagerServiceTest.CONTAINS_MATCH);
	}

	@Test
	public void testDoGermplasmListSearchEqual() throws Exception {
		final List<GermplasmList> expectedResult = new ArrayList<>();
		expectedResult.add(new GermplasmList());

		Mockito.when(
				this.germplasmListManager.searchForGermplasmList(BreedingManagerServiceTest.SAMPLE_SEARCH_STRING,
						BreedingManagerServiceTest.DUMMY_PROGRAM_UUID, Operation.LIKE)).thenReturn(expectedResult);

		// assume we have a search result
		final List<GermplasmList> result =
				this.breedingManagerService.doGermplasmListSearch(BreedingManagerServiceTest.SAMPLE_SEARCH_STRING, Operation.LIKE);

		Mockito.verify(this.germplasmListManager).searchForGermplasmList(BreedingManagerServiceTest.SAMPLE_SEARCH_STRING,
				BreedingManagerServiceTest.DUMMY_PROGRAM_UUID, Operation.LIKE);

		Assert.assertTrue("expects the result size is equal to the expectedResult size", result.size() == expectedResult.size());
	}

	@Test(expected = BreedingManagerSearchException.class)
	public void testDoGermplasmListSearchEqualUnderADifferentProgram() throws Exception {
		final List<GermplasmList> expectedResult = new ArrayList<>();
		expectedResult.add(new GermplasmList());

		this.breedingManagerService.doGermplasmListSearch(BreedingManagerServiceTest.SAMPLE_SEARCH_STRING,
				BreedingManagerServiceTest.CONTAINS_MATCH);
	}

	@Test
	public void testDoGermplasmListSearchContains() throws Exception {
		final List<GermplasmList> expectedResult = new ArrayList<>();
		expectedResult.add(new GermplasmList());

		Mockito.when(
				this.germplasmListManager.searchForGermplasmList(BreedingManagerServiceTest.SAMPLE_SEARCH_STRING,
						BreedingManagerServiceTest.DUMMY_PROGRAM_UUID, BreedingManagerServiceTest.CONTAINS_MATCH)).thenReturn(
				expectedResult);

		// assume we have a search result
		final List<GermplasmList> result =
				this.breedingManagerService.doGermplasmListSearch(BreedingManagerServiceTest.SAMPLE_SEARCH_STRING,
						BreedingManagerServiceTest.CONTAINS_MATCH);

		Mockito.verify(this.germplasmListManager).searchForGermplasmList(BreedingManagerServiceTest.SAMPLE_SEARCH_STRING,
				BreedingManagerServiceTest.DUMMY_PROGRAM_UUID, BreedingManagerServiceTest.CONTAINS_MATCH);

		Assert.assertTrue("expects the result size is equal to the expectedResult size", result.size() == expectedResult.size());
	}

	@Test(expected = BreedingManagerSearchException.class)
	public void testDoGermplasmListSearchStartsWithUnderADifferentProgram() throws Exception {
		final List<GermplasmList> expectedResult = new ArrayList<>();
		expectedResult.add(new GermplasmList());

		this.breedingManagerService.doGermplasmListSearch(BreedingManagerServiceTest.SAMPLE_SEARCH_STRING,
				BreedingManagerServiceTest.CONTAINS_MATCH);
	}

	@Test
	public void testDoGermplasmListSearchEmptyString() throws Exception {

		try {
			this.breedingManagerService.doGermplasmListSearch("", BreedingManagerServiceTest.CONTAINS_MATCH);
			Assert.fail("expects an error since germplasm search string is empty");
		} catch (final BreedingManagerSearchException e) {
			Assert.assertEquals("Should throw a BreedingManagerSearchException with SEARCH_QUERY_CANNOT_BE_EMPTY message",
					e.getErrorMessage(), Message.SEARCH_QUERY_CANNOT_BE_EMPTY);
			Mockito.verifyZeroInteractions(this.germplasmListManager); // germplasmListManager should not be called
		}
	}

	@Test
	public void testValidateEmptySearchString() throws Exception {
		try {
			this.breedingManagerService.validateEmptySearchString("");
			Assert.fail("expects a BreedingManagerSearchException to be thrown");
		} catch (final BreedingManagerSearchException e) {
			Assert.assertEquals("Should throw a BreedingManagerSearchException with SEARCH_QUERY_CANNOT_BE_EMPTY message",
					e.getErrorMessage(), Message.SEARCH_QUERY_CANNOT_BE_EMPTY);
		}
	}

	protected Person createDummyPerson() {
		final String firstName = "FIRST NAME";
		final String middleName = "MIDDLE NAME";
		final String lastName = "LAST NAME";

		return new Person(firstName, middleName, lastName);
	}

}
