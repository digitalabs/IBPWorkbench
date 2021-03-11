import { Routes } from '@angular/router';
import { GermplasmDetailsComponent } from './germplasm-details.component';
import { BasicDetailsPaneComponent } from './basic-details/basic-details-pane.component';
import { AttributesPaneComponent } from './attributes/attributes-pane.component';
import { PedigreePaneComponent } from './pedigree/pedigree-pane.component';
import { ObservationsPaneComponent } from './observations/observations-pane.component';
import { InventoryPaneComponent } from './inventory/inventory-pane.component';
import { ListsPaneComponent } from './lists/lists-pane.component';
import { SamplesPaneComponent } from './samples/samples-pane.component';
import { GermplasmDetailsPopupComponent } from './germplasm-details-dialog.component';

export const germplasmDetailsPaneRoutes: Routes = [
    {
        path: 'germplasm-basic-details',
        component: BasicDetailsPaneComponent
    },
    {
        path: 'attributes',
        component: AttributesPaneComponent
    },
    {
        path: 'pedigree',
        component: PedigreePaneComponent
    },
    {
        path: 'observations',
        component: ObservationsPaneComponent
    },
    {
        path: 'inventory',
        component: InventoryPaneComponent
    },
    {
        path: 'lists',
        component: ListsPaneComponent
    },
    {
        path: 'samples',
        component: SamplesPaneComponent
    }
]

export const germplasmDetailsRoutes: Routes = [
    {
        // Path for showing germplasm details as a standalone page in a internet browser.
        path: 'germplasm-details/:gid',
        component: GermplasmDetailsComponent,
        children: [
            ...germplasmDetailsPaneRoutes,
            {
                path: '',
                redirectTo: 'germplasm-basic-details',
                pathMatch: 'full'
            }
        ]
    },
    {
        // Path for showing germplasm details as a dialog box in Angular.
        path: 'germplasm-details-dialog/:gid',
        component: GermplasmDetailsPopupComponent,
        outlet: 'popup'
    }
];
