/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.middleware.manager.api;


import java.util.List;
import java.util.Map;
import java.util.Set;

import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Property;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TraitReference;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;

// TODO: Auto-generated Javadoc
/**
 * This is the API for retrieving ontology data from the CHADO schema.
 * 
 * 
 */
public interface OntologyDataManager {

	/**
	 * Retrieves a Term record given its id. This can also be used to retrieve traits, methods and scales.
	 *
	 * @param termId the term id
	 * @return the term by id
	 * @throws MiddlewareQueryException the middleware query exception
	 */
    public Term getTermById(int termId) throws MiddlewareQueryException;
    
    /**
     * Retrieves a StandardVariable given its id.
     *
     * @param stdVariableId the std variable id
     * @return the standard variable
     * @throws MiddlewareQueryException the middleware query exception
     */
	public StandardVariable getStandardVariable(int stdVariableId) throws MiddlewareQueryException; 
	
	 /**
 	 * Retrieves a the standardVariableId given the property, scale and method Ids.
 	 *
 	 * @param propertyId the property id
 	 * @param scaleId the scale id
 	 * @param methodId the method id
 	 * @return the standard variable id by property scale method
 	 * @throws MiddlewareQueryException the middleware query exception
 	 */
	public Integer getStandardVariableIdByPropertyScaleMethod(Integer propertyId,Integer scaleId, Integer methodId) throws MiddlewareQueryException; 

	
	/**
	 * Find standard variables by name or synonym.
	 *
	 * @param nameOrSynonym the name or synonym
	 * @return the sets the
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Set<StandardVariable> findStandardVariablesByNameOrSynonym(String nameOrSynonym) throws MiddlewareQueryException;
	
	/**
	 * Adds a StandardVariable to the database.
	 * Must provide the property, method, scale, dataType, and storedIn info.
	 * Otherwise, it will throw an exception.
	 *
	 * @param stdVariable the std variable
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	public void addStandardVariable(StandardVariable stdVariable) throws MiddlewareQueryException;

	
	/**
	 * Adds a new Method to the database.
	 * Creates a new cvterm entry in the local database.
	 * Returns a negative id.
	 *
	 * @param name the name
	 * @param definition the definition
	 * @return the term
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Term addMethod(String name, String definition) throws MiddlewareQueryException;
	
	/**
	 * Find method by id.
	 *
	 * @param id the id
	 * @return the term
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Term findMethodById(int id) throws MiddlewareQueryException;
	
	/**
	 * Find method by name.
	 *
	 * @param name the name
	 * @return the term
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Term findMethodByName(String name) throws MiddlewareQueryException;
	
	/**
	 * Retrieves the StandardVariable given the property, scale and method names.
	 *
	 * @param property the property
	 * @param scale the scale
	 * @param method the method
	 * @return StandardVariable
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	StandardVariable findStandardVariableByTraitScaleMethodNames(String property, String scale, String method) throws MiddlewareQueryException;
	
	/**
	 * Retrieve method given the traitId.
	 *
	 * @param traitId the trait id
	 * @return List<Term>
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Term> getMethodsForTrait(Integer traitId) throws MiddlewareQueryException;
	
	/**
	 * Retrieve scales given the traitId.
	 *
	 * @param traitId the trait id
	 * @return List<Term>
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Term> getScalesForTrait(Integer traitId) throws MiddlewareQueryException;
	
	/**
	 * Returns the list of Term entries based on the given CvId. The CvId can be CvId.PROPERTIES, CvId.METHODS, CvId.SCALES, CvId.VARIABLES.
	 * 
	 * This can be used to get all scales, all traits, all trait methods, all properties, all methods and all variables.
	 *
	 * @param cvId the cv id
	 * @return the all terms by cv id
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Term> getAllTermsByCvId(CvId cvId) throws MiddlewareQueryException;
	
	/**
	 * Returns the list of Term entries based on the given CvId filtered by start and number of records. The CvId can be CvId.PROPERTIES, CvId.METHODS, CvId.SCALES, CvId.VARIABLES.
	 * 
	 * 
	 * This can be used to get all scales, all traits, all trait methods, all properties, all methods and all variables.
	 *
	 * @param cvId the cv id
	 * @param start the start
	 * @param numOfRows the num of rows
	 * @return the all terms by cv id
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Term> getAllTermsByCvId(CvId cvId, int start, int numOfRows) throws MiddlewareQueryException;
	
	
	/**
	 * Returns the count of entries based on the given CvId. The CvId can be CvId.PROPERTIES, CvId.METHODS, CvId.SCALES, CvId.VARIABLES.
	 * 
	 * This can be used to count all scales, all traits, all trait methods, all properties, all methods and  all variables.
	 *
	 * @param cvId the cv id
	 * @return the long
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	long countTermsByCvId(CvId cvId) throws MiddlewareQueryException; 
	
	/**
	 * Returns Term based on the given name and cvid.
	 *
	 * @param name the name
	 * @param cvId the cv id
	 * @return Term
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Term findTermByName(String name, CvId cvId) throws MiddlewareQueryException;
	
	/**
	 * Adds a new Term to the database.
	 * Creates a new cvterm entry in the local database.
	 * Returns a negative id.
	 *
	 * @param name the name
	 * @param definition the definition
	 * @param cvId the cv id
	 * @return the term
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Term addTerm(String name, String definition, CvId cvId) throws MiddlewareQueryException;
	
	/**
	 * Returns the list of Term entries based on possible data types.
	 *
	 * @return list of data type Term objects
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Term> getDataTypes() throws MiddlewareQueryException;
	
	
	/**
	 * Returns the key-value pairs of PhenotypicType - StandardVariable.
	 *
	 * @param type the type
	 * @param start the start
	 * @param numOfRows the num of rows
	 * @return Map of PhenotypicType - StandardVariable
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Map<String, StandardVariable> getStandardVariablesForPhenotypicType(PhenotypicType type, int start,int numOfRows) throws MiddlewareQueryException;
	
	/**
	 * Returns the standard variables associated to a project from projectprop, cvterm or trait - in the given order.
	 * 
	 * 1. Search for DISTINCT standard variables used for projectprop records where projectprop.value equals input name (eg. REP)
	 * 2. If no variable found, search for cvterm (standard variables) with given name.
	 * 3. If no variable still found for steps 1 and 2, treat the header as a trait / property name.
	 * Search for trait with given name and return the standard variables using that trait (if any)
	 *
	 * @param headers the headers
	 * @return The key in map would be the header string. If no standard variable list found, an empty list on map is returned for that header key.
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Map<String, List<StandardVariable>> getStandardVariablesInProjects(List<String> headers) throws MiddlewareQueryException;

	/**
	 * Retrieves the List of Terms matching the given nameOrSynonym and CvId.
	 *
	 * @param nameOrSynonym the name or synonym
	 * @param cvId the cv id
	 * @return the list
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<Term> findTermsByNameOrSynonym(String nameOrSynonym, CvId cvId) throws MiddlewareQueryException;
	
	/**
	 * Returns the count of Term entries based on possible "is a" of properties.
	 *
	 * @return count of is_a Term objects
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	
	long countIsAOfProperties() throws MiddlewareQueryException;
	
	/**
	 * Returns the list of Term entries based on possible "is a" of properties.
	 *
	 * @param start the start
	 * @param numOfRows the num of rows
	 * @return list of is_a Term objects
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	
	List<Term> getIsAOfProperties(int start, int numOfRows) throws MiddlewareQueryException;
	
	/**
	 * Adds a new property to the database that adds the property term and it's is a relationship)
	 * Creates a new cvterm entry in the local database and a cvterm_relationship of type is_a
	 * Returns the added term.
	 *
	 * @param name the name
	 * @param definition the definition
	 * @param isA the is a
	 * @return Term
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Term addProperty(String name, String definition, int isA) throws MiddlewareQueryException;
	
	/**
	 * Given the termId, retrieve the Property POJO.
	 *
	 * @param termId the term id
	 * @return property
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Property getProperty(int termId) throws MiddlewareQueryException;
	
	/**
	 * Given the name, retrieve the Property POJO.
	 *
	 * @param name the name
	 * @return property
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	Property getProperty(String name) throws MiddlewareQueryException;
	
	
	/**
	 * Retrieves the trait classes containing the hierarchical structure
	 * of the trait groups: Trait Group --> Properties --> Standard Variables.
	 * 
	 * The lists are returned in alphabetical order of the name.
	 *
	 * @return the trait groups
	 * @throws MiddlewareQueryException the middleware query exception
	 */
	List<TraitReference> getTraitGroups() throws MiddlewareQueryException;
	

    /**
     * Retrieves all the trait classes (id, name, description) from central and local.
     * 
     * The lists are returned in alphabetical order of the name.
     *
     * @return the all trait classes
     * @throws MiddlewareQueryException the middleware query exception
     */
    List<TraitReference> getAllTraitClasses() throws MiddlewareQueryException;
	
    
    /**
     * Retrieves all the Term entries based on the given list of ids.
     *
     * @param ids the ids
     * @return the terms by ids
     * @throws MiddlewareQueryException the middleware query exception
     */
    public List<Term> getTermsByIds(List<Integer> ids) throws MiddlewareQueryException;
    
    
    /**
     * Adds the trait class.
     *
     * @return the term
     * @throws MiddlewareQueryException the middleware query exception
     */
    public Term addTraitClass(String name, String definition, CvId cvId) throws MiddlewareQueryException;

}
