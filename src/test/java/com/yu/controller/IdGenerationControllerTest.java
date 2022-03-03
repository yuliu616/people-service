package com.yu.controller;

import com.yu.exception.RecordInsertionFailException;
import com.yu.model.IntegerId;
import com.yu.model.config.IdGenerationStrategy;
import com.yu.model.people.People;
import com.yu.modelMapper.PeopleMapper;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.time.temporal.ChronoUnit;

import static org.easymock.EasyMock.*;

public class IdGenerationControllerTest {

    private IdGenerationController controller;

    private static Logger logger = LoggerFactory.getLogger(IdGenerationControllerTest.class);

    @Before
    public void setup(){
        controller = new IdGenerationController();
        controller.peopleMapper = mock(PeopleMapper.class);
        controller.currentTimeController = mock(CurrentTimeController.class);
        controller.random = mock(SecureRandom.class);
        controller.idCounter.set(8899L); // AtomicLong could not be mocked
    }

    @Test
    public void genIdUsingIdTableErrorOnIdTableFail(){
        controller.peopleIdGenerationStrategy = IdGenerationStrategy.ID_TABLE;

        // define expectations
        expect(
            controller.peopleMapper.generatePeopleId(EasyMock.anyObject())
        ).andReturn(0L);
        EasyMock.replay(
            controller.peopleMapper
        );

        // trigger test
        People people = new People();
        try {
            controller.fillWithGeneratedId(people);
            logger.debug("model.people = {}", people.getId());
            assert false; // not expected to reach here
        } catch (RecordInsertionFailException ex) {
            assert ex instanceof RecordInsertionFailException; // this exception is expected
        }

        // verify mocked objects
        EasyMock.verify(
                controller.peopleMapper
        );
    }

    @Test
    public void genIdUsingIdTableWork(){
        controller.peopleIdGenerationStrategy = IdGenerationStrategy.ID_TABLE;

        // define expectations
        expect(
            controller.peopleMapper.generatePeopleId(EasyMock.anyObject())
        ).andAnswer(()->{
            IntegerId arg0 = (IntegerId)EasyMock.getCurrentArguments()[0];
            arg0.setId(333501L);
            return 1L;
        }).once();
        EasyMock.replay(
            controller.peopleMapper
        );

        // trigger test
        People people = new People();
        controller.fillWithGeneratedId(people);
        logger.debug("model.people = {}", people.getId());

        // verify mocked objects
        EasyMock.verify(
            controller.peopleMapper
        );

        assert(people.getId()).equals("333501");
    }

    @Test
    public void genIdUsingRandomWork(){
        controller.peopleIdGenerationStrategy = IdGenerationStrategy.RANDOM;

        // define expectations
        expect(
            controller.random.nextLong()
        ).andReturn(123008L);
        EasyMock.expectLastCall().once();
        EasyMock.replay(
            controller.random
        );

        // trigger test
        People people = new People();
        controller.fillWithGeneratedId(people);
        logger.debug("model.people = {}", people.getId());

        // verify mocked objects
        EasyMock.verify(
            controller.random
        );

        assert(people.getId()).equals("123008");
    }

    @Test
    public void genIdUsingRandomWithPrefixWork(){
        controller.peopleIdGenerationStrategy = IdGenerationStrategy.RANDOM_WITH_PREFIX;
        controller.peopleIdPrefix = "ABC-";

        // define expectations
        expect(
            controller.random.nextLong()
        ).andReturn(123008L);
        EasyMock.expectLastCall().once();
        EasyMock.replay(
            controller.random
        );

        // trigger test
        People people = new People();
        controller.fillWithGeneratedId(people);
        logger.debug("model.people = {}", people.getId());

        // verify mocked objects
        EasyMock.verify(
            controller.random
        );

        assert(people.getId()).equals("ABC-123008");
    }

    @Test
    public void genIdUsingRandomWithSuffixWork(){
        controller.peopleIdGenerationStrategy = IdGenerationStrategy.RANDOM_WITH_SUFFIX;
        controller.peopleIdSuffix = "/RXV";

        // define expectations
        expect(
            controller.random.nextLong()
        ).andReturn(123008L);
        EasyMock.expectLastCall().once();
        EasyMock.replay(
            controller.random
        );

        // trigger test
        People people = new People();
        controller.fillWithGeneratedId(people);
        logger.debug("model.people = {}", people.getId());

        // verify mocked objects
        EasyMock.verify(
            controller.random
        );

        assert(people.getId()).equals("123008/RXV");
    }

    @Test
    public void genIdUsingCounterWithPrefixWork(){
        controller.peopleIdGenerationStrategy = IdGenerationStrategy.APP_SERVER_COUNTER_WITH_PREFIX;
        controller.peopleIdPrefix = "XYZ-";
        controller.peopleIdBase = 2510000;

        // trigger test
        People people1 = new People();
        controller.fillWithGeneratedId(people1);
        logger.debug("model.people1 = {}", people1.getId());

        People people2 = new People();
        controller.fillWithGeneratedId(people2);
        logger.debug("model.people2 = {}", people2.getId());

        assert(people1.getId()).equals("XYZ-2518900");
        assert(people2.getId()).equals("XYZ-2518901");
    }

    @Test
    public void genIdUsingCounterWithSuffixWork(){
        controller.peopleIdGenerationStrategy = IdGenerationStrategy.APP_SERVER_COUNTER_WITH_SUFFIX;
        controller.peopleIdSuffix = "/LQX";
        controller.peopleIdBase = 2510000;

        // trigger test
        People people1 = new People();
        controller.fillWithGeneratedId(people1);
        logger.debug("model.people1 = {}", people1.getId());

        People people2 = new People();
        controller.fillWithGeneratedId(people2);
        logger.debug("model.people2 = {}", people2.getId());

        assert(people1.getId()).equals("2518900/LQX");
        assert(people2.getId()).equals("2518901/LQX");
    }

    @Test
    public void genIdUsingCounterWithRandPrefixWork(){
        controller.peopleIdGenerationStrategy = IdGenerationStrategy.APP_SERVER_COUNTER_WITH_RANDOM_PREFIX;
        controller.peopleIdPrefix = "XYZ-";
        controller.peopleIdRandPrefix = "555898";
        controller.peopleIdSuffix = "/LQX-";
        controller.peopleIdBase = 2240000;

        // trigger test
        People people1 = new People();
        controller.fillWithGeneratedId(people1);
        logger.debug("model.people1 = {}", people1.getId());

        People people2 = new People();
        controller.fillWithGeneratedId(people2);
        logger.debug("model.people2 = {}", people2.getId());

        assert(people1.getId()).equals("XYZ-555898/LQX-2248900");
        assert(people2.getId()).equals("XYZ-555898/LQX-2248901");
    }

    @Test
    public void genIdUsingCurrentTimeAndRandomWork(){
        controller.peopleIdGenerationStrategy = IdGenerationStrategy.TIME_BASED_SEQ_WITH_RANDOM_SUFFIX;
        controller.peopleIdPrefix = "ABC-";
        controller.peopleIdLenOfRand = 2;

        // define expectations
        expect(
            controller.currentTimeController.getCurrentTime()
        ).andReturn(
            java.time.Instant.parse("2019-12-31T23:58:59Z")
                .plus(333888555L, ChronoUnit.NANOS)
        );
        EasyMock.expectLastCall().once();
        expect(
            controller.random.nextInt(100)
        ).andReturn(76);
        EasyMock.replay(
            controller.currentTimeController,
            controller.random
        );

        // trigger test
        People people = new People();
        controller.fillWithGeneratedId(people);
        logger.debug("model.people = {}", people.getId());

        // verify mocked objects
        EasyMock.verify(
            controller.currentTimeController,
            controller.random
        );

        assert(people.getId()).equals("ABC-2019123123585933388855576");
    }

    @Test
    public void genIdUsingIdTableCrossingCounterWork(){
        controller.peopleIdGenerationStrategy = IdGenerationStrategy.ID_TABLE_CROSSING_COUNTER;
        controller.peopleIdFactorForIdTableCrossingCounter = 10L;

        // define expectations
        expect(
            controller.peopleMapper.generatePeopleId(EasyMock.anyObject())
        ).andAnswer(()->{
            IntegerId arg0 = (IntegerId)EasyMock.getCurrentArguments()[0];
            arg0.setId(80124L);
            return 1L;
        }).once();
        expect(
            controller.peopleMapper.generatePeopleId(EasyMock.anyObject())
        ).andAnswer(()->{
            IntegerId arg0 = (IntegerId)EasyMock.getCurrentArguments()[0];
            arg0.setId(80332L);
            return 1L;
        }).once();
        EasyMock.replay(
            controller.peopleMapper
        );

        // trigger test
        People people1 = new People();
        controller.fillWithGeneratedId(people1);
        logger.debug("model.people1 = {}", people1.getId());

        People people2 = new People();
        controller.fillWithGeneratedId(people2);
        logger.debug("model.people2 = {}", people2.getId());

        People people3 = new People();
        controller.fillWithGeneratedId(people3);
        logger.debug("model.people3 = {}", people3.getId());

        People people4 = new People();
        controller.fillWithGeneratedId(people4);
        logger.debug("model.people4 = {}", people4.getId());

        People people5 = new People();
        controller.fillWithGeneratedId(people5);
        logger.debug("model.people5 = {}", people5.getId());

        People people6 = new People();
        controller.fillWithGeneratedId(people6);
        logger.debug("model.people6 = {}", people6.getId());

        People people7 = new People();
        controller.fillWithGeneratedId(people7);
        logger.debug("model.people7 = {}", people7.getId());

        People people8 = new People();
        controller.fillWithGeneratedId(people8);
        logger.debug("model.people8 = {}", people8.getId());

        People people9 = new People();
        controller.fillWithGeneratedId(people9);
        logger.debug("model.people9 = {}", people9.getId());

        People people10 = new People();
        controller.fillWithGeneratedId(people10);
        logger.debug("model.people10 = {}", people10.getId());

        People people11 = new People();
        controller.fillWithGeneratedId(people11);
        logger.debug("model.people11 = {}", people11.getId());

        People people12 = new People();
        controller.fillWithGeneratedId(people12);
        logger.debug("model.people12 = {}", people12.getId());

        assert(people1.getId()).equals("801241");
        assert(people2.getId()).equals("801242");
        assert(people3.getId()).equals("801243");
        assert(people4.getId()).equals("801244");
        assert(people5.getId()).equals("801245");
        assert(people6.getId()).equals("801246");
        assert(people7.getId()).equals("801247");
        assert(people8.getId()).equals("801248");
        assert(people9.getId()).equals("801249");
        assert(people10.getId()).equals("801250");
        assert(people11.getId()).equals("803321");
        assert(people12.getId()).equals("803322");

        // verify mocked objects
        EasyMock.verify(
            controller.peopleMapper
        );
    }

}
