@SCRUM-38 @EPIC-1
Feature: US-ADM-03 User Management, Authentication and Role-Based Access Control (RBAC)

  As a Möber platform administrator or user
  I want a secure user management, authentication and RBAC mechanism
  So that users can register, login with JWT, and only access endpoints permitted by their active role

  Background:
    Given the Möber authentication and user management system is operational

  # ---------------------------------------------------------------------------
  # 1. User Registration & Password Encryption
  # ---------------------------------------------------------------------------
  Scenario: Successful user registration with encrypted password
    When a new courier submits valid registration data:
      | name     | André Pereira     |
      | email    | andre@mober.pt    |
      | password | SecurePass123!    |
    Then the system creates the user account with status "ACTIVE" and role "ROLE_COURIER"
    And the stored password is encrypted using BCrypt (never plaintext)
    And the HTTP response status code is 201 Created
    And the response body contains a valid JWT token and user profile details

  Scenario Outline: Registration rejection due to invalid input data
    When a registration request is submitted with name "<name>", email "<email>" and password "<password>"
    Then the system rejects the registration with HTTP status 400 Bad Request
    And the response includes an error message indicating the invalid fields

    Examples:
      | name          | email            | password | reason           |
      |               | valid@mober.pt   | Pass1234 | Blank name       |
      | André Pereira | invalid-email-at | Pass1234 | Invalid email    |
      | André Pereira | valid@mober.pt   | short    | Password < 8 chs |

  Scenario: Registration rejection due to duplicate email
    Given a user already exists with email "andre@mober.pt"
    When another user attempts to register with email "andre@mober.pt"
    Then the system rejects the registration with HTTP status 409 Conflict
    And the response indicates that the email is already in use

  # ---------------------------------------------------------------------------
  # 2. Authentication (Login) & JWT Issuance
  # ---------------------------------------------------------------------------
  Scenario Outline: Successful login returns JWT token containing role
    Given an active user exists with email "<email>", password "ValidPass123!" and role "<role>"
    When the user submits a login request with email "<email>" and password "ValidPass123!"
    Then the HTTP response status code is 200 OK
    And the response body contains a JWT Bearer token
    And the decoded token contains subject "<email>" and claim role "<role>"

    Examples:
      | email            | role          |
      | courier@mober.pt | ROLE_COURIER  |
      | tech@mober.pt    | ROLE_OPERATOR |
      | admin@mober.pt   | ROLE_ADMIN    |

  Scenario: Login rejection with incorrect password
    Given an active user exists with email "courier@mober.pt" and password "CorrectPassword123!"
    When the user attempts to login with email "courier@mober.pt" and password "WrongPassword"
    Then the HTTP response status code is 401 Unauthorized
    And no JWT token is issued


  # ---------------------------------------------------------------------------
  # 3. Role-Based Access Control (RBAC) & Endpoint Protection
  # ---------------------------------------------------------------------------
  Scenario: Unauthenticated request to protected endpoints is rejected
    When a client sends a GET request to "/api/users/me" without an Authorization header
    Then the HTTP response status code is 401 Unauthorized

  Scenario: Authenticated user accesses their own profile
    Given an authenticated user with email "courier@mober.pt" and role "ROLE_COURIER"
    When the user sends a GET request to "/api/users/me" with their Bearer token
    Then the HTTP response status code is 200 OK
    And the response contains their profile details without the password field

  Scenario: Non-admin user is forbidden from accessing administrative endpoints
    Given an authenticated user with role "ROLE_COURIER"
    When the user sends a GET request to "/api/users" with their Bearer token
    Then the HTTP response status code is 403 Forbidden

