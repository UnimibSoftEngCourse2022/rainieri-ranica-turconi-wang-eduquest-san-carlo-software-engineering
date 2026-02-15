# Install

This document shows how to install and start EduQuest on your PC. 

## Prerequisites

Before you begin the installation, ensure you have the following tools:
- **Java Developer Kit 21**: You must have at least version 21.x.x installed. You can download it from [Adoptium (Eclipse Temurin)](https://adoptium.net/) or [Oracle](https://www.oracle.com/java/technologies/downloads/). You can check your Java version by running the following command:
``` bash
java -version
```
- **Maven**: Required for building the project. Download it from the [official Apache Maven website](https://maven.apache.org/).You can check if it is installed by performing the following command
``` bash
mvn -v
```
- Google Chrome or another modern browser
- **Database Setup**: No external database installation is required. The application uses an in-memory database (H2) and comes pre-configured. It runs completely out-of-the-box.

## Developer installation

1. Clone and build
``` bash
git clone [eduquest-repository-url]
cd [cloned-repository-folder]
mvn clean install
```

2. Test
```
mvn test
```

3. Run
```
mvn spring-boot:run
```

## User installation

1. Find the latest release of EduQuest in the <a href="https://github.com/UnimibSoftEngCourse2022/rainieri-ranica-turconi-wang-eduquest-san-carlo-software-engineering/releases">releases page</a>
2. Download the `.jar` file attached in the chosen release
3. Run the following command in your terminal

``` bash
java -jar eduquest-x.y.z.jar
```

## Use the application

After installing and running the software, you can access the web application on your browser at the following address: `localhost:8080`.
