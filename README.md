# fabrick-transaction-manager-api
Spring Boot based RESTful service for managing bank account operations, including balance checks, transaction history, and money transfers.

# How to Test the API

## 1. Run Locally

Make sure you have Java and Maven installed.

- **Build the project:**
  ```sh
  mvn clean install
- **Run the application:**
  ```sh
  mvn spring-boot:run
  ```

The application will be available at http://localhost:8080.

## 2. Explore and test the API 
### using Swagger UI
Open your browser and navigate to:
http://localhost:8080/swagger-ui.html 
This will display the Swagger UI where you can explore all available endpoints, their parameters, and responses.

### using Postman/Insomnia or cURL
You can also use Postman or cURL to test the API endpoints. Below are some example calls.

## 3. Example API Calls
### Get Transaction History
```sh   
curl -X GET "http://localhost:8080/api/v1/accounts/14537780/transactions?fromAccountingDate=2019-01-01&toAccountingDate=2019-12-01" \
  -H "accept: application/json" \
  -H "Authorization: Bearer faketoken123"
  ```
  
### Get Account Balance
```sh
curl -X GET "http://localhost:8080/api/v1/accounts/14537780/balance" \
  -H "accept: application/json" \
  -H "Authorization: Bearer faketoken123"
  ```

### Make a Money Transfer
```sh
curl -X POST "http://localhost:8080/api/v1/accounts/14537780/money-transfer" \
  -H "accept: application/json" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer faketoken123" \
  -d '{
    "creditor": {
      "name": "John Doe",
      "account": {
        "accountCode": "IT23A0336844430152923804660",
        "bicCode": "SELBIT2BXXX"
      },
      "address": {
        "address": null,
        "city": null,
        "countryCode": null
      }
    },
    "executionDate": "2025-06-19",
    "uri": "REMITTANCE_INFORMATION",
    "description": "Payment invoice 75/2017",
    "amount": 800,
    "currency": "EUR",
    "isUrgent": false,
    "isInstant": false,
    "feeType": "SHA",
    "feeAccountId": "14537780",
    "taxRelief": {
      "taxReliefId": "L449",
      "isCondoUpgrade": false,
      "creditorFiscalCode": "56258745832",
      "beneficiaryType": "NATURAL_PERSON",
      "naturalPersonBeneficiary": {
        "fiscalCode1": "MRLFNC81L04A859L",
        "fiscalCode2": null,
        "fiscalCode3": null,
        "fiscalCode4": null,
        "fiscalCode5": null
      }
    }
  }'
```
