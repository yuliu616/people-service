import { expect } from 'chai';
import { default as axios } from 'axios';

import {
  apiBaseUrl,
  base64Pattern,
  dateTimeAndMsNoZonePattern,
  dateTimeNoZonePattern,
  localDatePattern,
  numberOnlyPattern,
  generalLettersPattern,
} from './common.test';

beforeEach(async function(){
  await axios.post(`${apiBaseUrl}/debug/enableAllPeople`);
  return await axios.post(`${apiBaseUrl}/debug/resetVersionOfAllPeople`);
});

describe('people', function(){

  it('could create new people', async function(){
    let data = {
      nickname: 'pe',
      firstName: 'peter',
      lastName: 'zhang',
      gender: 'MALE',
      dateOfBirth: '1980-01-31',
      heightInCm: 171.18,
      weightInKg: 2.1,
    };

    // invoke to create people
    let res = await axios.post(`${apiBaseUrl}/people`, data);
    expect(res.data).is.an('object');
    expect(res.data.id).to.match(numberOnlyPattern).that.exist;
    expect(res.data.version).at.least(1);
    expect(res.data.creationDate).to.match(dateTimeNoZonePattern).that.exist;
    expect(res.data.lastUpdated).to.match(dateTimeNoZonePattern).that.exist;
    expect(res.data.nickname).eq('pe');
    expect(res.data.firstName).eq('peter');
    expect(res.data.lastName).eq('zhang');
    expect(res.data.gender).eq('MALE');
    expect(res.data.dateOfBirth).eq('1980-01-31');
    expect(res.data.heightInCm).eq(171.18);
    expect(res.data.weightInKg).eq(2.1);
  });

  it('reject creation if first name is missing', async function(){
    let data = {
      nickname: 'pe',
      // firstName: 'peter',
      lastName: 'zhang',
      gender: 'MALE',
      dateOfBirth: '1980-01-31',
      heightInCm: 171.18,
      weightInKg: 2.1,
    };

    // invoke to create people
    let resError = await axios.post(`${apiBaseUrl}/people`, data).then(res=>{
      return 'endpoint reject expected';
    }).catch(err=>{
      expect(err.response.status).eq(400);
      return err.response.data;
    });
    expect(resError).is.an('object');
    expect(resError.errorCode).eq('VALIDATION_ERROR');
  });

  it('reject creation if Gender value is invalid', async function(){
    let data = {
      nickname: 'pe',
      firstName: 'peter',
      lastName: 'zhang',
      gender: 'MAN',
      dateOfBirth: '1980-01-31',
      heightInCm: 171.18,
      weightInKg: 2.1,
    };

    // invoke to create people
    let resError = await axios.post(`${apiBaseUrl}/people`, data).then(res=>{
      return 'endpoint reject expected';
    }).catch(err=>{
      expect(err.response.status).eq(400);
      return err.response.data;
    });
    expect(resError).is.an('object');
    expect(resError.errorCode).eq('INVALID_USE_ERROR');
  });

  it('could find target record by id', async function(){
    // query target people
    let res = await axios.get(`${apiBaseUrl}/people/33001`);
    expect(res.data).is.an('object');
    expect(res.data.id).to.match(numberOnlyPattern).that.exist;
    expect(res.data.version).at.least(1);
    expect(res.data.creationDate).to.match(dateTimeNoZonePattern).that.exist;
    expect(res.data.lastUpdated).to.match(dateTimeNoZonePattern).that.exist;
    expect(res.data.nickname).eq('Peter');
    expect(res.data.firstName).eq('Peter');
    expect(res.data.lastName).eq('Chan');
    expect(res.data.gender).eq('MALE');
    expect(res.data.dateOfBirth).eq('1970-02-15');
    expect(res.data.heightInCm).eq(172);
    expect(res.data.weightInKg).eq(74.1);
  });

  it('reject when find target by invalid id', async function(){
    // query target people
    let resError = await axios.get(`${apiBaseUrl}/people/33001-not-exists`).then(res=>{
      return 'endpoint reject expected';
    }).catch(err=>{
      expect(err.response.status).eq(400);
      return err.response.data;
    });
    expect(resError).is.an('object');
    expect(resError.errorCode).eq('RECORD_NOT_FOUND');
  });

  it('could modify target record by id', async function(){
    // query target people
    let res = await axios.get(`${apiBaseUrl}/people/33002`);
    expect(res.data).is.an('object');
    expect(res.data.id).to.match(numberOnlyPattern).that.exist;
    expect(res.data.version).at.least(1);
    expect(res.data.creationDate).to.match(dateTimeNoZonePattern).that.exist;
    expect(res.data.lastUpdated).to.match(dateTimeNoZonePattern).that.exist;

    // modify people
    let data: any = {
      id: '33002',
      version: 1,
      nickname: 'joo',
      firstName: 'Joe',
      lastName: 'Wang',
      gender: 'MALE',
      dateOfBirth: '1999-01-31',
      heightInCm: 201.1,
      weightInKg: 80.5,
    };
    res = await axios.put(`${apiBaseUrl}/people/33002`, data);
    expect(res.data.version).eq(2);
    expect(res.data.creationDate).to.match(dateTimeNoZonePattern).that.exist;
    expect(res.data.lastUpdated).to.match(dateTimeNoZonePattern).that.exist;
    expect(res.data.nickname).eq('joo');
    expect(res.data.firstName).eq('Joe');
    expect(res.data.lastName).eq('Wang');
    expect(res.data.gender).eq('MALE');
    expect(res.data.dateOfBirth).eq('1999-01-31');
    expect(res.data.heightInCm).eq(201.1);
    expect(res.data.weightInKg).eq(80.5);

    // query target people
    res = await axios.get(`${apiBaseUrl}/people/33002`);
    expect(res.data.version).eq(2);
    expect(res.data.creationDate).to.match(dateTimeNoZonePattern).that.exist;
    expect(res.data.lastUpdated).to.match(dateTimeNoZonePattern).that.exist;
    expect(res.data.nickname).eq('joo');
    expect(res.data.firstName).eq('Joe');
    expect(res.data.lastName).eq('Wang');
    expect(res.data.gender).eq('MALE');
    expect(res.data.dateOfBirth).eq('1999-01-31');
    expect(res.data.heightInCm).eq(201.1);
    expect(res.data.weightInKg).eq(80.5);

    // modify people
    data = {
      id: '33002',
      version: 2,
      nickname: 'Jammy',
      firstName: 'Jammy',
      lastName: 'Li',
      gender: 'MALE',
      dateOfBirth: '1972-09-30',
      heightInCm: 176,
      weightInKg: 71.2,
    };
    res = await axios.put(`${apiBaseUrl}/people/33002`, data);
    expect(res.data.version).eq(3);

    // query target people
    res = await axios.get(`${apiBaseUrl}/people/33002`);
    expect(res.data.version).eq(3);
    expect(res.data.creationDate).to.match(dateTimeNoZonePattern).that.exist;
    expect(res.data.lastUpdated).to.match(dateTimeNoZonePattern).that.exist;
    expect(res.data.nickname).eq('Jammy');
    expect(res.data.firstName).eq('Jammy');
    expect(res.data.lastName).eq('Li');
    expect(res.data.gender).eq('MALE');
    expect(res.data.dateOfBirth).eq('1972-09-30');
    expect(res.data.heightInCm).eq(176);
    expect(res.data.weightInKg).eq(71.2);
  });

  it('reject modify record with inconsistent id', async function(){
    // query target people
    let res = await axios.get(`${apiBaseUrl}/people/33003`);
    expect(res.data).is.an('object');
    expect(res.data.id).to.match(numberOnlyPattern).that.exist;
    expect(res.data.version).at.least(1);
    expect(res.data.creationDate).to.match(dateTimeNoZonePattern).that.exist;
    expect(res.data.lastUpdated).to.match(dateTimeNoZonePattern).that.exist;

    // modify people
    let data: any = {
      id: '33009',
      version: 1,
      nickname: 'joo',
      firstName: 'Joe',
      lastName: 'Wang',
      gender: 'MALE',
      dateOfBirth: '1999-01-31',
      heightInCm: 201.1,
      weightInKg: 80.5,
    };
    let resError = await axios.put(`${apiBaseUrl}/people/33003`, data).then(res=>{
      return 'endpoint reject expected';
    }).catch(err=>{
      expect(err.response.status).eq(400);
      return err.response.data;
    });
    expect(resError).is.an('object');
    expect(resError.errorCode).eq('INVALID_USE_ERROR');
  });

  it('could list many record', async function(){
    // query people with paging
    let res = await axios.get(`${apiBaseUrl}/people`);
    expect(res.data).is.an('array');
    expect(res.data).to.have.length.at.least(10);
    expect(res.data[0]).is.an('object');
    expect(res.data[1]).is.an('object');
    expect(res.data[2]).is.an('object');
    expect(res.data[9]).is.an('object');
    expect(res.data[0].id).to.match(numberOnlyPattern).that.exist;
    expect(res.data[0].version).at.least(1);
    expect(res.data[0].creationDate).to.match(dateTimeNoZonePattern).that.exist;
    expect(res.data[0].lastUpdated).to.match(dateTimeNoZonePattern).that.exist;
    expect(res.data[1].firstName).to.match(generalLettersPattern).that.exist;
    expect(res.data[2].firstName).to.match(generalLettersPattern).that.exist;
    expect(res.data[9].firstName).to.match(generalLettersPattern).that.exist;
  });

  it('could list record as specific page size 2', async function(){
    // query people with paging
    let res = await axios.get(`${apiBaseUrl}/people?size=2`);
    expect(res.data).is.an('array');
    expect(res.data).to.have.length(2);
    expect(res.data[0]).is.an('object');
    expect(res.data[1]).is.an('object');
  });

  it('could list record as specific page size', async function(){
    // query people with paging
    let res = await axios.get(`${apiBaseUrl}/people?size=5`);
    expect(res.data).is.an('array');
    expect(res.data).to.have.length(5);
    expect(res.data[0]).is.an('object');
    expect(res.data[1]).is.an('object');
  });

  it('could list record as specific page offset', async function(){
    // query people with paging
    let res = await axios.get(`${apiBaseUrl}/people?offset=4&size=2`);
    expect(res.data).is.an('array');
    expect(res.data).to.have.length(2);
    expect(res.data[0]).is.an('object');
    expect(res.data[1]).is.an('object');
    expect(res.data[0].id).eq('33005');
    expect(res.data[1].id).eq('33006');
  });

});
