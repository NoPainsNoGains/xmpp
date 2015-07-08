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

Date: 2014-05-22 21:12:32
*/


-- ----------------------------
-- Table structure for "MUXMPP"."OFPROPERTY"
-- ----------------------------
DROP TABLE "MUXMPP"."OFPROPERTY";
CREATE TABLE "MUXMPP"."OFPROPERTY" (
"NAME" VARCHAR2(100 BYTE) NOT NULL ,
"PROPVALUE" VARCHAR2(4000 BYTE) NOT NULL 
)
LOGGING
NOCOMPRESS
NOCACHE

;

-- ----------------------------
-- Records of OFPROPERTY
-- ----------------------------
INSERT INTO "MUXMPP"."OFPROPERTY" VALUES ('jdbcProvider.connectionString', 'jdbc:oracle:thin:muxmpp/muxmpp@222.197.183.195:1521:mobiledb1');
INSERT INTO "MUXMPP"."OFPROPERTY" VALUES ('jdbcProvider.driver', 'oracle.jdbc.driver.OracleDriver');
INSERT INTO "MUXMPP"."OFPROPERTY" VALUES ('jdbcProvider.passwordSQL', 'select encryptedPassword from OFUSER where username=?');
INSERT INTO "MUXMPP"."OFPROPERTY" VALUES ('jdbcProvider.passwordType', 'md5');
INSERT INTO "MUXMPP"."OFPROPERTY" VALUES ('passwordKey', 'k4bHrljVUI3i09w');
INSERT INTO "MUXMPP"."OFPROPERTY" VALUES ('xmpp.socket.ssl.active', 'true');
INSERT INTO "MUXMPP"."OFPROPERTY" VALUES ('xmpp.domain', 'i8058');
INSERT INTO "MUXMPP"."OFPROPERTY" VALUES ('provider.auth.className', 'org.jivesoftware.openfire.login.MyselfAuthProvider');
INSERT INTO "MUXMPP"."OFPROPERTY" VALUES ('xmpp.auth.anonymous', 'true');
INSERT INTO "MUXMPP"."OFPROPERTY" VALUES ('xmpp.auth.sharedSecretEnabled', 'true');
INSERT INTO "MUXMPP"."OFPROPERTY" VALUES ('fastpath.database.setup', 'true');
INSERT INTO "MUXMPP"."OFPROPERTY" VALUES ('cache.KrakenSessionLocationCache.maxLifetime', '-1');
INSERT INTO "MUXMPP"."OFPROPERTY" VALUES ('cache.KrakenSessionLocationCache.min', '-1');
INSERT INTO "MUXMPP"."OFPROPERTY" VALUES ('cache.KrakenRegistrationCache.min', '-1');
INSERT INTO "MUXMPP"."OFPROPERTY" VALUES ('provider.admin.className', 'org.jivesoftware.openfire.admin.DefaultAdminProvider');
INSERT INTO "MUXMPP"."OFPROPERTY" VALUES ('provider.user.className', 'org.jivesoftware.openfire.user.DefaultUserProvider');
INSERT INTO "MUXMPP"."OFPROPERTY" VALUES ('provider.group.className', 'org.jivesoftware.openfire.group.DefaultGroupProvider');
INSERT INTO "MUXMPP"."OFPROPERTY" VALUES ('provider.securityAudit.className', 'org.jivesoftware.openfire.security.DefaultSecurityAuditProvider');
INSERT INTO "MUXMPP"."OFPROPERTY" VALUES ('demo.workgroup', 'true');
INSERT INTO "MUXMPP"."OFPROPERTY" VALUES ('cache.KrakenSessionLocationCache.size', '-1');
INSERT INTO "MUXMPP"."OFPROPERTY" VALUES ('cache.KrakenRegistrationCache.type', 'optimistic');
INSERT INTO "MUXMPP"."OFPROPERTY" VALUES ('cache.KrakenRegistrationCache.maxLifetime', '-1');
INSERT INTO "MUXMPP"."OFPROPERTY" VALUES ('update.lastCheck', '1400754051770');
INSERT INTO "MUXMPP"."OFPROPERTY" VALUES ('provider.lockout.className', 'org.jivesoftware.openfire.lockout.DefaultLockOutProvider');
INSERT INTO "MUXMPP"."OFPROPERTY" VALUES ('provider.vcard.className', 'org.jivesoftware.openfire.vcard.DefaultVCardProvider');
INSERT INTO "MUXMPP"."OFPROPERTY" VALUES ('xmpp.session.conflict-limit', '0');
INSERT INTO "MUXMPP"."OFPROPERTY" VALUES ('cache.KrakenSessionLocationCache.type', 'optimistic');
INSERT INTO "MUXMPP"."OFPROPERTY" VALUES ('cache.KrakenRegistrationCache.size', '-1');
INSERT INTO "MUXMPP"."OFPROPERTY" VALUES ('xmpp.filetransfer.enabled', 'true');

-- ----------------------------
-- Indexes structure for table OFPROPERTY
-- ----------------------------

-- ----------------------------
-- Checks structure for table "MUXMPP"."OFPROPERTY"
-- ----------------------------
ALTER TABLE "MUXMPP"."OFPROPERTY" ADD CHECK ("NAME" IS NOT NULL);
ALTER TABLE "MUXMPP"."OFPROPERTY" ADD CHECK ("PROPVALUE" IS NOT NULL);

-- ----------------------------
-- Primary Key structure for table "MUXMPP"."OFPROPERTY"
-- ----------------------------
ALTER TABLE "MUXMPP"."OFPROPERTY" ADD PRIMARY KEY ("NAME");
