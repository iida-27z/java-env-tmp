package com.iidadevworks.app.support.testcontainers;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

  static final PostgreSQLContainer POSTGRES;
  static final String FULL_IMAGE_NAME = "postgres:18.6";
  static final String SCRIPT_PATH = "db/init/00_db_init.sh";
  static final String INIT_DB_PATH = "/docker-entrypoint-initdb.d/init.sh";
  static final String MIGRATION_USER = "migrator";
  static final String MIGRATION_PASSWORD = "migrator_pass";
  static final String APP_USER = "app_user";
  static final String APP_PASSWORD = "app_pass";
  static final String APP_SCHEMA = "app_schema";
  static final String FLYWAY_HISTORY_TABLE = "flyway_schema_history";

  static {
    POSTGRES = new PostgreSQLContainer(DockerImageName.parse(FULL_IMAGE_NAME));
    POSTGRES
        .withCopyFileToContainer(MountableFile.forClasspathResource(SCRIPT_PATH), INIT_DB_PATH)
        .withEnv("MIGRATION_USER", MIGRATION_USER)
        .withEnv("MIGRATION_PASSWORD", MIGRATION_PASSWORD)
        .withEnv("APP_USER", APP_USER)
        .withEnv("APP_PASSWORD", APP_PASSWORD)
        .withEnv("APP_SCHEMA", APP_SCHEMA);
    POSTGRES.start();
  }

  @Bean
  PostgreSQLContainer postgresContainer() {
    return POSTGRES;
  }

  @Bean
  DynamicPropertyRegistrar dynamicPropertyRegistrar() {
    return registry -> {
      registry.add("spring.flyway.enabled", () -> true);
      registry.add("spring.flyway.schemas", () -> APP_SCHEMA);
      registry.add("spring.flyway.url", POSTGRES::getJdbcUrl);
      registry.add("spring.flyway.user", () -> MIGRATION_USER);
      registry.add("spring.flyway.password", () -> MIGRATION_PASSWORD);
      registry.add("spring.flyway.placeholders.history-table-name", () -> FLYWAY_HISTORY_TABLE);
      registry.add("spring.flyway.placeholders.app-user", () -> APP_USER);
      registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
      registry.add("spring.datasource.hikari.schema", () -> APP_SCHEMA);
      registry.add("spring.datasource.username", () -> APP_USER);
      registry.add("spring.datasource.password", () -> APP_PASSWORD);
    };
  }
}
