package app;

import app.domain.Product;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import java.util.UUID;
import lombok.NonNull;
import lombok.val;

public class ProductVerticle extends AbstractVerticle {

  private static final Logger logger = LoggerFactory.getLogger(ProductVerticle.class);
  private static final String url = "http://5b9d5606a4647e0014745172.mockapi.io/api/v1/products";
  private WebClient webClient;

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    super.start(startFuture);

    val webClientOptions = new WebClientOptions();
    webClientOptions.setConnectTimeout(5000);
    webClient = WebClient.create(vertx, webClientOptions);

    EventBus eventBus = getVertx().eventBus();
    eventBus.consumer("product-get").handler(this::handleGet);
    eventBus.consumer("product-post").handler(this::handlePost);
    eventBus.consumer("product-put").handler(this::handlePut);
    eventBus.consumer("product-delete").handler(this::handleDelete);

    // Create a random product to see the chain
    eventBus.send("product-post", UUID.randomUUID().toString());
  }

  private void handleGet(@NonNull Message<Object> message) {
    logger.info("Handling GET");
    val productId = message.body().toString();
    webClient.getAbs(url + "/" + productId).send(request -> {
      if (request.succeeded()) {
        val body = request.result().bodyAsString();
        val product = Json.decodeValue(body, Product.class);
        logger.info(String.format("%s successfully obtained", product));
        getVertx().eventBus().send("product-put", Json.encode(product));
      } else {
        logger.warn(String.format("Fail to get Product=[%s]", productId));
      }
    });
  }

  private void handlePost(@NonNull Message<Object> message) {
    logger.info("Handling POST");

    val productName = message.body().toString();
    val product = Product.builder()
      .name(productName)
      .build();

    webClient.postAbs(url).send(request -> {
      if (request.succeeded()) {
        val body = request.result().bodyAsString();
        val newProduct = Json.decodeValue(body, Product.class);
        logger.info(String.format("%s successfully created", newProduct));
        getVertx().eventBus().send("product-get", newProduct.getId());
      } else {
        logger.warn(String.format("Fail to create %s", product));
      }
    });
  }

  private void handlePut(@NonNull Message<Object> message) {
    logger.info("Handling PUT");

    val json = message.body().toString();
    val product = Json.decodeValue(json, Product.class);
    product.setName(UUID.randomUUID().toString());

    webClient.getAbs(url + "/" + product.getId()).send(request -> {
      if (request.succeeded()) {
        logger.info(String.format("Product=[%s] successfully updated", product.getId()));
        getVertx().eventBus().send("product-delete", Json.encode(product));
      } else {
        logger.warn(String.format("Fail to update Product=[%s], Reason=[%s]", product.getId(),
          request.result().statusMessage()));
      }
    });
  }

  private void handleDelete(@NonNull Message<Object> message) {
    logger.info("Handling DELETE");

    val json = message.body().toString();
    val product = Json.decodeValue(json, Product.class);

    webClient.deleteAbs(url + "/" + product.getId()).send(request -> {
      if (request.succeeded()) {
        logger.info(String.format("Product=[%s] successfully deleted", product.getId()));
      } else {
        logger.warn(String.format("Fail to delete Product=[%s], Reason=[%s]", product.getId(),
          request.result().statusMessage()));
      }
    });
  }

}
