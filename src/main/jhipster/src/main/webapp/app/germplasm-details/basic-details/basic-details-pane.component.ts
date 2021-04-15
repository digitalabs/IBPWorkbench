import { Component, OnInit } from '@angular/core';
import { JhiAlertService, JhiEventManager, JhiLanguageService } from 'ng-jhipster';
import { TranslateService } from '@ngx-translate/core';
import { GermplasmService } from '../../shared/germplasm/service/germplasm.service';
import { GermplasmDetailsContext } from '../germplasm-details.context';
import { GermplasmDto, GermplasmName } from '../../shared/germplasm/model/germplasm.model';
import { GermplasmDetailsUrlService } from '../../shared/germplasm/service/germplasm-details.url.service';
import { PopupService } from '../../shared/modal/popup.service';
import { Router } from '@angular/router';
import { GermplasmNameContext } from '../../entities/germplasm/name/germplasm-name.context';
import { Subscription } from 'rxjs';
import { ModalConfirmComponent } from '../../shared/modal/modal-confirm.component';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
    selector: 'jhi-basic-details-pane',
    templateUrl: './basic-details-pane.component.html'
})
export class BasicDetailsPaneComponent implements OnInit {

    eventSubscriber: Subscription;
    germplasm: GermplasmDto;
    geojson: any;

    constructor(public languageservice: JhiLanguageService,
                public translateService: TranslateService,
                private eventManager: JhiEventManager,
                private germplasmService: GermplasmService,
                private germplasmDetailsContext: GermplasmDetailsContext,
                public germplasmDetailsUrlService: GermplasmDetailsUrlService,
                private modalService: NgbModal,
                private popupService: PopupService,
                private router: Router,
                private germplasmNameContext: GermplasmNameContext,
                private alertService: JhiAlertService) {
    }

    ngOnInit(): void {
        this.loadGermplasm();
        this.registerGermplasmNameChanged();
    }

    registerGermplasmNameChanged() {
        this.eventSubscriber = this.eventManager.subscribe('germplasmNameChanged', (event) => {
            this.loadGermplasm();
        });
    }

    loadGermplasm(): void {
        this.germplasmService.getGermplasmById(this.germplasmDetailsContext.gid).toPromise().then((value) => {
            this.germplasm = value.body;
        })
    }

    editGermplasmBasicDetails(): void {
        this.germplasmDetailsContext.germplasm = this.germplasm;
        this.router.navigate(['/', { outlets: { popup: 'germplasm-edit-basic-details' }, }], {
            queryParamsHandling: 'merge'
        });
    }

    editGermplasmName(germplasmName: GermplasmName): void {
        this.germplasmNameContext.germplasmName = germplasmName;
        this.router.navigate(['/', { outlets: { popup: 'germplasm-name-dialog/' + this.germplasm.gid }, }], {
            queryParamsHandling: 'merge'
        });
    }

    createGermplasmName(): void {
        this.router.navigate(['/', { outlets: { popup: 'germplasm-name-dialog/' + this.germplasm.gid }, }], {
            queryParamsHandling: 'merge'
        });
    }

    deleteGermplasmName(nameId: number): void {
        const confirmModalRef = this.modalService.open(ModalConfirmComponent as Component);
        confirmModalRef.componentInstance.message = 'Are you sure you want to delete this germlasm name?';

        confirmModalRef.result.then(() => {
            this.germplasmService.deleteGermplasmName(this.germplasm.gid, nameId).toPromise().then((result) => {
                this.alertService.success('germplasm-name-modal.delete.success');
                this.loadGermplasm();
            }).catch((response) => {
                this.alertService.error('error.custom', { param: response.error.errors[0].message });
            });
        }, () => confirmModalRef.dismiss());
    }
}