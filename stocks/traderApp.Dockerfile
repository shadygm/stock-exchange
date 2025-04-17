FROM --platform=linux/amd64 maven:3.8.3-openjdk-17

COPY . ./

RUN mvn package -Dmaven.test.skip -Dcheckstyle.skip
ENTRYPOINT ["java", "-cp", "trader-application/target/trader-application-1.0-SNAPSHOT-jar-with-dependencies.jar", "nl.rug.aoop.traderapplication.startup.TraderMain"]
