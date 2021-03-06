
package org.generationcp.breeding.manager.util;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.dellroad.stuff.vaadin.ContextApplication;
import org.generationcp.breeding.manager.application.Message;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.dms.ProgramFavorite;
import org.generationcp.middleware.pojos.dms.ProgramFavorite.FavoriteType;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Window;

public class BreedingManagerUtil {

	private static final Logger LOG = LoggerFactory.getLogger(BreedingManagerUtil.class);
	public static final String[] USER_DEF_FIELD_CROSS_NAME = {"CROSS NAME", "CROSSING NAME"};

	private BreedingManagerUtil() {

	}

	/**
	 * Get the id for UserDefinedField of Germplasm Name type for Crossing Name (matches upper case of UserDefinedField either fCode or
	 * fName). Query is: <b> SELECT fldno FROM udflds WHERE UPPER(fname) IN ('CROSSING NAME', 'CROSS NAME') OR UPPER(fcode) IN ('CROSSING
	 * NAME', 'CROSS NAME'); </b>
	 * 
	 * @param germplasmListManager
	 * @return @
	 */
	public static Integer getIDForUserDefinedFieldCrossingName(final GermplasmListManager germplasmListManager) {

		final List<UserDefinedField> nameTypes = germplasmListManager.getGermplasmNameTypes();
		for (final UserDefinedField type : nameTypes) {
			for (final String crossNameValue : BreedingManagerUtil.USER_DEF_FIELD_CROSS_NAME) {
				if (crossNameValue.equalsIgnoreCase(type.getFcode()) || crossNameValue.equalsIgnoreCase(type.getFname())) {
					return type.getFldno();
				}
			}
		}

		return null;
	}

	/**
	 * Get the id for UserDefinedField of Germplasm Name type for Crossing Name (matches upper case of UserDefinedField either fCode or
	 * fName). Query is: <b> SELECT fldno FROM udflds WHERE UPPER(fname) IN ('CROSSING NAME', 'CROSS NAME') OR UPPER(fcode) IN ('CROSSING
	 * NAME', 'CROSS NAME'); </b> If any error occurs, shows error message in passed in Window instance
	 * 
	 * @param germplasmListManager - instance of GermplasmListManager
	 * @param window - window where error message will be shown
	 * @param messageSource - resource bundle where the error message will be retrieved from
	 * @return
	 */
	public static Integer getIDForUserDefinedFieldCrossingName(final GermplasmListManager germplasmListManager, final Window window,
			final SimpleResourceBundleMessageSource messageSource) {

		try {

			return BreedingManagerUtil.getIDForUserDefinedFieldCrossingName(germplasmListManager);

		} catch (final MiddlewareQueryException e) {
			BreedingManagerUtil.LOG.error(e.getMessage(), e);
			if (window != null && messageSource != null) {
				MessageNotifier.showError(window, messageSource.getMessage(Message.ERROR_DATABASE),
						messageSource.getMessage(Message.ERROR_IN_GETTING_CROSSING_NAME_TYPE));
			}
		}

		return null;
	}

	/**
	 * Displays an error in window if field's value is null: "<fieldName> must be specified."
	 * 
	 * @param window - window where error message will be shown
	 * @param field - field to check if null. If null, displays warning.
	 * @param messageSource - resource bundle where the error message will be retrieved from
	 * @param fieldName - name of the required Field which will appear in error message. If this is null, gets the field's caption as field
	 *        name.
	 * @return false if field is null. Else, return true;
	 */
	public static boolean validateRequiredField(final Window window, final AbstractField field, final SimpleResourceBundleMessageSource messageSource,
			final String fieldName) {
		// either the field caption or fieldName param must be available
		assert field.getCaption() != null || fieldName != null;

		if (window != null && field.getValue() == null) {
			BreedingManagerUtil.showFieldIsRequiredMessage(window, messageSource, fieldName != null ? fieldName : field.getCaption());
			return false;
		}

		return true;
	}

	/**
	 * Displays an error in window if field's string value is null: "<fieldName> must be specified."
	 * 
	 * @param window - window where error message will be shown
	 * @param field - field to check if empty string. If empty, display warning
	 * @param messageSource - resource bundle where the error message will be retrieved from
	 * @param fieldName - name of the required Field which will appear in error message. If this is null, gets the field's caption as field
	 *        name.
	 * @return false if field is null. Else, return true;
	 */
	public static boolean validateRequiredStringField(
		final Window window, final AbstractField field, final SimpleResourceBundleMessageSource messageSource,
			final String fieldName) {

		if (BreedingManagerUtil.validateRequiredField(window, field, messageSource, fieldName)) {
			if (StringUtils.isEmpty(((String) field.getValue()).trim())) {
				BreedingManagerUtil.showFieldIsRequiredMessage(window, messageSource, fieldName != null ? fieldName : field.getCaption());
				return false;
			}
			return true;
		}

		return false;
	}

	/**
	 * Displays a error in window: "<fieldName> must be specified."
	 * 
	 * @param window - window where error message will be shown
	 * @param field - field to check if null. If null, displays warning.
	 * @param messageSource - resource bundle where the error message will be retrieved from
	 * @param fieldName - name of the required Field which will appear in error message. If this is null, gets the field's caption as field
	 *        name.
	 * 
	 */
	public static void showFieldIsRequiredMessage(final Window window, final SimpleResourceBundleMessageSource messageSource, final String fieldName) {
		// either the field caption or fieldName param must be available
		assert fieldName != null;
		assert messageSource != null;

		if (window != null) {
			MessageNotifier.showRequiredFieldError(window,
					MessageFormat.format(messageSource.getMessage(Message.ERROR_MUST_BE_SPECIFIED), fieldName));
		}

	}

	/**
	 * Queries for program's favorite locations and sets the values to combobox and map
	 * 
	 * @param workbenchDataManager
	 * @param germplasmDataManager
	 * @param locationComboBox
	 * @param mapLocation
	 * @param locationType @
	 */
	@SuppressWarnings("deprecation")
	public static void populateWithFavoriteLocations(final WorkbenchDataManager workbenchDataManager, final GermplasmDataManager germplasmDataManager,
			final ComboBox locationComboBox, final Map<String, Integer> mapLocation, final Integer locationType, final String programUUID) {

		locationComboBox.removeAllItems();

		final List<Integer> favoriteLocationIds = new ArrayList<Integer>();
		List<Location> favoriteLocations = new ArrayList<Location>();

		// Get location Id's
		final List<ProgramFavorite> list = germplasmDataManager.getProgramFavorites(FavoriteType.LOCATION, 1000, programUUID);
		for (final ProgramFavorite f : list) {
			favoriteLocationIds.add(f.getEntityId());
		}

		// Get locations
		favoriteLocations = germplasmDataManager.getLocationsByIDs(favoriteLocationIds);

		for (final Location favoriteLocation : favoriteLocations) {
			if (locationType > 0 && favoriteLocation.getLtype().equals(locationType) || locationType.equals(Integer.valueOf(0))) {
				final Integer locId = favoriteLocation.getLocid();
				locationComboBox.addItem(locId);
				locationComboBox.setItemCaption(locId, BreedingManagerUtil.getLocationNameDisplay(favoriteLocation));
				if (mapLocation != null) {
					mapLocation.put(favoriteLocation.getLname(), new Integer(locId));
				}
			}
		}
	}

	public static String getLocationNameDisplay(final Location loc) {
		String locNameDisplay = loc.getLname();
		if (loc.getLabbr() != null && !"".equalsIgnoreCase(loc.getLabbr()) && !"-".equalsIgnoreCase(loc.getLabbr())) {
			locNameDisplay += " - (" + loc.getLabbr() + ")";
		}
		return locNameDisplay;
	}

	/**
	 * Queries for program's favorite locations and sets the values to combobox and map
	 * 
	 * @param workbenchDataManager
	 * @param germplasmDataManager
	 * @param locationComboBox
	 * @param mapLocation @
	 */
	public static void populateWithFavoriteLocations(final WorkbenchDataManager workbenchDataManager, final GermplasmDataManager germplasmDataManager,
			final ComboBox locationComboBox, final Map<String, Integer> mapLocation, final String programUUID) {
		BreedingManagerUtil.populateWithFavoriteLocations(workbenchDataManager, germplasmDataManager, locationComboBox, mapLocation, 0,
				programUUID);
	}

	/**
	 * Queries for program's favorite locations and sets the values to combobox and map. Only selects locations with ltype = 410, 411, or
	 * 412
	 * 
	 * @param workbenchDataManager
	 * @param germplasmDataManager
	 * @param locationComboBox
	 * @param mapLocation @
	 */
	@SuppressWarnings("deprecation")
	public static void populateWithFavoriteBreedingLocations(final WorkbenchDataManager workbenchDataManager,
			final GermplasmDataManager germplasmDataManager, final ComboBox locationComboBox, final Map<String, Integer> mapLocation, final String programUUID) {

		locationComboBox.removeAllItems();

		final List<Integer> favoriteLocationIds = new ArrayList<Integer>();
		List<Location> favoriteLocations = new ArrayList<Location>();

		// Get location Id's
		final List<ProgramFavorite> list = germplasmDataManager.getProgramFavorites(FavoriteType.LOCATION, 1000, programUUID);
		for (final ProgramFavorite f : list) {
			favoriteLocationIds.add(f.getEntityId());
		}

		// Get locations
		favoriteLocations = germplasmDataManager.getLocationsByIDs(favoriteLocationIds);

		for (final Location favoriteLocation : favoriteLocations) {
			if (favoriteLocation.getLtype() != null
					&& (favoriteLocation.getLtype().equals(Integer.valueOf(410))
							|| favoriteLocation.getLtype().equals(Integer.valueOf(411)) || favoriteLocation.getLtype().equals(
							Integer.valueOf(412)))) {
				final Integer locId = favoriteLocation.getLocid();
				locationComboBox.addItem(locId);
				locationComboBox.setItemCaption(locId, BreedingManagerUtil.getLocationNameDisplay(favoriteLocation));
				if (mapLocation != null) {
					mapLocation.put(favoriteLocation.getLname(), new Integer(locId));
				}
			}
		}

	}

	/**
	 * Queries for program's favorite locations and sets the values to combobox and map
	 * 
	 * @param workbenchDataManager
	 * @param germplasmDataManager
	 * @param locationComboBox
	 * @param mapLocation @
	 */
	public static void populateWithFavoriteMethods(final WorkbenchDataManager workbenchDataManager, final GermplasmDataManager germplasmDataManager,
			final ComboBox methodComboBox, final String programUUID) {
		BreedingManagerUtil.populateWithFavoriteMethods(workbenchDataManager, germplasmDataManager, methodComboBox, null,
				programUUID);
	}

	public static boolean hasFavoriteMethods(final GermplasmDataManager germplasmDataManager, final String programUUID) {
		boolean hasFavMethod = false;
		try {
			final List<ProgramFavorite> list = germplasmDataManager.getProgramFavorites(FavoriteType.METHOD, 1000, programUUID);
			if (list != null && !list.isEmpty()) {
				hasFavMethod = true;
			}
		} catch (final MiddlewareQueryException e) {
			BreedingManagerUtil.LOG.error(e.getMessage(), e);
		}
		return hasFavMethod;
	}

	public static boolean hasFavoriteLocation(final GermplasmDataManager germplasmDataManager,
			final LocationDataManager locationDataManager, final int locationType, final String programUUID) {
		boolean hasFavLocation = false;
		try {
			// Get location Id's
			final List<ProgramFavorite> list = germplasmDataManager.getProgramFavorites(FavoriteType.LOCATION, 1000, programUUID);
			if (list != null && !list.isEmpty()) {

				// Has favorites of specified location type
				if (locationType > 0) {
					final List<Integer> ids = new ArrayList<>();
					for (final ProgramFavorite programFavorite : list) {
						ids.add(programFavorite.getEntityId());
					}

					final List<Location> locations = locationDataManager.getLocationsByIDs(ids);

					for (final Location location : locations) {
						if (location.getLtype().equals(locationType)) {
							hasFavLocation = true;
							break;
						}
					}
				} else {
					hasFavLocation = true;
				}

			}
		} catch (final MiddlewareQueryException e) {
			BreedingManagerUtil.LOG.error(e.getMessage(), e);
		}

		return hasFavLocation;
	}

	/**
	 * Queries for program's favorite locations and sets the values to combobox and map. Only selects method with the GIVEN type.
	 * 
	 * @param workbenchDataManager
	 * @param germplasmDataManager
	 * @param locationComboBox
	 * @param mapLocation
	 * @param mType @
	 */
	public static void populateWithFavoriteMethods(final WorkbenchDataManager workbenchDataManager, final GermplasmDataManager germplasmDataManager,
			final ComboBox methodComboBox, final String mType, final String programUUID) {

		methodComboBox.removeAllItems();

		final List<Integer> favoriteMethodIds = new ArrayList<Integer>();
		List<Method> favoriteMethods = new ArrayList<Method>();

		try {

			final List<ProgramFavorite> list = germplasmDataManager.getProgramFavorites(FavoriteType.METHOD, 1000, programUUID);
			for (final ProgramFavorite f : list) {
				favoriteMethodIds.add(f.getEntityId());
			}

			// Get Methods
			if (!favoriteMethodIds.isEmpty()) {
				favoriteMethods = germplasmDataManager.getMethodsByIDs(favoriteMethodIds);
			}

		} catch (final MiddlewareQueryException e) {
			BreedingManagerUtil.LOG.error(e.getMessage(), e);
		}

		populateMethodsComboBox(methodComboBox, mType, favoriteMethods);

	}
	
	public static void populateMethodsComboBox(final ComboBox methodComboBox, final String mType,
				final List<Method> methods) {
		for (final Method method : methods) {
			if (mType != null && mType.length() > 0 && method.getMtype() != null && method.getMtype().equals(mType)
					|| mType == null || mType.length() == 0) {
				final Integer methodId = method.getMid();
				methodComboBox.addItem(methodId);
				methodComboBox.setItemCaption(methodId, method.getMname());
			}
		}
	}

	public static String getTypeString(final String typeCode, final List<UserDefinedField> listTypes) {
		try {
			for (final UserDefinedField listType : listTypes) {
				if (listType.getFcode().equals(typeCode)) {
					return listType.getFname();
				}
			}
		} catch (final MiddlewareQueryException ex) {
			BreedingManagerUtil.LOG.error("Error in getting list types.", ex);
			return "Error in getting list types.";
		}

		return "Germplasm List";
	}

	public static String getDescriptionForDisplay(final GermplasmList germplasmList) {
		String description = "-";
		if (germplasmList != null && germplasmList.getDescription() != null && germplasmList.getDescription().length() != 0) {
			description = germplasmList.getDescription().replaceAll("<", "&lt;");
			description = description.replaceAll(">", "&gt;");
			if (description.length() > 27) {
				description = description.substring(0, 27) + "...";
			}
		}
		return description;
	}

	private static String getNameFromDao(final WorkbenchUser user, final Person p) {
		if (p != null) {
			return p.getFirstName() + " " + p.getMiddleName() + " " + p.getLastName();
		} else {
			return user.getName();
		}
	}

	public static HttpServletRequest getApplicationRequest() {
		return ContextApplication.currentRequest();
	}

	public static String getGermplasmGid(final Optional<Germplasm> germplasm) {
		if (germplasm.isPresent()) {
			if(germplasm.get().getGid()!=0) {
				return String.valueOf(germplasm.get().getGid());
			} else {
				return Name.UNKNOWN;
			}
		} else {
			return "-";
		}
	}

	public static String getGermplasmPreferredName(final Optional<Germplasm> germplasm) {
		if (germplasm.isPresent()) {
			return germplasm.get().getPreferredName().getNval();
		} else {
			return "-";
		}
	}
}
