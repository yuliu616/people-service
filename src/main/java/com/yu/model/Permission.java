package com.yu.model;

public enum Permission {

    /**
     * permission to use the system/service
     */
    BASIC,

    /**
     * if one have this permission, he can do anything
     */
    ANYTHING,

    /**
     * permission to load/check existence of
     * a record of model `People` with explicit ID
     */
    PEOPLE_GET,

    /**
     * permission to load/search for
     * record of model `People` (using condition instead of ID)
     */
    PEOPLE_SEARCH,

    /**
     * permission to create new record of model `People`
     */
    PEOPLE_ADD,

    /**
     * permission to update a particular record of model `People`
     */
    PEOPLE_UPDATE,

    /**
     * permission to delete record of model `People`
     */
    PEOPLE_DELETE,

}
