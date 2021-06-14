export const MANAGE_INVENTORY_PERMISSIONS = [
    'ADMIN',
    'CROP_MANAGEMENT',
    'MANAGE_INVENTORY',
];
export const MANAGE_LOT_PERMISSIONS = [
    ...MANAGE_INVENTORY_PERMISSIONS,
    'MANAGE_LOTS',
];
export const LOT_LABEL_PRINTING_PERMISSIONS = [
    ...MANAGE_LOT_PERMISSIONS, 'LOT_LABEL_PRINTING'
];

export const SEARCH_GERMPLASM_PERMISSIONS = [
    'ADMIN',
    'GERMPLASM',
    'MANAGE_GERMPLASM',
    'SEARCH_GERMPLASM'
];

export const IMPORT_GERMPLASM_PERMISSIONS = [
    'ADMIN',
    'GERMPLASM',
    'MANAGE_GERMPLASM',
    'IMPORT_GERMPLASM'
];

export const IMPORT_GERMPLASM_UPDATES_PERMISSIONS = [
    'ADMIN',
    'GERMPLASM',
    'MANAGE_GERMPLASM',
    'IMPORT_GERMPLASM_UPDATES'
];

export const DELETE_GERMPLASM_PERMISSIONS = [
    'ADMIN',
    'GERMPLASM',
    'MANAGE_GERMPLASM',
    'DELETE_GERMPLASM'
];

export const GERMPLASM_LABEL_PRINTING_PERMISSIONS = [
    'ADMIN',
    'GERMPLASM',
    'MANAGE_GERMPLASM',
    'GERMPLASM_LABEL_PRINTING'
];

export const SITE_ADMIN_PERMISSIONS = [
    'ADMIN',
    'ADMINISTRATION',
    'SITE_ADMIN'
];

export const ADD_PROGRAM_PERMISSION = [
    'ADMIN',
    'CROP_MANAGEMENT',
    'ADD_PROGRAM',
    'MANAGE_PROGRAMS'
];

/**
 * {@see org.generationcp.middleware.pojos.workbench.PermissionsEnum.HAS_MANAGE_STUDIES_VIEW}
 */
export const MANAGE_STUDIES_VIEW_PERMISSIONS = [
    'ADMIN',
    'STUDIES',
    'MANAGE_STUDIES',
    'MS_MANAGE_OBSERVATION_UNITS',
    'MS_WITHDRAW_INVENTORY',
    'MS_CREATE_PENDING_WITHDRAWALS',
    'MS_CREATE_CONFIRMED_WITHDRAWALS',
    'MS_CREATE_LOTS'
];

export const GERMPLASM_LISTS_PERMISSIONS = [
    'ADMIN',
    'LISTS',
    'GERMPLASM_LISTS'
];

export const EDIT_GERMPLASM_PERMISSION = [
    'ADMIN',
    'GERMPLASM',
    'MANAGE_GERMPLASM',
    'EDIT_GERMPLASM'
];

export const GROUP_GERMPLASM_PERMISSIONS = [
    'ADMIN',
    'GERMPLASM',
    'MANAGE_GERMPLASM',
    'GROUP_GERMPLASM'
];

export const UNGROUP_GERMPLASM_PERMISSIONS = [
    'ADMIN',
    'GERMPLASM',
    'MANAGE_GERMPLASM',
    'UNGROUP_GERMPLASM'
];
