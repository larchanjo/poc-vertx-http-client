# Overview

This is a simple poc using [vertx-client].

## Tests

I have created a verticle named ProductVerticle whose contains four eventBus address, 
each one handle a different HTTP Verb (GET, POST, PUT, DELETE).

### Endpoints

The endpoints were created by [mockAPI]

- GET - http://5b9d5606a4647e0014745172.mockapi.io/api/v1/products
- POST - http://5b9d5606a4647e0014745172.mockapi.io/api/v1/products
- PUT - http://5b9d5606a4647e0014745172.mockapi.io/api/v1/products/:id
- DELETE - http://5b9d5606a4647e0014745172.mockapi.io/api/v1/products:/id

[vertx-client]: http://tutorials.jenkov.com/vert.x/http-client.html
[mockAPI]: https://www.mockapi.io
