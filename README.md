# GitHub Repositories app

## Building the app 
Open app directory and run:
`
mvn package
`


## Running the app 
Quickstart:
`
./mvnw spring-boot:run
`

Other way: 
`
java -jar app_name.jar
`

App configuration is provided in `application.properties` file.


## API

`
GET /repositories/{owner}/{repository-name
`

Please provide `Authorization` header (even empty) to make a request.
In order make request authorized with GitHub account with 2 factor verification set, provide personal access token in
 format as presented below:
 
 `
 token your_personal_token
 `
 
 Read more about [token generation](https://help.github.com/en/articles/creating-a-personal-access-token-for-the-command-line)
 
 
 ## General info
 For simplicity I ommitted saving logs to log files.
