import { Crop } from './crop.model';
import { Role } from './role.model';

export class User {
	// transient field for easy sorting and filtering
	public roleName : String = "";

    constructor(public id: string,
                public firstName: string,
                public lastName: string,
                public username: string,
                public crops: Crop[],
                public role: Role,
                public email: string,
                public status: string ) {
        this.roleName = role ? role.name : "";
    }

}
