import { Component } from '@angular/core';
import { GermplasmNameSettingModel } from '../../shared/germplasm/model/germplasm-name-setting.model';
import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { GermplasmService } from '../../shared/germplasm/service/germplasm.service';
import { JhiAlertService } from 'ng-jhipster';
import { GermplasmCodeNameType } from '../../shared/germplasm/model/germplasm-code-name-batch-request.model';

@Component({
    selector: 'jhi-germplasm-coding',
    templateUrl: './germplasm-coding-dialog.component.html'
})
export class GermplasmCodingDialogComponent {

    GermplasmCodeNameType = GermplasmCodeNameType;

    gids: number[];
    automaticNaming: boolean;
    nameType: GermplasmCodeNameType = GermplasmCodeNameType.CODE1;
    isProcessing: boolean;
    germplasmCodeNameSetting: GermplasmNameSettingModel = new GermplasmNameSettingModel();
    numberOfDigits = Array(9).fill(1).map((x, i) => i + 1);
    nextCodeNameSequence: string;

    constructor(private modal: NgbActiveModal,
                private germplasmService: GermplasmService,
                private alertService: JhiAlertService,
                private modalService: NgbModal) {
        this.automaticNaming = true;
        this.germplasmCodeNameSetting.addSpaceBetweenPrefixAndCode = false;
        this.germplasmCodeNameSetting.addSpaceBetweenSuffixAndCode = false;
        this.germplasmCodeNameSetting.numOfDigits = 1;
    }

    close() {
        this.modal.close();
    }

    applyCodes() {
        this.isProcessing = true;
        this.germplasmService.createGermplasmCodeNames({
            gids: this.gids,
            nameType: this.nameType,
            germplasmCodeNameSetting: this.automaticNaming ? null : this.germplasmCodeNameSetting
        }).toPromise().then((result) => {
            this.modal.close(result);
            this.isProcessing = false;
        }).catch((response) => {
            this.alertService.error('error.custom', { param: response.error.errors[0].message });
            this.isProcessing = false;
        });
    }

    validate() {
        return this.automaticNaming || (this.germplasmCodeNameSetting.prefix && this.germplasmCodeNameSetting.numOfDigits);
    }

    getNextNameSequence() {
        if (this.germplasmCodeNameSetting.prefix) {
            this.germplasmService.getNextNameInSequence(this.germplasmCodeNameSetting).toPromise().then((response) => {
                if (response.body) {
                    this.nextCodeNameSequence = response.body;
                }
            }).catch((response) => {
                if (response.error) {
                    this.alertService.error('error.custom', { param: JSON.parse(response.error).errors[0].message });
                }
            });
        }
    }

}
