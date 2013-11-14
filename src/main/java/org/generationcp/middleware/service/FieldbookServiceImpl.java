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
package org.generationcp.middleware.service;

import java.util.List;

import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.fieldbook.FieldMapInfo;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.service.api.FieldbookService;

public class FieldbookServiceImpl extends Service implements FieldbookService {

    public FieldbookServiceImpl(
            HibernateSessionProvider sessionProviderForLocal,
            HibernateSessionProvider sessionProviderForCentral) {
        super(sessionProviderForLocal, sessionProviderForCentral);
    }

    @Override
    public List<StudyDetails> getAllLocalNurseryDetails() throws MiddlewareQueryException{
        return getStudyDataManager().getAllStudyDetails(Database.LOCAL, StudyType.N);
    }
    
    @Override 
    public List<StudyDetails> getAllLocalTrialStudyDetails() throws MiddlewareQueryException{
        return getStudyDataManager().getAllStudyDetails(Database.LOCAL, StudyType.T);
    }

    @Override
    public FieldMapInfo getFieldMapInfoOfTrial(int trialId) throws MiddlewareQueryException{
        return getStudyDataManager().getFieldMapInfoOfStudy(trialId, StudyType.T);
    }
    
    @Override 
    public FieldMapInfo getFieldMapInfoOfNursery(int nurseryId) throws MiddlewareQueryException{
        return getStudyDataManager().getFieldMapInfoOfStudy(nurseryId, StudyType.N);
    }

    @Override 
    public List<Location> getAllLocations()throws MiddlewareQueryException{
    	GermplasmDataManager germplasmDataManager = getGermplasmDataManager();
    	return germplasmDataManager.getAllLocations();
    }

}
