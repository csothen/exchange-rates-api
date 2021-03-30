# Exchange Rates API #

## Get Started ##

### Requirements ###

- [Docker](https://docs.docker.com/get-started/)
- [Docker Compose](https://docs.docker.com/compose/install/)

### Starting the API Server ###

To start the API Server in your machine you can run `docker-compose up`, this will start the application and expose it in your port `9091`.

### Features ###

#### List all currencies available ####

```
GET : /api/currencies
```

#### Convert amounts ####

```
GET : /api/currencies/convert/{amount}?from={currencyCode}&to={currencyCode}
```

#### Get exchange rate for currency pair ####

```
GET : /api/rates?from={currencyCode}&to={currencyCode}
```

#### Retrieve a link to visualize the exchange rate evolution in a chart ####

```
GET : /api/rates/chart?from={currencyCode}&to={currencyCode}
```