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
    let res = await axios.get(`${apiBaseUrl}/people`, {
      params: {
        size: 2
      },
    });
    expect(res.data).is.an('array');
    expect(res.data).to.have.length(2);
    expect(res.data[0]).is.an('object');
    expect(res.data[1]).is.an('object');
  });

  it('could list record as specific page size', async function(){
    // query people with paging
    let res = await axios.get(`${apiBaseUrl}/people`, {
      params: {
        size: 5
      }
    });
    expect(res.data).is.an('array');
    expect(res.data).to.have.length(5);
    expect(res.data[0]).is.an('object');
    expect(res.data[1]).is.an('object');
  });

  it('could list record as specific page offset', async function(){
    // query people with paging
    let res = await axios.get(`${apiBaseUrl}/people`, {
      params: {
        offset: 4,
        size: 2,
        creationDateMax: '2020-12-31T00:00:00Z',
      }
    });
    expect(res.data).is.an('array');
    expect(res.data).to.have.length(2);
    expect(res.data[0]).is.an('object');
    expect(res.data[1]).is.an('object');
    expect(res.data[0].id).eq('33026');
    expect(res.data[1].id).eq('33025');
  });

  it('could list with creation date range', async function(){
    // query people with paging
    let res = await axios.get(`${apiBaseUrl}/people`, {
      params: {
        size: 100,
        idMin: '33001',
        idMax: '33031',
        creationDateMin: '2020-12-20T00:00:00Z',
        creationDateMax: '2020-12-25T22:30:00Z',
      },
    });
    expect(res.data).is.an('array');
    expect(res.data).to.have.length(11);
    expect(res.data[0]).is.an('object');
    expect(res.data[1]).is.an('object');
    expect(res.data[0].id).equals('33030');
    expect(res.data[1].id).equals('33029');
  });

  it('could list with last update date range', async function(){
    // query people with paging
    let res = await axios.get(`${apiBaseUrl}/people`, {
      params: {
        size: 100,
        idMin: '33001',
        idMax: '33031',
        lastUpdatedMin: '2020-12-25T16:00:00Z',
        lastUpdatedMax: '2020-12-26T21:50:59Z',
      },
    });
    expect(res.data).is.an('array');
    expect(res.data).to.have.length(15);
    expect(res.data[0]).is.an('object');
    expect(res.data[1]).is.an('object');
    expect(res.data[0].id).equals('33030');
    expect(res.data[1].id).equals('33025');
  });

  it('could list with id range', async function(){
    // query people with paging
    let res = await axios.get(`${apiBaseUrl}/people`, {
      params: {
        size: 100,
        idMin: '33001',
        idMax: '33010',
      },
    });
    expect(res.data).is.an('array');
    expect(res.data).to.have.length(9);
    expect(res.data[0]).is.an('object');
    expect(res.data[1]).is.an('object');
    expect(res.data[0].id).equals('33009');
    expect(res.data[1].id).equals('33008');
  });

  it('could count all records', async function(){
    // count records
    let res = await axios.get(`${apiBaseUrl}/people/count`);
    expect(res.data).is.an('object');
    expect(res.data.count).at.least(10);
  });

  it('could count records with creation date range', async function(){
    // count records
    let res = await axios.get(`${apiBaseUrl}/people/count`, {
      params: {
        idMin: '33001',
        idMax: '33031',
        creationDateMin: '2020-12-20T00:00:00Z',
        creationDateMax: '2020-12-25T22:30:00Z',
      },
    });
    expect(res.data).is.an('object');
    expect(res.data.count).equals(11);
  });

  it('could count records with last update date range', async function(){
    // count records
    let res = await axios.get(`${apiBaseUrl}/people/count`, {
      params: {
        idMin: '33001',
        idMax: '33031',
        lastUpdatedMin: '2020-12-25T16:00:00Z',
        lastUpdatedMax: '2020-12-26T21:50:59Z',
      },
    });
    expect(res.data).is.an('object');
    expect(res.data.count).equals(15);
  });

  it('could count records with id range', async function(){
    // count records
    let res = await axios.get(`${apiBaseUrl}/people/count`, {
      params: {
        idMin: '33001',
        idMax: '33010',
      },
    });
    expect(res.data).is.an('object');
    expect(res.data.count).equals(9);
  });

  it('could count inactive records', async function(){
    // query people with paging
    let res = await axios.get(`${apiBaseUrl}/people/count?isActive=0`);
    expect(res.data).is.an('object');
    expect(res.data.count).at.most(0);
  });

});

describe('people search', function(){

  it('could list people with first name', async function(){
    // search people
    let res = await axios.get(`${apiBaseUrl}/people/search/withNameSimilarTo`, {
      params: {
        namePattern: '^Wang$',
        size: 4,
      },
    });
    expect(res.data).is.an('array');
    expect(res.data).to.have.length.at.least(4);
    expect(res.data[0]).is.an('object');
    expect(res.data[1]).is.an('object');
    expect(res.data[2]).is.an('object');
    expect(res.data[3]).is.an('object');
    expect(res.data[0].id).to.match(numberOnlyPattern).that.exist;
    expect(res.data[0].version).at.least(1);
    expect(res.data[0].creationDate).to.match(dateTimeNoZonePattern).that.exist;
    expect(res.data[0].lastUpdated).to.match(dateTimeNoZonePattern).that.exist;
    expect(res.data[1].lastName).eq('Wang');
    expect(res.data[2].lastName).eq('Wang');
    expect(res.data[3].lastName).eq('Wang');
  });

  it('could list people with last name', async function(){
    // search people
    let res = await axios.get(`${apiBaseUrl}/people/search/withNameSimilarTo`, {
      params: {
        namePattern: '^Zh',
        size: 4,
      },
    });
    expect(res.data).is.an('array');
    expect(res.data).to.have.length.at.least(4);
    expect(res.data[0]).is.an('object');
    expect(res.data[1]).is.an('object');
    expect(res.data[2]).is.an('object');
    expect(res.data[3]).is.an('object');
    expect(res.data[0].id).to.match(numberOnlyPattern).that.exist;
    expect(res.data[0].version).at.least(1);
    expect(res.data[0].creationDate).to.match(dateTimeNoZonePattern).that.exist;
    expect(res.data[0].lastUpdated).to.match(dateTimeNoZonePattern).that.exist;
    expect(res.data[1].lastName).eq('Zhang');
    expect(res.data[2].lastName).eq('Zhang');
    expect(res.data[3].lastName).eq('Zhang');
  });

  it('could list people by part of name', async function(){
    // search people
    let res = await axios.get(`${apiBaseUrl}/people/search/withNameSimilarTo`, {
      params: {
        creationDateMax: '2020-12-31T00:00:00Z',
        namePattern: '(mm|nn|ll)',
        size: 3,
      },
    });
    expect(res.data).is.an('array');
    expect(res.data).to.have.length.at.least(3);
    expect(res.data[0]).is.an('object');
    expect(res.data[1]).is.an('object');
    expect(res.data[2]).is.an('object');
    expect(res.data[0].id).to.match(numberOnlyPattern).that.exist;
    expect(res.data[0].version).at.least(1);
    expect(res.data[0].creationDate).to.match(dateTimeNoZonePattern).that.exist;
    expect(res.data[0].lastUpdated).to.match(dateTimeNoZonePattern).that.exist;
    expect(res.data[0].firstName).eq('Cinderella');
    expect(res.data[1].firstName).eq('Annie');
    expect(res.data[2].firstName).eq('Jammy');
  });

});
