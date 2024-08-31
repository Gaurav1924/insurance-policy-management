
# 🛡️ **Insurance Policy Management System**

## Overview

The **Insurance Policy Management System** is a comprehensive solution designed to streamline the management of insurance policies, customer information, claims processing, and premium payments. Built using **Spring Boot**, this system provides a robust backend that exposes RESTful APIs for various operations, ensuring efficiency, scalability, and ease of use.

## 🚀 **Key Features**

### 1. **Customer Management**
   - **Create Customer:** Easily add new customer profiles with details like name, contact information, address, and identification.
   - **View Customers:** Access a complete list of customers with advanced search and filter options.
   - **Update Customer:** Modify existing customer details as needed.
   - **Delete Customer:** Safely deactivate or delete customer profiles.

### 2. **Policy Management**
   - **Create Policy:** Define new insurance policies, including policy number, type, coverage amount, start and end dates, and premium.
   - **Assign Policy:** Link policies to specific customers effortlessly.
   - **View Policies:** Filter and view policies by type, customer, or status.
   - **Update Policy:** Update policy details, renew, or cancel policies as needed.
   - **Delete Policy:** Deactivate or remove policies with ease.

### 3. **Claims Management**
   - **File a Claim:** Submit claims against policies, with details like claim amount, incident date, and description.
   - **View Claims:** Review all claims with filtering options by status (e.g., pending, approved, rejected).
   - **Update Claim Status:** Approve, reject, or request more information for claims.
   - **Claim History:** Track the history of claims associated with specific policies or customers.

### 4. **Reporting**
   - **Generate Reports:** Obtain insightful reports, such as:
     - Total number of active policies by type.
     - Total claims processed within a specific time frame.

## 🛠️ **Technical Specifications**

- **Backend Framework:** Spring Boot
- **Database:** MySQL/PostgreSQL
- **Build Tool:** Maven/Gradle
- **Languages:** Java
- **API Documentation:** Swagger

## 📦 **Project Structure**

- `src/main/java`: Contains the main codebase, including models, services, repositories, and controllers.
- `src/main/resources`: Contains configuration files, including application properties.
- `src/test/java`: Contains test cases to ensure code quality and reliability.

## 🧾 **Getting Started**

### Prerequisites
- Java JDK 11+
- Maven/Gradle
- MySQL/PostgreSQL Database

### Installation

1. **Clone the repository:**
   git clone https://github.com/your-username/insurance-policy-management.git
   
2. **Navigate to the project directory:**
   cd insurance-policy-management
  
3. **Configure the database:**
   - Update `application.properties` with your database credentials.

4. **Build the project:**
   mvn clean install
   
5. **Run the application:**
   mvn spring-boot:run

## 🧩 **Usage**

### API Endpoints

- **Customer Management:**
  - `POST /customers` - Create a new customer.
  - `GET /customers` - Retrieve all customers.
  - `GET /customers/{id}` - Retrieve a customer by ID.
  - `PUT /customers/{id}` - Update customer details.
  - `DELETE /customers/{id}` - Delete a customer.

- **Policy Management:**
  - `POST /policies` - Create a new policy.
  - `GET /policies` - Retrieve all policies.
  - `GET /policies/{id}` - Retrieve a policy by ID.
  - `PUT /policies/{id}` - Update policy details.
  - `DELETE /policies/{id}` - Delete a policy.

- **Claims Management:**
  - `POST /claims` - File a new claim.
  - `GET /claims` - Retrieve all claims.
  - `PUT /claims/{id}` - Update claim status.

### Example Request

```json
{
  "policyNumber": "12345",
  "type": "Auto",
  "coverageAmount": 5000.00,
  "startDate": "2024-01-01",
  "endDate": "2025-01-01",
  "premium": 300.00,
  "customer": {
    "id": 2
  }
}
