package com.yu.modelMapper;

import com.yu.model.IntegerId;
import com.yu.model.People;
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

    List<People> listAllPeople(
            @Param("isActive") boolean isActive,
            @Param("pageOffset") long pageOffset,
            @Param("pageSize") long pageSize);

}
