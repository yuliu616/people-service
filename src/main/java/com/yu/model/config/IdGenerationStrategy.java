package com.yu.model.config;

/**
 * how to generate ID field for new record of a model class (table)
 */
public enum IdGenerationStrategy {

    /**
     * use a separate table with auto-number defined as its primary key (id).
     * create new record in it will return a new id which will be used as id
     * for the target class.
     */
    ID_TABLE,

    /**
     * use(generate) UUID as the id
     */
    UUID,

    /**
     * just generate a random number
     */
    RANDOM,

    /**
     * generate a random number, adding a predefined prefix
     */
    RANDOM_WITH_PREFIX,

    /**
     * generate a random number, adding a predefined suffix
     */
    RANDOM_WITH_SUFFIX,

    /**
     * keep a thread-safe counter in app server, use it as id
     * with predefined prefix added.
     */
    APP_SERVER_COUNTER_WITH_PREFIX,

    /**
     * keep a thread-safe counter in app server, use it as id
     * with predefined suffix added.
     */
    APP_SERVER_COUNTER_WITH_SUFFIX,

}
