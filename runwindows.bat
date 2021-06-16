@echo off
@title Darkan Server
mvn clean install
mvn compile
mvn exec:java
pause