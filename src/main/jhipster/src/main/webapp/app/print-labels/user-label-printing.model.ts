export class UserLabelPrinting {
    constructor(
        public studyId?: number,
        public study?: string,
        public fieldMapInfo?: string,
        public fieldMapInfoList?: any,
        public name?: string,
        public owner?: string,
        public description?: string,
        public type?: string,
        public date?: string,
        public numberOfEntries?: number,
        public numberOfLotsWithReservations?: number,
        public numberOfCopies?: number,
        public sorting?: string,
        public title?: string,
        public objective?: string,
        public numberOfInstances?: number,
        public totalNumberOfLabelToPrint?: number,
        public sizeOfLabelSheet?: string,
        public numberOfLabelPerRow?: number,
        public numberOfRowsPerPageOfLabel?: number,
        public leftSelectedLabelFields?: string,
        public rightSelectedLabelFields?: string,
        public mainSelectedLabelFields?: string,
        public filename?: string,
        public generateType?: string,
        public order?: string,
        public filenameWithExtension?: string,
        public filenameDLLocation?: string,
        public isFieldMapsExisting?: string,
        public settingsName?: string,
        public includeColumnHeadinginNonPdf?: string,
        public isStockList?: string,
        public stockListId?: string,
        public stockListTypeName?: string,
        public inventoryDetailsList?: string,
        public barcodeGeneratedAutomatically = true,
        public firstBarcodeField = '',
        public secondBarcodeField = '',
        public thirdBarcodeField = '',
        public barcodeNeeded = false
    ) {
    }
}

export class LabelsNeededSummary {
    constructor(
        public headers?: any[],
        public values?: any[],
        public totalNumberOfLabelsNeeded?: number
    ) { }
}