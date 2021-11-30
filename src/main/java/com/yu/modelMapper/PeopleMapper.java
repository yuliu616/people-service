package com.yu.modelMapper;

import com.yu.model.IntegerId;
import com.yu.model.people.People;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * model mapper interface for model People
 */
public interface PeopleMapper {

    People findPeopleById(@Param("id") String id);

    long generatePeopleId(@Param("o") IntegerId integerId);

    long insertPeopleWithModel(@Param("it") People People);

    long updatePeopleWithModel(@Param("it") People People);

    /**
     * @param isActive "0" for false,
     *                 "1" for true,
     *                 "-1" for anything.
     */
    List<People> listAllPeople(
            @Param("isActive") int isActive,
            @Param("pageOffset") long pageOffset,
            @Param("pageSize") long pageSize);

    /**
     * @param isActive "0" for false,
     *                 "1" for true,
     *                 "-1" for anything.
     */
    long countAllPeople(
            @Param("isActive") int isActive);

    List<People> findPeopleByIdList(@Param("idList") List<String> idList);

    /**
     * @param namePattern pattern (in regex) to search for people,
     *                    all name related fields will be searched.
     */
    List<People> findPeopleWithNameSimilarTo(
            @Param("namePattern") String namePattern,
            @Param("isActive") boolean isActive,
            @Param("pageOffset") long pageOffset,
            @Param("pageSize") long pageSize);

}
