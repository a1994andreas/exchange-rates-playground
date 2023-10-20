
This repo is used as a playground, different branches contain different approaches for the same problem 

## Branch: webclient-implementation

#### Integrated with one client that is providing exchange rates.

#### Tech used in this solution:
1. Used WebClient for the integration

## Branch: feignClient-implementation

#### Integrated with two clients that are providing exchange rates.

#### Tech used in this solution:

1. Feign Clients
2. Circuit Breaker
3. Implemented tests using Wiremock server 

## Repo: https://github.com/a1994andreas/exchange-rates

#### This is the initial implementation using outdated technologies(java version: 8)
Integrated with two clients that are providing exchange rates.


#### Tech used in this solution:
1. RestTemplate
2. Converters
3. Custom implementation to pick up a healthy client at runtime 
4. External dockerized Redis instance for caching
5. Swagger
