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
  enumLikePattern,
} from './common.test';

beforeEach(async function(){
});

describe('dictionary', function(){

  it('could list Gender', async function(){
    // query product with paging
    let res = await axios.get(`${apiBaseUrl}/dict/Gender`);
    expect(res.data).is.an('array');
    expect(res.data).to.have.length.at.least(2);
    expect(res.data[0]).to.match(enumLikePattern).that.exist;
    expect(res.data[1]).to.match(enumLikePattern).that.exist;
  });

});
