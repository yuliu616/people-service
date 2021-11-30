package com.yu.controller;

import com.yu.exception.InconsistencyDataException;
import com.yu.exception.RecordInsertionFailException;
import com.yu.exception.RecordModificationFailException;
import com.yu.model.dto.CountDto;
import com.yu.model.people.People;
import com.yu.modelMapper.PeopleMapper;
import com.yu.util.MyBatisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.Arrays;
import java.util.List;

@RequestMapping("${people-service.api-base-url}/people")
@RestController
public class PeopleController {

    @Autowired
    protected PeopleMapper peopleMapper;

    @Autowired
    protected IdGenerationController idGenerationController;

    private static final long PAGE_SIZE_MIN = 1;
    private static final long PAGE_SIZE_SAFE_LIMIT = 1000;

    private static final Logger logger = LoggerFactory.getLogger(PeopleController.class);

    @GetMapping("/{id}")
    public People findPeopleById(@PathVariable("id") String id){
        return MyBatisUtil.valueOrRecordNotFoundErr(this.peopleMapper.findPeopleById(id));
    }

    @GetMapping("")
    public List<People> listAllPeople(
            @RequestParam(value = "offset", defaultValue = "0") long offset,
            @RequestParam(value = "size", defaultValue = "10") long size,
            @RequestParam(value = "isActive", defaultValue = "1") int isActive
    ){
        long safePageSize = Math.max(Math.min(size, PAGE_SIZE_SAFE_LIMIT), PAGE_SIZE_MIN);
        return this.peopleMapper.listAllPeople(isActive, offset, safePageSize);
    }

    @GetMapping("/count")
    public CountDto countAllPeople(
            @RequestParam(value = "isActive", defaultValue = "1") int isActive
    ){
        return new CountDto(this.peopleMapper.countAllPeople(isActive));
    }

    @GetMapping("/search/withIdList")
    public List<People> listPeopleByIdList(
            @RequestParam("idList") String idListStr
    ){
        List<String> idList = Arrays.asList(idListStr.split(","));
        if (idList.size() > PAGE_SIZE_SAFE_LIMIT) {
            throw new ValidationException("idList too long");
        }
        return this.peopleMapper.findPeopleByIdList(idList);
    }

    @GetMapping("/search/withNameSimilarTo")
    public List<People> findPeopleWithNameSimilar(
            @RequestParam(value = "namePattern", required = true) String namePattern,
            @RequestParam(value = "offset", defaultValue = "0") long offset,
            @RequestParam(value = "size", defaultValue = "10") long size
    ){
        long safePageSize = Math.max(Math.min(size, PAGE_SIZE_SAFE_LIMIT), PAGE_SIZE_MIN);
        return this.peopleMapper.findPeopleWithNameSimilarTo(namePattern, true, offset, safePageSize);
    }

    @Transactional
    @PostMapping("")
    public People createPeople(@RequestBody @Valid People people){
        People newPeople = idGenerationController.fillWithGeneratedId(people);
        newPeople.setActive(true); // record is created active by default
        long created = this.peopleMapper.insertPeopleWithModel(newPeople);
        if (created <= 0) {
            throw new RecordInsertionFailException();
        }
        return this.findPeopleById(newPeople.getId());
    }

    @Transactional
    @PutMapping("/{id}")
    public People updatePeopleById(@PathVariable("id") String id,
                                   @RequestBody @Valid People people)
    {
        if (!id.equals(people.getId())) {
            throw new InconsistencyDataException();
        }

        long affected = this.peopleMapper.updatePeopleWithModel(people);
        if (affected <= 0) {
            throw new RecordModificationFailException();
        }
        return this.peopleMapper.findPeopleById(id);
    }

}
