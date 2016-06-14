#! /bin/bash
cd services/eureka-service
mvn spring-boot:run &
cd ../..
cd services/api-gateway
mvn spring-boot:run &
cd ../..
cd services/movie-service
mvn spring-boot:run &
cd ../..
cd services/image-service
mvn spring-boot:run &
cd ../..
cd services/ratings-service
mvn spring-boot:run &
cd ../..
cd services/similar-movie-service
mvn spring-boot:run &