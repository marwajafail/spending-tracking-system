# Spending Tracking System
_A management system for users to monitor and track their spending._

Unit 2 Project for General Assembly JDI course. (Group project)



## Featured Technologies:
* Java
* Springboot
* Maven
* Postgre
* Github
* Gmail Services
* Swagger


## Resources:
| Resources |
| ------ |
| [Trello](https://trello.com/invite/b/66a0cb134ac1bad7b1f64ed3/ATTIc2bb1d203e6f1e7a3cf795947b6b8aa3DB0F7E2B/spendingappboard) |
| [ERD](https://app.smartdraw.com/editor.aspx?templateId=520ece43-167b-445c-b810-fb7d35e9aba4&flags=128#depoId=58995394&credID=-65479385) |
| [Frontend Repo](https://git.generalassemb.ly/fatemamarhoon/Project2Frontend) |


![Screenshot 2024-07-24 at 12 42 46 PM](https://media.git.generalassemb.ly/user/53164/files/6118d0f2-c6d5-4ce5-8dab-cdef29c4ae47)


## Tutorials:
| Tutorials |
| ------ |
| [Email Verification](https://www.codejava.net/frameworks/spring-boot/email-verification-example)|
| [Uploading Files](https://spring.io/guides/gs/uploading-files)|
| [Swagger Documentation](https://www.baeldung.com/spring-rest-openapi-documentation)|


## Planning & Development Process:
* Creation of user stories and acceptance criteria for admins and regular users.
* Initially started with data structure by creating an ERD and the corresponding java model classes.
* Distribution of feature list among project members.
* Create repository and service classes for each entity class.
* Create API endpoints for each service and CRUD operations.
* Perform endpoint testing to test that all APIs are functioning properly.
* Integrate all components and develop a frontend for the user to interact with.

## Challenges:
* Attempting to reference the user that is currently logged in by retrieving the user from the bearer token.
* Testing the endpoints to cover most of the validations and reduce the risk of exceptions.

## Favorite Functions:
* Swagger documentation
    * Addition of a name and description for each controller and each endpoint.
    * Produce a clear list of all endpoints, the request parameters and bodies they accept, and what is the expected return value.
* Email verification
    * Construct various email messages with HTML elements.
    * Connecting to the gmail servers to handle email sending.

## Run Instructions:
* Run the script.sql file in the Database folder.
* Modify variables in the application.properties and any other profiles.
* Run the sprinboot project.
* Access the endpoints names and description using swagger.
* Use postman to send requests to the API endpoints.

"# Spending-tracking-system" 
