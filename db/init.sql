CREATE DATABASE cellphones;
use cellphones;

CREATE TABLE cellphones_table (
  name VARCHAR(20),
  brand  VARCHAR(10)
);

INSERT INTO cellphones_table
  (name, brand)
VALUES
  ('Note 20', 'Samsung'),
  ('P30 pro', 'Huawei'),
  ('F2 pro', 'Pocophone'),
  ('Iphone xs', 'Apple');
