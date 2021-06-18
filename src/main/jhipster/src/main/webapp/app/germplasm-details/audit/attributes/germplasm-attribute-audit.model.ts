export class GermplasmAttributeAudit {
    constructor(
        public revisionType: string,
        public attributeType: string,
        public value: string,
        public creationDate: string,
        public locationName: string,
        public createdBy: string,
        public createdDate: number,
        public modifiedBy: string,
        public modifiedDate: number,
        public attributeTypeChanged: boolean,
        public locationChanged: boolean,
        public creationDateChanged: boolean,
        public valueChanged: boolean
) {
    }
}
