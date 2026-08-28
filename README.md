# CareerConnect AI - Full Stack AI-Powered Job Portal

CareerConnect AI is a comprehensive, interview-ready **Full Stack AI-Powered Job Portal** designed for college placement drives and corporate recruitment.

Built using the standard **Java MVC Architecture** (Java Servlets, JSP, JDBC, DAO pattern, MySQL, Maven) with a modern responsive blue-and-white visual design and React UI components.

---

## 🚀 Key Features

### 🔐 Authentication Module
- Multi-role registration (Job Seeker / Recruiter)
- Role-based Login with automatic dashboard routing
- Role-Based Security Filter (`AuthFilter`)
- Password recovery and reset mechanism
- Session invalidation & safe Logout

### 👨‍🎓 Job Seeker Module
- Candidate Overview Dashboard & Application Status Tracker
- Interactive **AI Resume Match & ATS Optimizer** powered by React
- Browse, Search, and Filter jobs by Keyword, Category, Location, and Job Type
- One-click Job Application & Resume PDF Manager
- Real-time Notifications feed for recruiter application decisions

### 🏢 Recruiter Module
- Recruiter Dashboard & Company Profile Manager
- Create, Edit, Delete, and Manage Job Openings
- Candidate Application Screening Board (Accept / Reject applications)
- Download candidate Resume PDFs
- Instant notification dispatch to applicants upon status updates

### 🛡️ Admin Module
- System Administrator Dashboard with Platform Analytics (Users, Recruiters, Postings, Applications)
- Recruiter Verification & Approval Queue
- Fraud Control: Flag or remove fake job listings
- Job Category Management (Add / Delete)
- Platform User Directory

---

## 🛠️ Technology Stack & Architecture

- **Frontend Layer**: HTML5, CSS3 (Modern Blue & White Design System + Dark Mode), Vanilla JavaScript (`app.js`), React (`components.js`)
- **Controller Layer**: Java Servlets (`AuthServlet`, `JobServlet`, `JobSeekerServlet`, `RecruiterServlet`, `AdminServlet`, `AIResumeServlet`, `FileServlet`)
- **Model Layer**: Java Beans (DTOs), Data Access Objects (`DAO` pattern), `DBConnection`
- **Database**: MySQL 8.0+ (`schema.sql` with 14 normalized tables and seed data)
- **Build & Packaging**: Apache Maven (`pom.xml`, WAR package)
- **Server**: Apache Tomcat 9.0+ / 10.0+

---

## 📂 Project Folder Structure

```
CareerConnect AI/
├── pom.xml                     # Maven configuration & dependencies
├── schema.sql                  # MySQL database schema & seed data
├── README.md                   # Setup guide & technical documentation
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── careerconnect/
        │           ├── controller/     # Java Servlets
        │           ├── dao/            # Data Access Objects & JDBC Connection
        │           ├── filter/         # Security Filter
        │           └── model/          # Java Beans / DTOs
        └── webapp/
            ├── WEB-INF/
            │   └── web.xml             # Deployment Descriptor
            ├── css/
            │   └── style.css           # Blue & White Theme + Dark Mode
            ├── js/
            │   ├── app.js              # Theme switcher, Toast & Modal controls
            │   └── components.js       # React UI Components
            ├── includes/               # Reusable JSPs (navbar, footer, sidebar)
            ├── index.html              # Primary Landing Page
            ├── index.jsp               # JSP Landing Page
            ├── login.jsp               # Multi-role Login Page
            ├── register.jsp            # User/Recruiter Registration
            ├── jobs.jsp                # Job Directory & Filter Page
            ├── job-detail.jsp          # Job Detail & Apply Modal Page
            ├── user-dashboard.jsp      # Candidate Portal
            ├── recruiter-dashboard.jsp # Employer Portal
            ├── admin-dashboard.jsp     # Administrator Control Center
            └── profile.jsp             # Candidate Profile & Resume Page
```

---

## ⚙️ How to Build and Run

### 1. Database Setup (MySQL)
1. Open MySQL Workbench or Command Line.
2. Execute the `schema.sql` script:
   ```sql
   SOURCE d:/CareerConnect AI/schema.sql;
   ```
3. Verify that the `careerconnect_db` database is created with 14 tables and pre-loaded seed data.

### 2. Build with Maven
In the project root directory, run:
```bash
mvn clean package
```
This produces `CareerConnectAI.war` inside the `target/` directory.

### 3. Deploy on Apache Tomcat
1. Copy `target/CareerConnectAI.war` to your Tomcat `webapps/` folder.
2. Start Tomcat (`bin/startup.bat` or `bin/startup.sh`).
3. Open your browser and navigate to:
   `http://localhost:8080/CareerConnectAI/`

---

## 🔑 Demo Credentials for Technical Interview

| Role | Email | Password |
|---|---|---|
| **Candidate / Student** | `alex.student@college.edu` | `password123` |
| **Recruiter / Employer** | `recruiter@techcorp.com` | `password123` |
| **System Administrator** | `admin@careerconnect.ai` | `password123` |

---

## 🎓 Technical Interview Q&A Guide for Students

1. **How is MVC implemented in this project?**
   - **Model**: Java Beans (`User`, `Job`, `Application`) represent business objects. Data logic is isolated in DAO classes using JDBC.
   - **View**: JSP pages enhanced with React components present the UI.
   - **Controller**: Java Servlets inspect request paths, interact with DAOs, set request attributes, and forward/redirect to JSPs.

2. **How does JDBC prevent SQL Injection?**
   - All DAO implementations use `PreparedStatement` with parametrized queries (`?`), ensuring user inputs are escaped safely before SQL compilation.

3. **How is Role-Based Security enforced?**
   - `AuthFilter.java` intercepts request URIs to check if the session contains an authenticated user with the required role before granting access to dashboard pages.

4. **How does the AI Resume Analyzer work?**
   - `AIResumeServlet` accepts resume content and target job roles, performs keyword token matching against industry standard criteria, calculates an ATS match percentage score, and returns tailored recommendations via JSON.
