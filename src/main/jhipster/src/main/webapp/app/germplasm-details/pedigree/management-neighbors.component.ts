import { Component, Input, OnInit } from '@angular/core';
import { GermplasmDto } from '../../shared/germplasm/model/germplasm.model';
import { GermplasmPedigreeService } from '../../shared/germplasm/service/germplasm.pedigree.service';

@Component({
    selector: 'jhi-management-neighbors',
    template: `
		<jhi-germplasm-table [germplasmList]="germplasmList"></jhi-germplasm-table>`
})
export class ManagementNeighborsComponent implements OnInit {

    @Input() gid: number;
    germplasmList: GermplasmDto[] = [];

    constructor(public germplasmPedigreeService: GermplasmPedigreeService) {
    }

    ngOnInit(): void {
        this.germplasmPedigreeService.getManagementNeighbors(this.gid).subscribe((result) => {
            this.germplasmList = result;
        });
    }
}
