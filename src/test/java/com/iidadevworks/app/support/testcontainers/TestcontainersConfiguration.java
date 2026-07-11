package com.iidadevworks.app.support.testcontainers;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

  static final PostgreSQLContainer POSTGRES;
  static final String FULL_IMAGE_NAME = "postgres:18.6";
  static final String SCRIPT_PATH = "db/init/00_db_init.sh";
  static final String INIT_DB_PATH = "/docker-entrypoint-initdb.d/init.sh";

  static {
    POSTGRES = new PostgreSQLContainer(DockerImageName.parse(FULL_IMAGE_NAME));
    POSTGRES
        .withEnv("MIGRATION_USER", "migrator")
        .withEnv("MIGRATION_PASSWORD", "migrator_pass")
        .withEnv("APP_USER", "app_user")
        .withEnv("APP_PASSWORD", "app_pass")
        .withEnv("MIGRATION_SCHEMA", "migration_schema")
        .withEnv("APP_SCHEMA", "app_schema")
        .withCopyFileToContainer(MountableFile.forClasspathResource(SCRIPT_PATH), INIT_DB_PATH);
  }

  @Bean
  @ServiceConnection
  PostgreSQLContainer postgresContainer() {
    return POSTGRES;
  }
}
