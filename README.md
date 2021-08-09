# Vehicle Service App

This repository contains a cross-platform JavaFX desktop application for technical vehicle service. Other used tools are SQLite database, 
JasperReports, Java ResourceBundle (for internationalization), and Maven.

![rpr-projekat-app-screenshots](https://user-images.githubusercontent.com/44180058/128631541-dc526a45-0487-4ac8-a485-b8e9eeaab925.jpeg)

## How To Use :wrench:

To clone and run this application, you will need [Git](https://git-scm.com), 
[JDK 10](https://www.oracle.com/java/technologies/java-archive-javase10-downloads.html) and 
[Maven](https://maven.apache.org/download.cgi).

```bash
# Clone this repository
$ git clone https://github.com/kkadusic/vehicle-service-app

# Go into the root directory
$ cd vehicle-service-app

# Compile the project and generate target folder
$ mvn compile

# Execute the project
$ mvn exec:java -Dexec.mainClass=ba.unsa.etf.rpr.projekat.Main

# If you want to create fat JAR with dependencies
$ mvn clean compile assembly:single
```
