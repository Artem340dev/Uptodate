# UPTODATE v0.2.2 (dev)

## New amendments and features

1. The REST API has been enhanced by enforcing some of new API endpoints:
   - /api/articles/search
   - /api/articles/delete
   - /api/articles/edit
   - /api/articles/create
   - /auth/register
   - /auth/register/verify-code
2. The registration system including confirming a prospective user by email verification code has been implemented
3. The authentication system has been significantly boosted and maintained

## Execution

**You are capable of executing the Backend by using Docker. Keep the further requirements:**

1. Download the project from the Github repository
2. In order to launch the project in the downloaded folder, you need to execute the further command: `docker-compose up --build`
4. The Docker environment is going to be assembled
5. After assembling, please, reboot all the containers

# New amendments and features

1. The issues of SQL injections connected to improperly working the Json Web Token Middleware facility are exempt now
2. The Spring JPA models related to internal project features are implemented now
3. Swagger UI has been involved into a project for describing the endpoints
4. Some of the REST API endpoints related to the articles Frontend-to-Backend requests processing have been implemented (/api/articles/get, /api/articles/getAll)