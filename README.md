# 🍽️ VinRECIPE — Smart Recipe & Grocery Planner

**Group 13 | COMP1020 Object-Oriented Programming | VinUniversity**

> A desktop application for university dorm students to manage recipes, search ingredients, and plan grocery shopping — backed by a shared cloud database.

---

## 👥 Team Members

| Name | Student ID | Role |
|---|---|---|
| Nguyen Trong Nhan | V202502098 | Team Leader, Architecture, DAO |
| Nguyen Thi Quynh Trang | V202502665 | Model Layer, User System |
| Nguyen Tien Nhat Nguyen | V202502943 | Service Layer, Search Algorithm |
| Dang Tuan Kiet | V202502041 | JavaFX GUI |
| Do Viet Phuong | V202502638 | Shopping List, Testing |

---

## ⚡ Quick Start (Zero Configuration)

### Prerequisites

| Requirement | Version |
|---|---|
| Java JDK | 17+ (tested on Java 25) |
| Windows | 10 / 11 |

> ✅ **No MySQL, no Maven installation needed.** Both are bundled inside the project.

### Steps

```
1. Clone or download this repository
2. Double-click  run-app.bat
3. Done! The app connects to the shared cloud database automatically.
```

**Demo accounts:**

| Username | Password | Role |
|---|---|---|
| `admin` | `admin123` | Admin (can edit/delete any recipe) |
| `nhan` | `nhan123` | Room Leader |
| `trang` | `trang123` | Normal Student |
| `kiet` | `kiet123` | Normal Student |

---

## 🏗️ Project Architecture

```
Model  →  DAO  →  Service  →  View / Controller (JavaFX)
```

| Layer | Package | Responsibility |
|---|---|---|
| **Model** | `com.vinrecipe.model` | Data classes: User, Recipe, Ingredient, Tag, Room, ShoppingList |
| **DAO** | `com.vinrecipe.dao` | JDBC database operations (PreparedStatement, Singleton connection) |
| **Service** | `com.vinrecipe.service` | Business logic: Search (Inverted Index), CRUD, Shopping aggregation |
| **Controller** | `com.vinrecipe.controller` | JavaFX UI controllers, Single-window navigation pattern |

---

## 📐 OOP Concepts Demonstrated

| Concept | Where |
|---|---|
| **Inheritance** | `Admin`, `NormalStudent`, `RoomLeader` extend abstract `User` |
| **Encapsulation** | All model fields `private` with validated getters/setters |
| **Polymorphism** | `getPermissionLevel()` overridden in each User subclass; Role-based UI visibility |
| **Abstraction** | `User` is abstract class; `ContextAware` is an interface |
| **Composition** | `Recipe` HAS-A `List<Ingredient>`, `Room` HAS-A `List<NormalStudent>` |

---

## 🧠 Key Algorithms & Data Structures

### 🔍 Inverted Index Search (`SearchService`)
```
Input:  userIngredients = ["chicken", "garlic", "rice"]
Index:  HashMap<String, List<Recipe>>   ← ingredient → recipes
Count:  HashMap<Recipe, Integer>        ← match count per recipe
Sort:   matchCount DESC → prepTime ASC
```

### 🛒 Shopping List Aggregation (`ShoppingListService`)
```
Input:  user selects individual ingredients with custom quantities
Uses:   ObservableList<CartItem> with merge on duplicate names
Output: real-time total price in VND
```

### ⚡ Performance Optimisation
```
RecipeService caches all recipes in RAM after first DB call.
Subsequent searches/loads run locally at O(n) — no network round-trip.
Cache is invalidated on any create/update/delete operation.
```

### Data Structures

| Structure | Used In | Purpose |
|---|---|---|
| `ArrayList<Recipe>` | DAO, Service | Recipe lists |
| `ArrayList<Ingredient>` | Recipe model | Ingredient storage |
| `HashMap<String, List<Recipe>>` | SearchService | Inverted index |
| `HashMap<Recipe, Integer>` | SearchService | Match count per recipe |
| `HashMap<String, Double>` | ShoppingList | Ingredient quantity aggregation |
| `ObservableList<CartItem>` | ShoppingListController | Live cart table data |

---

## ✅ Features

| Feature | Description |
|---|---|
| **Login / Register** | Role-based authentication (Admin, RoomLeader, Student) with show/hide password |
| **Dashboard** | Recipe cards with sort by rating / prep time |
| **Recipe CRUD** | Create, view, edit, delete recipes with ingredients & tags |
| **Search** | By title (LIKE), by available ingredients (Inverted Index), by tag filter |
| **Shopping List** | Search ingredients → add to cart with custom quantity → see real-time total price in VND |
| **Admin Controls** | Admin can edit/delete any recipe regardless of author |
| **Cloud Database** | Shared Aiven MySQL — all users see the same live data |
| **Single-window UX** | No new windows; content swapped in BorderPane for smooth navigation |

---

## 🗄️ Database

The app connects to a shared **Aiven Cloud MySQL** database.
Connection is auto-configured on first launch via `run-app.bat` — no manual setup required.

Schema location: `src/main/resources/db/schema.sql`

---

## 🛠️ Manual Build (Advanced)

If you want to build and run manually:

```powershell
# 1. Make sure .env exists (copy from .env.example and fill in credentials)
copy .env.example .env

# 2. Compile
.\apache-maven-3.9.6\bin\mvn.cmd clean compile

# 3. Run
.\apache-maven-3.9.6\bin\mvn.cmd javafx:run
```

---

*Built with Java 17 · JavaFX 21 · MySQL 8 · Aiven Cloud*
