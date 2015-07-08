/*
Navicat Oracle Data Transfer
Oracle Client Version : 11.2.0.2.0

Source Server         : faropenfire
Source Server Version : 100200
Source Host           : 222.197.183.195:1521
Source Schema         : MUXMPP

Target Server Type    : ORACLE
Target Server Version : 100200
File Encoding         : 65001

Date: 2014-06-04 16:22:27
*/


-- ----------------------------
-- Table structure for "MUXMPP"."OFCHATLOGS"
-- ----------------------------
DROP TABLE "MUXMPP"."OFCHATLOGS";
CREATE TABLE "MUXMPP"."OFCHATLOGS" (
"MESSAGEID" NUMBER(11) NOT NULL ,
"SENDER" VARCHAR2(30 BYTE) NOT NULL ,
"RECEIVER" VARCHAR2(30 BYTE) NOT NULL ,
"CREATEDATE" VARCHAR2(30 BYTE) NOT NULL ,
"LENGTH" NUMBER(11) NOT NULL ,
"CONTENT" VARCHAR2(1000 BYTE) NULL ,
"STATE" NUMBER(1) NOT NULL 
)
LOGGING
NOCOMPRESS
NOCACHE

;

-- ----------------------------
-- Records of OFCHATLOGS
-- ----------------------------
INSERT INTO "MUXMPP"."OFCHATLOGS" VALUES ('10', '201322060541', '201321060318', '2014-05-27 19:57:22.816', '5', '恩 不错 ', '0');
INSERT INTO "MUXMPP"."OFCHATLOGS" VALUES ('5', '201321060329', '201322060541', '2014-05-27 15:40:57.955', '12', 'football.flv@C:\uploadFile\201321060329\football.flv', '1');
INSERT INTO "MUXMPP"."OFCHATLOGS" VALUES ('13', '201321060318', '201121060422', '2014-05-29 12:06:45.353', '3', '胖师姐', '0');

INSERT INTO "MUXMPP"."OFCHATLOGS" VALUES ('28', '201321060319', '201322060541', '2014-05-29 12:06:45.353', '12', 'football.flv@C:\uploadFile\201321060319\football.flv', '1');
INSERT INTO "MUXMPP"."OFCHATLOGS" VALUES ('29', '201321060319', '201322060541', '2014-05-29 12:06:45.353', '7', 'football.flv@C:\uploadFile\201321060319\abc.txt', '1');
INSERT INTO "MUXMPP"."OFCHATLOGS" VALUES ('30', '201321060319', '201322060541', '2014-05-29 12:06:45.353', '7', 'football.flv@C:\uploadFile\201321060319\psb.jpg', '1');
-- ----------------------------
-- Indexes structure for table OFCHATLOGS
-- ----------------------------

-- ----------------------------
-- Checks structure for table "MUXMPP"."OFCHATLOGS"
-- ----------------------------
ALTER TABLE "MUXMPP"."OFCHATLOGS" ADD CHECK ("MESSAGEID" IS NOT NULL);
ALTER TABLE "MUXMPP"."OFCHATLOGS" ADD CHECK ("SENDER" IS NOT NULL);
ALTER TABLE "MUXMPP"."OFCHATLOGS" ADD CHECK ("RECEIVER" IS NOT NULL);
ALTER TABLE "MUXMPP"."OFCHATLOGS" ADD CHECK ("CREATEDATE" IS NOT NULL);
ALTER TABLE "MUXMPP"."OFCHATLOGS" ADD CHECK ("LENGTH" IS NOT NULL);
ALTER TABLE "MUXMPP"."OFCHATLOGS" ADD CHECK ("STATE" IS NOT NULL);

-- ----------------------------
-- Primary Key structure for table "MUXMPP"."OFCHATLOGS"
-- ----------------------------
ALTER TABLE "MUXMPP"."OFCHATLOGS" ADD PRIMARY KEY ("MESSAGEID");
