package org.generationcp.middleware.v2.domain.builder;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.v2.domain.Term;
import org.generationcp.middleware.v2.pojos.CVTerm;

public class TermBuilder extends Builder {

	public TermBuilder(HibernateSessionProvider sessionProviderForLocal,
			               HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}

	public Term get(int termId) throws MiddlewareQueryException {
		Term term = null;
		if (setWorkingDatabase(termId)) {
			term = mapCVTermToTerm(getCvTermDao().getById(termId));
		}
		return term;
	}
	
	private Term mapCVTermToTerm(CVTerm cVTerm){
		Term term = null;
		
		if (cVTerm != null){
			term = new Term(cVTerm.getCvTermId(), cVTerm.getName(), cVTerm.getDefinition());
			term.setObsolete(cVTerm.isObsolete());
			term.setVocabularyId(cVTerm.getCv());
		}
		return term;
	}
}
