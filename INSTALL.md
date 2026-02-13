# Install

This document shows how to install and start EduQuest on your PC. 

## Prerequisites

Before you begin the installation, ensure you have the following tools:
- Java Developer Kit 21. You can check your Java version by running the following command; you must have at least 21.x.x
``` bash
java -version
```
- Maven. You can check if it installed by performing the following command
``` bash
mvn -v
```
- Google Chrome or another modern browser

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

After you installed and run the software, you can access the web application on your browser at the following address: `localhost:8080`.
