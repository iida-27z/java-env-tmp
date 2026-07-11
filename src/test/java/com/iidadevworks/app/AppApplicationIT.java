package com.iidadevworks.app;

import com.iidadevworks.app.testcontainers.TestcontainersConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class AppApplicationIT {

  @Test
  @DisplayName("Spring Bootコンテキストとテスト用コンテナが正常にロードされる")
  void contextLoads() {}
}
