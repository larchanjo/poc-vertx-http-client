package app;

import com.fasterxml.jackson.databind.DeserializationFeature;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class Application {

  private static final Logger logger = LoggerFactory.getLogger(Application.class);

  public static void main(String... arguments) {
    logger.info("Starting application");
    Json.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new ProductVerticle());
  }

}
