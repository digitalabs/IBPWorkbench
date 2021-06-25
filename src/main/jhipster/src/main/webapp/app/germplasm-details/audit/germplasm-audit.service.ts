import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SERVER_API_URL } from '../../app.constants';
import { ParamContext } from '../../shared/service/param.context';
import { createRequestOption } from '../../shared';
import { GermplasmNameAudit } from './names/germplasm-name-audit.model';
import { GermplasmAttributeAudit } from './attributes/germplasm-attribute-audit.model';
import { GermplasmBasicDetailsAudit } from './basic-details/germplasm-basic-details-audit.model';

@Injectable()
export class GermplasmAuditService {

    constructor(private http: HttpClient,
                private context: ParamContext) {
    }

    getNamesChanges(gid: number, nameId: number, request: any): Observable<HttpResponse<GermplasmNameAudit[]>> {
        const params = createRequestOption(request);
        return this.http.get<GermplasmNameAudit[]>(SERVER_API_URL + `crops/${this.context.cropName}/germplasm/${gid}/name/${nameId}/changes`,
            { params, observe: 'response' });
    }

    getAttributesChanges(gid: number, attributeId: number, request: any): Observable<HttpResponse<GermplasmAttributeAudit[]>> {
        const params = createRequestOption(request);
        return this.http.get<GermplasmAttributeAudit[]>(SERVER_API_URL + `crops/${this.context.cropName}/germplasm/${gid}/attribute/${attributeId}/changes`,
            { params, observe: 'response' });
    }

    getBasicDetailsChanges(gid: number, request: any): Observable<HttpResponse<GermplasmBasicDetailsAudit[]>> {
        const params = createRequestOption(request);
        return this.http.get<GermplasmBasicDetailsAudit[]>(SERVER_API_URL + `crops/${this.context.cropName}/germplasm/${gid}/basic-details/changes`,
            { params, observe: 'response' });
    }

}