GRANT SELECT, INSERT, UPDATE, DELETE, TRIGGER
  ON hellodb.ppl_people TO 'ppl_dbuser'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE, TRIGGER
  ON hellodb.ppl_people_id TO 'ppl_dbuser'@'%';

FLUSH PRIVILEGES;
