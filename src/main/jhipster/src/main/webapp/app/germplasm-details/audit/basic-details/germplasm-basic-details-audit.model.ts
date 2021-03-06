import { GermplasmAudit } from '../germplasm-audit.model';

export class GermplasmBasicDetailsAudit extends GermplasmAudit {
    constructor(
        public revisionType: string,
        public createdBy: string,
        public createdDate: number,
        public modifiedBy: string,
        public modifiedDate: number,
        public creationDate: string,
        public locationName: string,
        public groupId: number,
        public locationChanged: boolean,
        public creationDateChanged: boolean,
        public groupIdChanged: boolean
    ) {
        super(revisionType, createdBy, createdDate, modifiedBy, modifiedDate);
    }

}
