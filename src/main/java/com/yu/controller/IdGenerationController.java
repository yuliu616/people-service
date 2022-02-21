package com.yu.controller;

import com.yu.exception.RecordInsertionFailException;
import com.yu.exception.UnhandledException;
import com.yu.model.IntegerId;
import com.yu.model.config.IdGenerationStrategy;
import com.yu.model.people.People;
import com.yu.modelMapper.PeopleMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class IdGenerationController {

    @Autowired
    protected PeopleMapper peopleMapper;

    @Value("${people-service.options.model.people.id-generation-strategy}")
    protected IdGenerationStrategy peopleIdGenerationStrategy;

    private static final Logger logger = LoggerFactory.getLogger(IdGenerationController.class);

    public People fillWithGeneratedId(People model){
        if (peopleIdGenerationStrategy == IdGenerationStrategy.ID_TABLE) {
            return this.fillWithGeneratedIdWithIdTable(model);
        } else {
            throw new UnhandledException("unknown IdGenerationStrategy: "+this.peopleIdGenerationStrategy);
        }
    }

    private People fillWithGeneratedIdWithIdTable(People model){
        IntegerId integerId = new IntegerId();
        long affected = peopleMapper.generatePeopleId(integerId);
        if (affected <= 0) {
            throw new RecordInsertionFailException();
        }
        String idInString = String.valueOf(integerId.getId());
        model.setId(idInString);
        return model;
    }

}
