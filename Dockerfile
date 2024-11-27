FROM registry.ar.bsch/santandertec/santander-tecnologia-docker-base-images-java-openjdk-base:v17.0.5-runtime

VOLUME /tmp
ADD /target/*.jar app.jar

CMD [ "java", "-jar", "app.jar" ]
