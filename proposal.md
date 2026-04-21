  
**(GROUP 13\) VinRECIPE \- Smart Recipe and Grocery Planner**

**1\. Team introduction**  
\- Nguyen Trong Nhan (Leader) \- V202502098: Python (Intermediate), Java (Intermediate), C++ (Intermediate)  
\- Nguyen Thi Quynh Trang \- V202502665: Python (Intermediate), Java (Beginner)  
\- Nguyen Tien Nhat Nguyen \- V202502943: Java (Intermediate), Python (Intermediate).  
\- Dang Tuan Kiet \- V202502041: Python (Intermediate), Java (Beginner), JavaScript (Beginner)        
\- Do Viet Phuong \- V202502638, Python (Fluent), Java (Beginner).

### **2\. Product Description**

* **Problem identification:** For VinUni students balancing demanding academic schedules, meal planning is often a frustrating cycle of hunting down scattered recipes and making disorganized grocery runs that waste time, money, and shared dorm space. This system solves this by providing a centralized database to easily manage and search for meal ideas, while automatically generating precise grocery lists based on selected ingredients.  
* **The idea, purpose, and objectives:** The objective is to design a system to manage recipes, search for new recipes, and generate shopping lists based on selected ingredients.  
* **The type of product:** This will be a desktop application featuring a graphical user interface (GUI) built with JavaFX, powered by a core Java backend to handle the object-oriented logic and data structures.

### **3\. Target Users**

* **Intended Users:** The primary target users are university students and staff (such as the VinUni community). It is especially useful for students who cook regularly in dorms or apartments, who might need a structured way to catalog precise ingredients, measurements, and recipe variations.  
* **Benefits:** The proposed product will benefit these users by saving them time during meal prep and saving them money at the grocery store. By automating the shopping list generation, users avoid over-purchasing or forgetting key items.

### **4\. Functional Specification**

**System Context and Architecture** 

* **Frontend (GUI):** Built with JavaFX (or Java Swing) to provide a responsive and interactive desktop user interface.  
* **Backend (Core Logic):** Powered by standard Java to handle all business logic, demonstrating the effective use of OOP design (Inheritance, Encapsulation) and data structures.  
    
* **Database:** A MySQL database will be used for the persistent storage of recipes and ingredients.

**Main Features and Functionalities**

* **Recipe Management (CRUD):** The system will allow users to manage recipes by creating, reading, updating, and deleting entries via the desktop application.  
* **Search and Filter:** The application will enable users to search for new recipes based on titles or tags. The backend will fetch data from MySQL and process it using Java data structures like HashSet to ensure unique categories are sent to the GUI dropdown menus.  
* **Automated Shopping List Generator:** The system will generate shopping lists based on selected ingredients. When a user selects multiple recipes on the frontend, the Java backend will use a HashMap to mathematically aggregate and combine duplicate ingredient quantities, returning a clean, consolidated grocery list to the screen.

**Core Class Architecture** To manage the system's data and logic effectively, the backend will be structured around the following main Java classes:

* **User, Admin, NormalStudent, RoomLeader:** Manages account profiles, authentication, and specific campus/dorm permissions.  
* **Room:** Groups students together to facilitate the sharing of meals and grocery lists within a specific dorm room.  
* **Recipe, Ingredient, Tag:** The core data models representing meal instructions, their required components, and search categories.  
* **RecipeService:** Handles the backend logic for searching, filtering, and retrieving recipes from the database.  
* **ShoppingList:** Contains the engine for mathematically aggregating ingredients for grocery runs.

**OOP Implementation Details**

* **Inheritance**: The NormalStudent, RoomLeader, and Admin classes inherit baseline profile data from the parent User class.  
* **Encapsulation**: Core classes like Recipe encapsulate their data using private fields and public getters/setters, ensuring data validation before database interaction. Recipe also uses a List to securely store its specific Ingredient objects.

**Data Structures:**

* **ArrayList**: Stores and manages variable-length lists of Recipe and Ingredient objects.  
    
    
* **HashSet**: Filters out duplicate categories to ensure the GUI search menus only display a clean list of unique tags.  
* **HashMap**: Powers the Automated Shopping List by mapping ingredient names (keys) to their aggregated quantities (values), efficiently combining duplicates.  
* Other data structures that may be used: HashTable

**Algorithm:**

* **Inverted Index Search:** To find the recipe with matching ingredients. The system chooses the recipe with the highest completion percentage, if there are multiple recipes with the same percentage, choose the one with the quickest prep time.  
* **Sorting algorithm**: To sort ratings, prep time, prices, etc.

### **5\. Project Timeline**

| Phase | Name | Description | Start Date | End Date |
| ----- | ----- | ----- | ----- | ----- |
| 1 | Planning and Analysis | Proposal preparation, problem definition, identifying users and key features | March 12 | March 26 |
| 2 | Requirements Definition | Define detailed requirements, use cases, and system scope | March 27 | April 2 |
| 3 | Design and Prototyping | UML diagrams, database schema, UI mockups | April 3 | April 12 |
| 4 | Implementation | Develop backend, frontend, core features | April 13 | May 20 |
| 5 | Refactoring and Feature Development | Optimize code, improve UI, refine features  | May 21 | May 25 |
| 6 | Testing and Deployment | Testing (unit \+ integration), debugging, prepare stable demo version | May 26 | May 30 |
| 7 | Finalization and Documentation | Final report, slides, demo preparation | May 31 | June 2 |

**6\. Appendix** 

### 

### **Term Project Team Agreement Form**

 **Course:** **COMP1020**  
 **Group Number: 13**  
 **Project Title:** **VinRECIPE \- Smart Recipe and Grocery Planner**  
 **Team Leader:** Nguyen Trong Nhan  
 **Date:** 19/03/2026

---

#### **Team Member Information**

| Name | Student ID | Signature / Acknowledgment |  |
| ----- | ----- | ----- | :---- |
| Member 1 Nguyen Trong Nhan | V202502098 |  | ![][image1] |
| Member 2 Nguyen Thi Quynh Trang | V202502665 |  | ![][image2] |
| Member 3 Nguyen Tien Nhat Nguyen | V202502943 |  | ![][image3]w |
| Member 4 Dang Tuan Kiet | V202502041 |  | **![][image4]** |
| Member 5 Do Viet Phuong | V202502638 |  |  |

---

#### **Agreement Terms**

By signing this agreement, we, the members of the above project team, acknowledge and agree to the following:

1. **Equal Commitment**: We are each responsible for contributing actively and fairly to the project's success.  
2. **Role Assignment**: We will collaboratively determine task assignments based on individual strengths and project requirements.  
3. **Communication**: We agree to communicate regularly, respond promptly to messages, and attend scheduled team meetings.

4. **Accountability**: We understand that individual contributions will be evaluated, and non-participation may negatively affect final grades.  
5. **Conflict Resolution**: We will attempt to resolve conflicts internally; if unresolved, the issue will be reported to the instructor.  
6. **Reporting Non-Participation**: The team leader will report any non-contributing members, and members may also report non-performing leaders.

---

**Acknowledgment:**  
We understand and agree to abide by the terms outlined above. Failure to meet these expectations may result in individual penalties.

[image1]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAE4AAAA0CAYAAAAkEw66AAADn0lEQVR4Xu2Z60sVQRTA/Tt7SV+KyCiKKIqIoCKoSCqsQCgNE0LQIqObj+gl+eihPS1MKcpMLSnN9273dJ1279k5s2fO3r3drvP7ovfMmdmZ353dPbu3wneIqMABBw8nTogTJ8SJE+LECXHihDhxQkpe3PySh0MlQdHFzSx6fu1jnow1dct//j7/ihpKgETi1MK4zGalne/LSYvri9sv9fNkF4uiiuPmt76LSuL2LRaJxXV9jC6SovYpL3djY1RSWYnb3LTMXlDLIE9a5RX9eNzjFItE4kAGd0HcvN039Xk7b+jj/wqxuMxQbgd5nuc3vojfTRxx479wJODJl/hjFBOxuL23AhEcKVXX4nPWMsYpFcTiwrL2ZeJ3w9D3+BzqC6gfiO+LqX6Uu4xQYyalIOIKRTNxA7E9FuRDzZgmYnH47lfZQC+ufwxH7Nh+nR4bo5OsiyXFWhw8MgH3P+R/oxsM4jjXNzi1dKy/HN9XQQnak9HHk2At7kL2kWnJiy4SYhefReMAtaAwVA4Vxxxo9/yFZf3xgZoeuk2CtTiYQD3x3EgtkoqH0eXYlCBbms25uvGTYC0OrlfUJHpH9ZOn8sPocnQxCqgnTZzqMrfbYi0OMC3o5MNo2/EH0RgGjwmfJwwFMWZsBkeimE5lWwouTtf2ahxHooT7qf9tCuLDd+PrNqpN1Xw2FFycpPQYmQomHl78InOHnMju8jcTwWdqflQcbiyAzR284OKAml7egsOoMUHA28kgfqjTPBa0/5zPj90Z8f25lVfuVJmjOHovaD9nMe9UxIXbuRV8eMfp4hTU6QyPgUdWTl8damfjdvyZIhVxt9/7f3cBt6RQY+Inkl2t5mP1fNaPr6RQYhX72/L7x61NkYo4QOXUETUfxjRmwwCOxKPETc3hlnxwGXOwgzff1MRtXSlIj4WuISZgzI5hHM3BOR5Gdxpy6CNqUUxq4gC4vu1gvrk9022+HlGcJR6loA/cbSmqswVx5wiO8rEWB3cr00LCQN6mJl7ut1nfX1evz6WuYwA1F4i/DpUoGGi3eeuCsRb36QdfHLxq4ooD2odxJGB6QS8PfunHwtX8Tnfr+wDwFMFdhw5rcYOT9LeswybXBPyuAWNtI15RgfSrL/NFVbXkpHILaRv+G3GlhrW40elkW7xcsBYH2DzTlSsicfAmYrUjEmf64Xi1IBLncOLEOHFCnDghTpwQJ06IEyfEiRPixAlx4oQ4cUJ+A6fWeFdH9ToTAAAAAElFTkSuQmCC>

[image2]: <data:image/png;base64,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>

[image3]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAFEAAAA3CAYAAAB0IqZxAAADLklEQVR4Xu2ZPY7VMBSFZ2P0bIAFsAAWwAJYABtgAYiGZhoqKKYZCdHQQEODkGgoEALe/AQdJA+Xo2vn3OT6ZQj+pJHyHMe+/q7jOJmTabCaEy4YxBkSExgSExgSExgSExgSExgSExgSExgSEwhJvPPsOxcNpiExhd1LRMzl7/6LH3w6hS4S1Xq94TgenR+mx28Of5VlEJJ471TLJAd/bL4erqsx1MrXIEt8/fnqdyYVegQaodX/6YfL6d2XKy5ehSzx6fuL6cnbCy52aQ2iN4qgu89z45MlQiBmo8KWEhWy45MlPnj585+YiQrZ8ckS0bEqsddWIgtMiExCEtXO1XpboS5LKiGJ6m2gzti9kC4RWc7O9G2ni8T/jXSJ6oZ8T4QkKu+dt+GhoiY8i5BEhbl6rz5e3jx4MoV/+nZ9c4x28f5sy6Lg2rmxFMIS516rWh3jnF0zW3WjPDz7k5CSnLn2EQteAZFYZu5aS1jiXOO1894GvFZ3Cbatctz66oSZWt6hOQ58J4g8II8isfbCb8txnXd7Y0Ae3I8nsbVf9ep7v5UlQZZYBoiHC7JYw8s+BwkgsAwSn6fQrlfPE4s+uNyT0ppNVjCO7Zi8MbQISwTeYAHWSw4cvyGJ8QbNx8DbEaCOrYf1kKViq1WbidyHLVuyRZMl2oDK04/xblsI5FsC1/KgLXah925nnrU4Ztkoq/XhScTs43ZVFkkEXmdeGeDs2pnkXcOCuI4tw8ARG8eHPvk64JXV8BLoIUv0GuSAvNsW4MmMwWJmsDxuo8DnIQnHdrmw6zPPdsBtz90BDF9fQ5bIax0o2wScUzssoH7rIVU2u7XzCriedwAR1PqrJBZa52ogQDXIpWBtLbd1NCFIIq+zNSSJPT5v8SzJZu7Nao5IgiWJoIfEtQNlbIw9E8TIEvnpt5ZIplVsjD3aryFLzM5sj0Faibyt6oksMTuonhKjr21rkSVm00NiSXSPtlvsSuKYiQlAYvbarbCJRGyCe0hc8uaUwSYS8emqx2DLG8qx2UQiBEY+BKj0SIzCkJjAZhK9/7CtJfKBIZPNJO6JTSTujSExgSExgSExgV884nMwN3cDmwAAAABJRU5ErkJggg==>

[image4]: <data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADQAAAAnCAYAAABAOmveAAAB/0lEQVR4Xu2YzS8DQRTA+3f6Ci5OLmhE0qPEwcXBzQmpRuLzgJSKEBxITxKCtIIDEjRSNMruymRNun1m5s28zrZL9ndp+nbmdX47r2+nTXj/jAQM/HVioagTCzHaph0YIlOqeF7/quNdPMErNKwLua4LQ1I6ZurzLJzqz5VBEhpaFwupRCFT+cYXL4IktHwWzmJsQBIqlH4LmexOmJCEyh8wYs6ihe+LCJIQpN3C7pSrdgStCDUKL1cbZUsS+gq0Zth6IQ9vMCKnZUK2yoMRlGiZ0H3Zfz2+syfGyBVhxKdz1tGWJQnxtq0qN5NSE9GVdrzXn0rQlWGQhPIaO6OziGyhlqd3Hh+vA0lo+wpfMHadERwzcai+Sakt9XUOSWjl3PUc5BBqKoShO5YkxJJjh8uRTXwBozt+joMbdS7G7Qs+hkESmjxy0TumczxKbvg5sFyc8X18nLEQ+/CxPfPuI0J3Pu+mg2v4eGMhRipX237dRYnQnZs+gRE5JCFeKhzZwqoOXveyuTKKgp8uQUhCA4KtFy1MFINcPsNIPbxxcLCc1oQYu9f17zMapaJ6vvAjlgkkIdVdgu1V9bzqW/LzqPKZYl2IEbzek5GPTQb+bAnOGc7K52CEIgR5fIeR8GiKUDOJhRjdc+ELVT7lzUQFSagZUKsgskJUYqGoEwtFnW8l1kZ2lYvypQAAAABJRU5ErkJggg==>