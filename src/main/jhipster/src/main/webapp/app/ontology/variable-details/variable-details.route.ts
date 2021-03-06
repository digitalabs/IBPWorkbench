import { Routes } from '@angular/router';
import { RouteAccessService } from '../../shared';
import { VariableDetailsComponent } from './variable-details.component';
import { DetailsComponent } from './details.component';
import { ValidValuesComponent } from './valid-values.component';
import { VariableDetailsPopupComponent } from './variable-details-modal.component';

export const variableDetailsRoutes: Routes = [
    {
        path: 'variable-details',
        component: VariableDetailsComponent,
        canActivate: [RouteAccessService],
        children: [
            {
                path: '',
                redirectTo: 'details',
                pathMatch: 'full'
            },
            {
                path: 'details',
                component: DetailsComponent
            },
            {
                path: 'valid-values',
                component: ValidValuesComponent
            }
        ]
    },
    {
        path: 'variable-details-popup',
        component: VariableDetailsPopupComponent,
        outlet: 'popup'
    }
];
