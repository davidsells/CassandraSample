package com.persist.simple;

import com.persist.simple.CassandraSampleApp;
import com.persist.simple.config.AsyncSyncConfiguration;
import com.persist.simple.config.EmbeddedCassandra;
import com.persist.simple.config.EmbeddedKafka;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = { CassandraSampleApp.class, AsyncSyncConfiguration.class })
@EmbeddedCassandra
@EmbeddedKafka
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public @interface IntegrationTest {
}
