-- ORACLE VERSION
-- Crea temporal de codigos de tres digitos
 
-- Elimina la tabla temporal si existe c_country3
DROP TABLE "ADEMPIERE"."C_COUNTRY3" ;

-- ADEMPIERE.C_COUNTRY3 definition

CREATE TABLE "ADEMPIERE"."C_COUNTRY3" 
   (	"AD_CLIENT_ID" NUMBER(10,0) NOT NULL ENABLE, 
	"AD_ORG_ID" NUMBER(10,0) NOT NULL ENABLE, 
	"C_COUNTRY3_ID" NUMBER(10,0) NOT NULL ENABLE, 
	"C_COUNTRY3_UU" VARCHAR2(36 CHAR) DEFAULT NULL, 
	"COUNTRYCODE" VARCHAR2(2 CHAR) NOT NULL ENABLE, 
	"COUNTRYCODE3" VARCHAR2(3 CHAR) DEFAULT NULL, 
	"CREATED" DATE DEFAULT SYSDATE NOT NULL ENABLE, 
	"CREATEDBY" NUMBER(10,0) NOT NULL ENABLE, 
	"DESCRIPTION" VARCHAR2(255 CHAR) DEFAULT NULL, 
	"NAME" VARCHAR2(60 CHAR) NOT NULL ENABLE, 
	"UPDATED" DATE DEFAULT SYSDATE NOT NULL ENABLE, 
	"UPDATEDBY" NUMBER(10,0) NOT NULL ENABLE, 
	"ISACTIVE" CHAR(1) DEFAULT 'Y' NOT NULL ENABLE, 
	"C_COUNTRY_ID" NUMBER(10,0), 
	 CONSTRAINT "C_COUNTRY3_KEY" PRIMARY KEY ("C_COUNTRY3_ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE, 
	 CONSTRAINT "C_COUNTRY3_UU_IDX" UNIQUE ("C_COUNTRY3_UU")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE, 
	 CHECK (IsActive IN ('Y','N')) ENABLE, 
	 CONSTRAINT "ADCLIENT_CCOUNTRY3" FOREIGN KEY ("AD_CLIENT_ID")
	  REFERENCES "ADEMPIERE"."AD_CLIENT" ("AD_CLIENT_ID") DEFERRABLE INITIALLY DEFERRED ENABLE, 
	 CONSTRAINT "ADORG_CCOUNTRY3" FOREIGN KEY ("AD_ORG_ID")
	  REFERENCES "ADEMPIERE"."AD_ORG" ("AD_ORG_ID") DEFERRABLE INITIALLY DEFERRED ENABLE, 
	 CONSTRAINT "CREATEDBY_CCOUNTRY3" FOREIGN KEY ("CREATEDBY")
	  REFERENCES "ADEMPIERE"."AD_USER" ("AD_USER_ID") DEFERRABLE INITIALLY DEFERRED ENABLE, 
	 CONSTRAINT "UPDATEDBY_CCOUNTRY3" FOREIGN KEY ("UPDATEDBY")
	  REFERENCES "ADEMPIERE"."AD_USER" ("AD_USER_ID") DEFERRABLE INITIALLY DEFERRED ENABLE
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;

CREATE UNIQUE INDEX "ADEMPIERE"."C_COUNTRY3_KEY" ON "ADEMPIERE"."C_COUNTRY3" ("C_COUNTRY3_ID") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
  CREATE UNIQUE INDEX "ADEMPIERE"."C_COUNTRY3_UU_IDX" ON "ADEMPIERE"."C_COUNTRY3" ("C_COUNTRY3_UU") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;