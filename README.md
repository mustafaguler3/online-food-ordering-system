# FoodApp Backend

## 📌 Project Overview
FoodApp Backend is a **Spring Boot 3** REST API for a food ordering system.  
It provides:
- User authentication with JWT
- Admin and delivery roles
- Menu, category, and order management
- Stripe payment integration
- Email notifications via SMTP
- PostgreSQL/MySQL database support

This backend works in conjunction with the FoodApp Frontend (`food-react`) and is containerized with Docker for easy deployment.

## 🛠 Technology Stack
- **Java 17 + Spring Boot 3**
- **Spring Data JPA + Hibernate**
- **PostgreSQL** (primary) / **MySQL** (optional dev)
- **Docker & Docker Compose**
- **Redis** for caching and async tasks
- **GitHub Actions** for CI/CD
- **Render** for production deployment
- **JUnit & Mockito** for testing

## ⚙️ Setup and Development

### 1. Clone Repository
git clone <backend-repo-url>
cd online-food-ordering-system

2. **Environment Variables**
Create .env.dev for development:

DB_USERNAME=root
DB_PASSWORD=1234
DB_URL=jdbc:mysql://mysql:3306/fooddb

MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-password

JWT_SECRET=supersecretjwt
STRIPE_PUBLIC_KEY=pk_test_xxx
STRIPE_SECRET_KEY=sk_test_xxx
SPRING_PROFILES_ACTIVE=dev
FRONTEND_BASE_URL=http://localhost:3000
BASE_PAYMENT_LINK=http://localhost:3000/pay?orderId=

3. **Create .env.prod for production:**
   DB_USERNAME=your-prod-db-username
   DB_PASSWORD=your-prod-db-password
   DB_URL=jdbc:postgresql://<prod-db-host>:5432/fooddb

MAIL_USERNAME=your-prod-email
MAIL_PASSWORD=your-prod-email-password

JWT_SECRET=supersecretjwtprod
STRIPE_PUBLIC_KEY=pk_live_xxx
STRIPE_SECRET_KEY=sk_live_xxx
SPRING_PROFILES_ACTIVE=prod
FRONTEND_BASE_URL=https://your-frontend-url.com
BASE_PAYMENT_LINK=https://your-frontend-url.com/pay?orderId=

4. **Docker Compose (Development)**

services:
mysql:
image: mysql:8
container_name: food-mysql
environment:
MYSQL_ROOT_PASSWORD: 1234
MYSQL_DATABASE: fooddb
ports:
- "3307:3306"
volumes:
- mysql_data:/var/lib/mysql

redis:
image: redis:7
container_name: food-redis
ports:
- "6379:6379"

backend:
build:
context: .
dockerfile: Dockerfile
container_name: food-backend
ports:
- "8080:8080"
env_file:
- .env.dev
depends_on:
- mysql
- redis

volumes:
mysql_data:

Start dev environment
docker compose -f docker-compose.dev.yml up --build

5. **Running Locally without Docker**
   mvn clean install
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
6. **Testing**
- mvn test
  Unit tests use JUnit 5 and Mockito. Integration tests are configured for dev databases.

🚀 **Deployment**

Render Deployment (Production)
1.	Connect your GitHub backend repository to Render.
2.	Set the build and start commands:
      ./mvnw clean package
      java -jar target/online-food-ordering-system-0.0.1-SNAPSHOT.jar
3.	Add environment variables from .env.prod in Render dashboard.
4.	Ensure the database URL points to a production PostgreSQL instance.

**CI/CD with GitHub Actions**
•	Optional: Automate testing, build, and deploy on every push to main or develop:
1.	Checkout repository
2.	Install dependencies and build
3.	Run tests
4.	Deploy to Render or other cloud provider
•	Example workflow can include Docker image build and push to Docker Hub.

**Docker Production Deployment**
*	Build Docker image: docker build -t food-backend:latest .
*   Push to Docker registry (optional)
  - docker tag food-backend:latest <dockerhub-username>/food-backend:latest
  - docker push <dockerhub-username>/food-backend:latest

🔑 Notes
•	Make sure SPRING_PROFILES_ACTIVE matches your environment (dev or prod).
•	For production, use a strong JWT secret (>= 256 bits) for HS256.
•	Ensure all environment variables are correctly configured in Render or your hosting provider.
•	Database migrations are handled automatically with spring.jpa.hibernate.ddl-auto=update (for dev). For production, consider Flyway or Liquibase for controlled migrations.

📂 **Folder Structure**

src
└── main
├── java
│   └── com.example.FoodApp
│       ├── admin
│       ├── auth_users
│       ├── aws
│       ├── cart
│       ├── category
│       ├── config
│       ├── delivery
│       ├── email_notification
│       ├── enums
│       ├── exceptions
│       ├── image.controller
│       ├── menu
│       ├── order
│       ├── payment
│       ├── response
│       ├── restaurant
│       ├── review
│       ├── role
│       ├── security
│       └── FoodAppApplication.java
│
├── resources
│   ├── images
│   ├── static.uploads
│   ├── templates
│   ├── application.yml
│   ├── application-dev.yml
│   └── application-prod.yml
│
└── test

Frontend Netlify -> https://online-food-app-react.netlify.app/home
Frontend Repo - https://github.com/mustafaguler3/online-food-ordering-frontend.git