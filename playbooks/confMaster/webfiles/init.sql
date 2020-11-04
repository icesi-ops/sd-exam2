CREATE SCHEMA IF NOT EXISTS SG;
USE SG;

DROP TABLE IF EXISTS s_customer;
CREATE TABLE s_customer 
 (id                         VARCHAR(3)  NOT NULL,
  name                       VARCHAR(20) NOT NULL,
  phone                      VARCHAR(20) NOT NULL,
  address                    VARCHAR(20),
  city                       VARCHAR(20),
  state                      VARCHAR(15),
  country                    VARCHAR(20),
  zip_code                   VARCHAR(15),
  credit_rating              VARCHAR(9),
  sales_rep_id               VARCHAR(3),
  region_id                  VARCHAR(3),
  comments                   VARCHAR(255),
  CONSTRAINT s_customer_id_pk PRIMARY KEY (id),
  CONSTRAINT s_customer_credit_rating_ck
  CHECK (credit_rating IN ('EXCELLENT', 'GOOD', 'POOR'))
 );


INSERT INTO s_customer VALUES ('301', 'iPhone 12 Pro Max', 'Apple','A14',
'512 GB', '14 MP','4000mA', 'iOS 14','EXCELLENT', '12', '1', NULL);
INSERT INTO s_customer VALUES ('302', 'Mi 10 Pro', 'Xiamoi','Snapdragon',
'256 GB', '108 MP','4400mA', 'Miui 11','EXCELLENT', '14', '1', NULL);
INSERT INTO s_customer VALUES ('303', 'Galaxi S20 ultra', 'Samsung','Exynos 990',
'512 GB', '108 MP','5000mA', 'Android','EXCELLENT', '14', '1', NULL);
INSERT INTO s_customer 
VALUES ('304', 'P40 Pro +', 'Huawei','Kirin 990', '512 GB', '50 MP',
'4000mA', 'Android','EXCELLENT', '12', '1', NULL);

INSERT INTO s_customer VALUES ('305', 'Z flip', 'Samsung','Snapdragon',
'256 GB', '12 MP','4000mA', 'Android','GOOD', '14', '1', NULL);
INSERT INTO s_customer VALUES ('306', 'Galaxy note 10', 'Samsung','Snapdragon',
'256 GB', '16 MP','4400mA', 'Android','GOOD', '12', '1', NULL);

INSERT INTO s_customer VALUES ('215', 'V50', 'Samsung','Snapdragon',
'128 GB', '16 MP', '3500mA', 'Android','POOR', '11', '5', NULL);
COMMIT;

