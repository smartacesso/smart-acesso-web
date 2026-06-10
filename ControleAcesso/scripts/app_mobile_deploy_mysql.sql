-- Script único para deploy da API mobile (MySQL).
-- Alternativa: hibernate.hbm2ddl.auto=update cria/atualiza automaticamente.

SOURCE TB_AVISO_APP_mysql.sql;
SOURCE TB_DEVICE_TOKEN_mysql.sql;

-- Se TB_DEVICE_TOKEN já existia sem APP_VERSION:
-- SOURCE TB_DEVICE_TOKEN_alter_app_version_mysql.sql;
