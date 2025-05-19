# 👥 Persons Finder – Backend Challenge

Welcome to the **Persons Finder** backend challenge! This project simulates the backend for a mobile app that helps users find people around them.

Your task is to implement a REST API that allows clients to create, update, and search for people based on location and other criteria.

---

## 📌 Requirements

Implement the following endpoints:

### ➕ `POST /persons`

Create a new person.

---

### ✏️ `PUT /persons/{id}/location`

Update (or create if not exists) a person's current **latitude** and **longitude**.

---

### 🔍 `GET /persons/nearby`

Find people around a **query location**, specified using the following query parameters:

* `lat`: latitude
* `lon`: longitude
* `radiusKm`: radius in kilometres

> 🧠 **Extra challenge**: Return the list **sorted by distance** to the query point.

---

### 👤 `GET /persons`

Retrieve one or more persons by their IDs. Accepts:

* `id`: one or more person IDs (e.g., `?id=1&id=2`)

---

## 📦 Expected Output

All responses must be in **valid JSON format**, following clean and consistent REST API design principles.

---

## 🧱 What You Need to Build

* Domain models: `Person`, `Location`, etc.
* Services for saving, updating, and querying data
* In-memory storage or a basic persistent layer
* Proper project structure (e.g. controller / service / repository)
* Extra bonus if you use UseCase pattern (Controller -> Use Case (business logic) -> Service -> Repository)

---

## 🧪 Bonus Points

### ✅ Testing

* Include **unit tests** for service logic
* Include **integration tests** for API endpoints
* Use a test framework like **JUnit**, **MockK**, or **Mockito**

---

### 🧠 Scalability Challenge

* Seed the system with **1 million**, **10 million**, and **100 million** records
* Benchmark and **optimise** the `GET /persons/nearby` endpoint
* Explain any indexing or query optimisation strategies used

---

### 📚 Clean Code

* Use **DTOs** for API request and response bodies
* Apply proper **validation**, **error handling**, and maintain clean separation of concerns

---

## ✅ Getting Started

```bash
git clone https://github.com/leonardoduartelana/persons-finder.git
cd persons-finder
```

Implement your solution and push it to your **own GitHub repository**.

---

## 📬 Submission & Questions

* Submit the link to your GitHub repository
* For any questions, email: [leo@emerge.nz](mailto:leo@emerge.nz)

---

## 💡 Tips

* Use **OpenAPI/Swagger** to document your APIs (optional, but encouraged)
* Handle edge cases like missing locations or malformed input
* Design the system **as if it were going into production**
