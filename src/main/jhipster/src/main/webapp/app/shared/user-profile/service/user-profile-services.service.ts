import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { UserProfileModel } from '../model/user-profile.model';
import { HttpClient } from '@angular/common/http';
import { SERVER_API_URL } from '../../../app.constants';

@Injectable()
export class UserProfileServices {

    private readonly resourceUrl: string;

    constructor(private http: HttpClient) {
        this.resourceUrl = SERVER_API_URL
    }

    update(userProfile: UserProfileModel, userId: number): Observable<void> {
        return this.http.put<void>(this.resourceUrl + `users/${userId}/profile`, userProfile);
    }

}
