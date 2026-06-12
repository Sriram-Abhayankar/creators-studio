# Fabric Expense Management App

## Project Overview

Fabric Expense Management App is a mobile application designed for a single business owner to track fabric and accessory purchase expenses.

The application is focused on recording purchasing expenses and maintaining accurate expense records.

This is not an inventory management system in the current version.

---

## Primary Goal

Provide a simple and efficient way to record:

* Fabric purchases
* Accessory purchases
* Purchase costs
* Expense history (future enhancement)

---

## User Type

Single User Application

* One registered user
* Username and password authentication
* No roles
* No multi-user support
* No permission management

---

## Current Modules

### Authentication

* User Registration
* User Login

### Dashboard

* Purchasing Bills
* Payment Bills (Future Module)

### Purchasing Bills

#### Fabric Purchase

Features:

* Fabric Name dropdown with predefined values and Others option
* Fabric Type dropdown with predefined values and Others option
* Dynamic row addition
* Automatic row total calculation
* Automatic purchase total calculation

#### Accessory Purchase

Accessory Types:

* Cone
* Size Pattern
* Others

Features:

* Dynamic rows for Cone entries
* Automatic row total calculation
* Automatic purchase total calculation

---

## Database Design

Main Entities:

* User
* Fabric
* Fabric_items
* Accessory
* Cone
* Cone_items
* Size_pattern
* others

Relationships:

* Fabric → Fabric_items
* Accessory → Cone
* Cone → Cone_items
* Accessory → Size_pattern
* Accessory → others

---

## Technology Stack

Backend:

* Java
* Spring Boot
* Spring Data JPA
* Hibernate
* MySQL

Frontend:

* HTML
* CSS
* JavaScript

Build Tool:

* Maven

---

## Future Enhancements

* Payment Bills Module
* Expense Table
* Reports
* Search and Filters
* PDF Export
* Analytics Dashboard
* Supplier Management
* Inventory Management

---

## Important Development Rules

* Use the requirement document as the source of truth.
* Do not rename database tables.
* Do not rename database columns.
* Preserve all business rules defined in the requirement document.
* Implement dynamic row functionality exactly as specified.
* Generate code in phases and wait for review before moving to the next phase.
