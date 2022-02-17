package com.yu.controller;

import com.yu.exception.RecordInsertionFailException;
import com.yu.exception.UnhandledException;
import com.yu.model.IntegerId;
import com.yu.model.config.IdGenerationStrategy;
import com.yu.model.people.People;
import com.yu.modelMapper.PeopleMapper;
import com.yu.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.util.UUID;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class IdGenerationController {

    protected SecureRandom random = new SecureRandom(); // FIXME: should add parameter of algorithm instead

    @Autowired
    protected PeopleMapper peopleMapper;

    @Autowired
    protected CurrentTimeController currentTimeController;

    @Value("${people-service.options.model.people.id-generation-strategy}")
    protected IdGenerationStrategy peopleIdGenerationStrategy;

    @Value("${people-service.options.model.people.id-prefix:#{null}}")
    protected String peopleIdPrefix;

    protected String peopleIdRandPrefix = StringUtil.int2Str(this.random.nextInt(10000), 4);

    @Value("${people-service.options.model.people.id-suffix:#{null}}")
    protected String peopleIdSuffix;

    @Value("${people-service.options.model.people.id-base:#{0}}")
    protected long peopleIdBase;

    @Value("${people-service.options.model.people.length-of-rand:#{6}}")
    protected int peopleIdLenOfRand;

    /**
     * (for IdGenerationStrategy.ID_TABLE_CROSSING_COUNTER)
     */
    private Semaphore peopleIdTableLock = new Semaphore(1);

    /**
     * for each id generated by the ID table,
     * it will be multiplied by this factor first,
     * then, adding the Counter.
     * (that means, one row in ID table could produce many ID)
     * (for IdGenerationStrategy.ID_TABLE_CROSSING_COUNTER)
     */
    @Value("${people-service.options.model.people.factor-for-crossing:#{1000000}}")
    protected long peopleIdFactorForIdTableCrossingCounter;

    /**
     * (for IdGenerationStrategy.ID_TABLE_CROSSING_COUNTER)
     */
    protected Long idTableLastGenerated = null;

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
        } else if (peopleIdGenerationStrategy == IdGenerationStrategy.APP_SERVER_COUNTER_WITH_RANDOM_PREFIX) {
            return this.fillWithGeneratedIdWithCounterWithRandPrefix(model);
        } else if (peopleIdGenerationStrategy == IdGenerationStrategy.TIME_BASED_SEQ_WITH_RANDOM_SUFFIX) {
            return this.fillWithGeneratedIdWithTimeBasedSeqWithRandomSuffix(model);
        } else if (peopleIdGenerationStrategy == IdGenerationStrategy.ID_TABLE_CROSSING_COUNTER) {
            return this.fillWithGeneratedIdWithIdTableCrossCounter(model);
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
//        logger.debug("random (+prefix) id = {}", idInString);
        model.setId(idInString);
        return model;
    }

    private People fillWithGeneratedIdWithRandomWithSuffix(People model){
        String idInString = String.valueOf(Math.abs(random.nextLong()))
                + (this.peopleIdSuffix == null ? "" : this.peopleIdSuffix);
//        logger.debug("random (+suffix) id = {}", idInString);
        model.setId(idInString);
        return model;
    }

    private People fillWithGeneratedIdWithCounterWithPrefix(People model){
        String idInString = (this.peopleIdPrefix == null ? "" : this.peopleIdPrefix)
                + String.valueOf(this.peopleIdBase + this.idCounter.incrementAndGet());
//        logger.debug("counter (+prefix) id = {}", idInString);
        model.setId(idInString);
        return model;
    }

    private People fillWithGeneratedIdWithCounterWithSuffix(People model){
        String idInString = String.valueOf(this.peopleIdBase + this.idCounter.incrementAndGet())
                + (this.peopleIdSuffix == null ? "" : this.peopleIdSuffix);
//        logger.debug("counter (+suffix) id = {}", idInString);
        model.setId(idInString);
        return model;
    }

    private People fillWithGeneratedIdWithCounterWithRandPrefix(People model){
        StringBuilder buffer = new StringBuilder();
        if (this.peopleIdPrefix != null) {
            buffer.append(this.peopleIdPrefix);
        }
        buffer.append(this.peopleIdRandPrefix);
        if (this.peopleIdSuffix != null) {
            buffer.append(this.peopleIdSuffix);
        }
        buffer.append(this.peopleIdBase + this.idCounter.incrementAndGet());

        String idInString = buffer.toString();
//        logger.debug("counter (+rand prefix) id = {}", idInString);
        model.setId(idInString);
        return model;
    }

    private People fillWithGeneratedIdWithTimeBasedSeqWithRandomSuffix(People model){
        StringBuilder result = new StringBuilder();
        if (this.peopleIdPrefix != null) {
            result.append(this.peopleIdPrefix);
        }
        LocalDateTime now = LocalDateTime.ofInstant(currentTimeController.getCurrentTime(), ZoneOffset.UTC);
//        logger.debug("now = {}/{}/{} {}:{}:{} nano={}",
//                now.get(ChronoField.YEAR),
//                now.get(ChronoField.MONTH_OF_YEAR),
//                now.get(ChronoField.DAY_OF_MONTH),
//                now.get(ChronoField.HOUR_OF_DAY),
//                now.get(ChronoField.MINUTE_OF_HOUR),
//                now.get(ChronoField.SECOND_OF_MINUTE),
//                now.get(ChronoField.NANO_OF_SECOND));
        result.append(StringUtil.int2Str(now.get(ChronoField.YEAR), 4));
        result.append(StringUtil.int2Str(now.get(ChronoField.MONTH_OF_YEAR), 2));
        result.append(StringUtil.int2Str(now.get(ChronoField.DAY_OF_MONTH), 2));
        result.append(StringUtil.int2Str(now.get(ChronoField.HOUR_OF_DAY), 2));
        result.append(StringUtil.int2Str(now.get(ChronoField.MINUTE_OF_HOUR), 2));
        result.append(StringUtil.int2Str(now.get(ChronoField.SECOND_OF_MINUTE), 2));
        result.append(StringUtil.int2Str(now.get(ChronoField.NANO_OF_SECOND), 9));
        if (peopleIdLenOfRand > 9) {
            throw new RuntimeException("peopleIdLenOfRand not supported (too large): "+peopleIdLenOfRand);
        }
        long rand;
        if (peopleIdLenOfRand == 1) {
            rand = random.nextInt(10);
        } else if (peopleIdLenOfRand == 2) {
            rand = random.nextInt(100);
        } else if (peopleIdLenOfRand == 3) {
            rand = random.nextInt(1000);
        } else if (peopleIdLenOfRand == 4) {
            rand = random.nextInt(10000);
        } else if (peopleIdLenOfRand == 5) {
            rand = random.nextInt(100000);
        } else if (peopleIdLenOfRand == 6) {
            rand = random.nextInt(1000000);
        } else if (peopleIdLenOfRand == 7) {
            rand = random.nextInt(10000000);
        } else if (peopleIdLenOfRand == 8) {
            rand = random.nextInt(100000000);
        } else {
            int boundOfRand = 1;
            for (int i=0;i<peopleIdLenOfRand;i++) {
                boundOfRand *= 10;
            }
            rand = random.nextInt(boundOfRand);
        }
        result.append(StringUtil.int2Str(rand, peopleIdLenOfRand));
        String idInString = result.toString();
//        logger.debug("time+rand id = {}", idInString);
        model.setId(idInString);
        return model;
    }

    /**
     * thread-safe
     */
    private People fillWithGeneratedIdWithIdTableCrossCounter(People model) {
        // get/manage idTableLastGenerated and idCounter (with thread-safe)
        long idTableLastGen;
        long counterValue;
        try {
            peopleIdTableLock.acquire();
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
        try {
            if (idTableLastGenerated == null) {
                logger.debug("askIdTableToSpawn for init.");
                idTableLastGenerated = askIdTableToSpawn();
                idCounter.set(0L);
            }

            // ensuring idCounter within bound
            idTableLastGen = idTableLastGenerated;
            counterValue = idCounter.incrementAndGet();
            if (counterValue > peopleIdFactorForIdTableCrossingCounter) {
                logger.debug("counter reach upper bound, askIdTableToSpawn.");
                idTableLastGenerated = askIdTableToSpawn();
                idCounter.set(0L);
                idTableLastGen = idTableLastGenerated;
                counterValue = idCounter.incrementAndGet();
            }
        } finally {
            peopleIdTableLock.release();
        }

        model.setId(Long.toString(
                idTableLastGen * peopleIdFactorForIdTableCrossingCounter
                   +counterValue));
//        logger.debug("fillWithGeneratedIdWithIdTableCrossCounter got new ID = {}", model.getId());
        return model;
    }

    private long askIdTableToSpawn(){
        IntegerId integerId = new IntegerId();
        long affected = peopleMapper.generatePeopleId(integerId);
        if (affected <= 0) {
            throw new RecordInsertionFailException();
        }
        long newGen = integerId.getId();
        logger.debug("ID table generated: {}.", newGen);
        return newGen;
    }

}
