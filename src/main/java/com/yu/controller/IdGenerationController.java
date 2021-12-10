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

import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class IdGenerationController {

    @Autowired
    protected PeopleMapper peopleMapper;

    @Value("${people-service.options.model.people.id-generation-strategy}")
    protected IdGenerationStrategy peopleIdGenerationStrategy;

    @Value("${people-service.options.model.people.id-prefix:#{null}}")
    protected String peopleIdPrefix;

    @Value("${people-service.options.model.people.id-suffix:#{null}}")
    protected String peopleIdSuffix;

    @Value("${people-service.options.model.people.id-base:#{0}}")
    protected long peopleIdBase;

    private SecureRandom random = new SecureRandom(); // FIXME: should add parameter of algorithm instead

    // only used in some of id generation
    protected AtomicLong idCounter = new AtomicLong();

    private static final Logger logger = LoggerFactory.getLogger(IdGenerationController.class);

    public People fillWithGeneratedId(People model){
        if (peopleIdGenerationStrategy == IdGenerationStrategy.ID_TABLE) {
            return this.fillWithGeneratedIdWithIdTable(model);
        } else if (peopleIdGenerationStrategy == IdGenerationStrategy.UUID) {
            return this.fillWithGeneratedIdWithUUID(model);
        } else if (peopleIdGenerationStrategy == IdGenerationStrategy.RANDOM) {
            return this.fillWithGeneratedIdWithRandom(model);
        } else if (peopleIdGenerationStrategy == IdGenerationStrategy.RANDOM_WITH_PREFIX) {
            return this.fillWithGeneratedIdWithRandomWithPrefix(model);
        } else if (peopleIdGenerationStrategy == IdGenerationStrategy.RANDOM_WITH_SUFFIX) {
            return this.fillWithGeneratedIdWithRandomWithSuffix(model);
        } else if (peopleIdGenerationStrategy == IdGenerationStrategy.APP_SERVER_COUNTER_WITH_PREFIX) {
            return this.fillWithGeneratedIdWithCounterWithPrefix(model);
        } else if (peopleIdGenerationStrategy == IdGenerationStrategy.APP_SERVER_COUNTER_WITH_SUFFIX) {
            return this.fillWithGeneratedIdWithCounterWithSuffix(model);
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

    private People fillWithGeneratedIdWithUUID(People model){
        String idInString = UUID.randomUUID().toString();
//        logger.debug("uuid = {}", idInString);
        model.setId(idInString);
        return model;
    }

    private People fillWithGeneratedIdWithRandom(People model){
        String idInString = String.valueOf(Math.abs(random.nextLong()));
//        logger.debug("random id = {}", idInString);
        model.setId(idInString);
        return model;
    }

    private People fillWithGeneratedIdWithRandomWithPrefix(People model){
        String idInString = (this.peopleIdPrefix == null ? "" : this.peopleIdPrefix)
                + String.valueOf(Math.abs(random.nextLong()));
        logger.debug("random (+prefix) id = {}", idInString);
        model.setId(idInString);
        return model;
    }

    private People fillWithGeneratedIdWithRandomWithSuffix(People model){
        String idInString = String.valueOf(Math.abs(random.nextLong()))
                + (this.peopleIdSuffix == null ? "" : this.peopleIdSuffix);
        logger.debug("random (+suffix) id = {}", idInString);
        model.setId(idInString);
        return model;
    }

    private People fillWithGeneratedIdWithCounterWithPrefix(People model){
        String idInString = (this.peopleIdPrefix == null ? "" : this.peopleIdPrefix)
                + String.valueOf(this.peopleIdBase + this.idCounter.incrementAndGet());
        logger.debug("counter (+prefix) id = {}", idInString);
        model.setId(idInString);
        return model;
    }

    private People fillWithGeneratedIdWithCounterWithSuffix(People model){
        String idInString = String.valueOf(this.peopleIdBase + this.idCounter.incrementAndGet())
                + (this.peopleIdSuffix == null ? "" : this.peopleIdSuffix);
        logger.debug("counter (+suffix) id = {}", idInString);
        model.setId(idInString);
        return model;
    }

}
