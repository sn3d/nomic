CREATE EXTERNAL TABLE IF NOT EXISTS ${DATABASE_SCHEMA}.users(
  NAME STRING,
  SURNAME STRING
)
STORED AS PARQUET
LOCATION '${APP_ROOT_DIR}/data/users';
