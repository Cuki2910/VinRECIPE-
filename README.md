# VinRECIPE — Smart Recipe and Grocery Planner

**Group 13 | COMP1020 | VinUniversity**

## Team Members
| Name | Student ID | Role |
|---|---|---|
| Nguyen Trong Nhan | V202502098 | Team Leader, Architecture, DAO |
| Nguyen Thi Quynh Trang | V202502665 | Model Layer, User System |
| Nguyen Tien Nhat Nguyen | V202502943 | Service Layer, Search Algorithm |
| Dang Tuan Kiet | V202502041 | JavaFX GUI |
| Do Viet Phuong | V202502638 | Shopping List, Testing |

---

## Prerequisites

| Requirement | Version |
|---|---|
| Java | 17+ (tested on Java 25) |
| Maven | 3.8+ |
| MySQL Server | 8.x |

---

## Setup Instructions

### 1. Database Setup

Open MySQL and run the schema script:

```bash
mysql -u root -p < src/main/resources/db/schema.sql
```

Or paste it manually in MySQL Workbench.

This creates:
- Database `vinrecipe_db`
- All necessary tables
- Demo seed data (5 recipes, 6 users)

**Demo accounts:**
| Username | Password | Role |
|---|---|---|
| `admin` | `admin123` | Admin |
| `nhan` | `nhan123` | Room Leader |
| `trang` | `trang123` | Normal Student |

### 2. Configure Database Connection

Edit `src/main/java/com/vinrecipe/dao/DatabaseConnection.java`:

```java
private static final String URL      = "jdbc:mysql://localhost:3306/vinrecipe_db?...";
private static final String USER     = "root";         // your MySQL username
private static final String PASSWORD = "vinrecipe";    // your MySQL password
```

### 3. Build & Run

```bash
mvn clean compile
mvn javafx:run
```

---

## Project Architecture

```
Model → DAO → Service → View/Controller (JavaFX)
```

### Layer Overview

| Layer | Package | Responsibility |
|---|---|---|
| **Model** | `com.vinrecipe.model` | Data classes (User, Recipe, Ingredient, etc.) |
| **DAO** | `com.vinrecipe.dao` | JDBC database operations |
| **Service** | `com.vinrecipe.service` | Business logic (Search, CRUD, Aggregation) |
| **Controller** | `com.vinrecipe.controller` | JavaFX UI controllers |

---

## OOP Concepts Demonstrated

| Concept | Where |
|---|---|
| **Inheritance** | `Admin`, `NormalStudent`, `RoomLeader` extend abstract `User` |
| **Encapsulation** | All model fields `private` with validated getters/setters |
| **Polymorphism** | `getPermissionLevel()` overridden in each User subclass |
| **Abstraction** | `User` is abstract class; `ContextAware` is an interface |
| **Composition** | `Recipe` HAS-A `List<Ingredient>`, `Room` HAS-A `List<NormalStudent>` |

---

## Key Algorithms & Data Structures

### Inverted Index Search (`SearchService`)
```
Input: userIngredients = ["chicken", "garlic", "rice"]
invertedIndex: HashMap<String, List<Recipe>>
matchCount:    HashMap<Recipe, Integer>
Sort: completion% DESC → prepTime ASC
```

### Shopping List Aggregation (`ShoppingListService`)
```
Input:  [Recipe_A, Recipe_B]
Uses:   HashMap.merge(name, quantity, Double::sum)
Output: {"chicken" → 700g, "garlic" → 30g, ...}
```

### Data Structures Used
| Structure | Where | Purpose |
|---|---|---|
| `ArrayList<Recipe>` | DAO, Service | Recipe lists |
| `ArrayList<Ingredient>` | Recipe model | Ingredient storage |
| `HashSet<String>` | SearchService | Unique tag names for ComboBox |
| `HashMap<String, Double>` | ShoppingList | Ingredient aggregation |
| `HashMap<String, List<Recipe>>` | SearchService | Inverted index |
| `HashMap<Recipe, Integer>` | SearchService | Match count |

---

## Features

- ✅ **Login / Register** — Role-based authentication (Admin, RoomLeader, Student)
- ✅ **Dashboard** — Recipe cards with sort by rating / prep time / price
- ✅ **Recipe CRUD** — Create, view, edit, delete recipes with ingredients & tags
- ✅ **Search** — By title (LIKE), by available ingredients (Inverted Index), by tag
- ✅ **Shopping List** — Select multiple recipes → auto-generate aggregated grocery list
- ✅ **Single-window navigation** — No new windows, content swapped in BorderPane
