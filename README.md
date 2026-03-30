# 💬 Lovable Clone (AI + Payments + Auth)

A full-stack backend project inspired by Lovable, built using **Spring Boot**, integrating **AI chat**, **JWT authentication**, **Stripe payments**, and **cloud-ready architecture**.

---

## 🚀 Features

* 🔐 JWT-based Authentication & Authorization
* 🤖 AI Chat Integration (OpenRouter / OpenAI APIs)
* 💳 Stripe Payment Gateway Integration
* 📂 File Handling (MinIO for local object storage)
* 🧠 LLM Response Parsing System (Custom Chat Event Parser)
* 🔄 RESTful APIs with clean architecture
* ⚙️ Environment-based configuration (.env support)

---

## 🛠️ Tech Stack

**Backend:**

* Java
* Spring Boot
* Spring Security
* Spring Data JPA (Hibernate)

**Database:**

* PostgreSQL

**DevOps & Tools:**

* Docker (optional)
* MinIO
* Git & GitHub

**Integrations:**

* OpenRouter / OpenAI (LLM APIs)
* Stripe (Payments)

---

## 📁 Project Structure

```
lovable-clone/
│── src/
│   ├── main/
│   │   ├── java/com/MayukhProjects/
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── repository/
│   │   │   ├── entity/
│   │   │   ├── config/
│   │   │   └── llm/
│   │   └── resources/
│   │       ├── application.yaml
│
│── .env.example
│── README.md
```

---

## ⚙️ Setup Instructions

### 1️⃣ Clone the repository

```bash
git clone https://github.com/Mayukh10/lovable-clone
cd lovable-clone
```

---

### 2️⃣ Setup Environment Variables

Create a `.env` file in root:

```env
DB_USERNAME=postgres
DB_PASSWORD=your_password

JWT_SECRET=your_jwt_secret

STRIPE_SECRET_KEY=your_stripe_key
STRIPE_WEBHOOK_SECRET=your_webhook_secret

OPENAI_API_KEY=your_api_key
```

---

### 3️⃣ Configure Database

Make sure PostgreSQL is running:

```sql
CREATE DATABASE lovableDB;
```

---

### 4️⃣ Run the Application

```bash
mvn spring-boot:run
```

Server runs on:

```
http://localhost:8081
```

---

## 🔐 Security Practices

* Secrets are stored in `.env` (not committed)
* `.gitignore` prevents sensitive file exposure
* JWT used for secure authentication
* API keys are environment-driven

---

## 💳 Stripe Integration

* Payment intent creation
* Webhook handling
* Secure key management via environment variables

---

## 🤖 AI Integration

* Uses OpenRouter/OpenAI APIs
* Supports structured LLM responses
* Custom parser converts responses into Chat Events

---

## 📌 Future Improvements

* ✅ Frontend integration (React / Next.js)
* 🚀 Deployment (AWS / Docker / CI-CD)
* 📊 Analytics dashboard
* 🔄 WebSocket real-time chat

---

## 👨‍💻 Author

**Mayukh Chakraborty**
Backend Developer | DevOps Enthusiast

---

## ⭐ Show Your Support

If you like this project, give it a ⭐ on GitHub!
