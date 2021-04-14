import { Component, OnDestroy, OnInit } from '@angular/core';
import { NgbActiveModal, NgbCalendar, NgbDate } from '@ng-bootstrap/ng-bootstrap';
import { NameType } from '../../../shared/germplasm/model/name-type.model';
import { LocationModel } from '../../../shared/location/model/location.model';
import { ActivatedRoute } from '@angular/router';
import { PopupService } from '../../../shared/modal/popup.service';
import { GermplasmNameContext } from './germplasm-name.context';
import { GermplasmService } from '../../../shared/germplasm/service/germplasm.service';
import { JhiEventManager } from 'ng-jhipster';

@Component({
    selector: 'jhi-germplasm-name-modal',
    templateUrl: './germplasm-name-modal.component.html'
})
export class GermplasmNameModalComponent implements OnInit, OnDestroy {

    isEditMode: boolean;
    gid: number;
    nameTypes: Promise<NameType[]>;
    locations: LocationModel[];
    isLoading: boolean;

    nameId: number;
    name: string;
    nameTypeCode: string;
    locationId: number;
    date: NgbDate;
    preferred: boolean;


    constructor(public activeModal: NgbActiveModal,
                private eventManager: JhiEventManager,
                private germplasmNameContext: GermplasmNameContext,
                private germplasmService: GermplasmService,
                private calendar: NgbCalendar) {
    }

    ngOnInit(): void {
        this.nameTypes = this.germplasmService.getGermplasmNameTypes([]).toPromise();
        this.date = this.calendar.getToday();
        if (this.germplasmNameContext.germplasmName) {
            console.log(this.germplasmNameContext.germplasmName);
            this.nameId = this.germplasmNameContext.germplasmName.id;
            this.name = this.germplasmNameContext.germplasmName.name;
            this.nameTypeCode = this.germplasmNameContext.germplasmName.nameTypeCode;
            this.locationId = this.germplasmNameContext.germplasmName.locationId;
            this.date = this.convertStringToNgbDate(this.germplasmNameContext.germplasmName.date);
            this.preferred = this.germplasmNameContext.germplasmName.preferred;
        }
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {

        if (this.nameId) {
            // if name id is available, we have to update the name
            this.germplasmService.updateGermplasmName(this.gid, this.nameId, {
                name: this.name,
                nameTypeCode: this.nameTypeCode,
                preferredName: this.preferred || false,
                locationId: this.locationId,
                date: this.convertNgbDateToString(this.date)
            }).toPromise().then((result) => {
                this.clear();
            });
        } else {
            // If name id is not available, we have to create a new name
            this.germplasmService.createGermplasmName(this.gid, {
                name: this.name,
                nameTypeCode: this.nameTypeCode,
                preferredName: this.preferred || false,
                locationId: this.locationId,
                date: this.convertNgbDateToString(this.date)
            }).toPromise().then((result) => {
                this.clear();
            });
        }

        this.eventManager.broadcast({ name: 'germplasmNameChanged' });

    }

    isFormValid(f) {
        return f.form.valid && this.name
            && this.nameTypeCode && this.locationId && this.date;
    }

    ngOnDestroy(): void {
        this.germplasmNameContext.germplasmName = null;
    }

    // TODO: Create helper class
    convertNgbDateToString(date: NgbDate) {
        return '' + date.year + this.twoDigit(date.month) + this.twoDigit(date.day);
    }

    convertStringToNgbDate(dateString: string): NgbDate {
        if (dateString && dateString.length == 8) {
            let year = Number(dateString.substring(0, 4));
            let month = Number(dateString.substring(4, 6));
            let day = Number(dateString.substring(6, 8));
            return new NgbDate(year, month, day);
        }
        return this.calendar.getToday();
    }

    twoDigit(n) {
        return (n < 10 ? '0' : '') + n;
    }
}

@Component({
    selector: 'jhi-germplasm-name-popup',
    template: ``
})
export class GermplasmNamePopupComponent implements OnInit {

    constructor(private route: ActivatedRoute,
                private popupService: PopupService) {
    }

    ngOnInit(): void {
        const modal = this.popupService.open(GermplasmNameModalComponent as Component, { windowClass: 'modal-medium', backdrop: 'static' });
        modal.then((modalRef) => {
            modalRef.componentInstance.gid = Number(this.route.snapshot.paramMap.get('gid'));
        });

    }

}
