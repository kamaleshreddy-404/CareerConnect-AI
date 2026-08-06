-- ============================================================
-- CareerConnect AI Database Schema & Initial Seed Data
-- Database Engine: MySQL 8.0+
-- Project: CareerConnect AI Job Portal
-- ============================================================

CREATE DATABASE IF NOT EXISTS careerconnect_db;
USE careerconnect_db;

-- 1. Users Table
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    role ENUM('JOB_SEEKER', 'RECRUITER', 'ADMIN') NOT NULL DEFAULT 'JOB_SEEKER',
    bio TEXT,
    profile_pic VARCHAR(255) DEFAULT 'default_avatar.png',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Companies Table
CREATE TABLE IF NOT EXISTS companies (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(150) NOT NULL,
    website VARCHAR(255),
    logo VARCHAR(255) DEFAULT 'default_company.png',
    description TEXT,
    location VARCHAR(150),
    industry VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. Recruiters Table
CREATE TABLE IF NOT EXISTS recruiters (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    company_id INT NOT NULL,
    position VARCHAR(100),
    status ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'APPROVED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE
);

-- 4. Job Categories Table
CREATE TABLE IF NOT EXISTS job_categories (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    icon VARCHAR(50) DEFAULT 'code',
    description TEXT
);

-- 5. Jobs Table
CREATE TABLE IF NOT EXISTS jobs (
    id INT PRIMARY KEY AUTO_INCREMENT,
    company_id INT NOT NULL,
    recruiter_id INT NOT NULL,
    category_id INT NOT NULL,
    title VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    requirements TEXT,
    location VARCHAR(100) NOT NULL,
    job_type ENUM('FULL_TIME', 'PART_TIME', 'INTERNSHIP', 'REMOTE', 'CONTRACT') NOT NULL DEFAULT 'FULL_TIME',
    salary_range VARCHAR(50),
    experience_level VARCHAR(50),
    status ENUM('ACTIVE', 'CLOSED', 'FLAGGED') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE CASCADE,
    FOREIGN KEY (recruiter_id) REFERENCES recruiters(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES job_categories(id) ON DELETE CASCADE
);

-- 6. Applications Table
CREATE TABLE IF NOT EXISTS applications (
    id INT PRIMARY KEY AUTO_INCREMENT,
    job_id INT NOT NULL,
    user_id INT NOT NULL,
    resume_path VARCHAR(255),
    cover_letter TEXT,
    status ENUM('APPLIED', 'UNDER_REVIEW', 'ACCEPTED', 'REJECTED') DEFAULT 'APPLIED',
    ai_score INT DEFAULT 75,
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 7. Saved Jobs Table
CREATE TABLE IF NOT EXISTS saved_jobs (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    job_id INT NOT NULL,
    saved_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (job_id) REFERENCES jobs(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_job (user_id, job_id)
);

-- 8. Notifications Table
CREATE TABLE IF NOT EXISTS notifications (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    title VARCHAR(150) NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 9. Skills Table
CREATE TABLE IF NOT EXISTS skills (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE
);

-- 10. User Skills Table
CREATE TABLE IF NOT EXISTS user_skills (
    user_id INT NOT NULL,
    skill_id INT NOT NULL,
    PRIMARY KEY (user_id, skill_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (skill_id) REFERENCES skills(id) ON DELETE CASCADE
);

-- 11. Education Table
CREATE TABLE IF NOT EXISTS education (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    degree VARCHAR(100) NOT NULL,
    institution VARCHAR(150) NOT NULL,
    field_of_study VARCHAR(100),
    start_year INT,
    end_year INT,
    grade VARCHAR(20),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 12. Experience Table
CREATE TABLE IF NOT EXISTS experience (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    title VARCHAR(100) NOT NULL,
    company_name VARCHAR(100) NOT NULL,
    location VARCHAR(100),
    start_date VARCHAR(30),
    end_date VARCHAR(30),
    description TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 13. Resume Table
CREATE TABLE IF NOT EXISTS resume (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(255) NOT NULL,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ai_score INT DEFAULT 0,
    ai_feedback TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 14. Admin Table
CREATE TABLE IF NOT EXISTS admin (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL UNIQUE,
    permissions VARCHAR(255) DEFAULT 'ALL',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ============================================================
-- INITIAL SEED DATA
-- ============================================================

-- Categories
INSERT INTO job_categories (id, name, icon, description) VALUES
(1, 'Software Engineering', 'code', 'Frontend, Backend, and Full Stack Engineering roles'),
(2, 'Data Science', 'database', 'Data Analytics, Big Data, and Business Intelligence'),
(3, 'AI / Machine Learning', 'cpu', 'Machine Learning Models, NLP, and Computer Vision'),
(4, 'Cloud Computing', 'cloud', 'AWS, Azure, DevOps, and Infrastructure Systems'),
(5, 'Cyber Security', 'shield', 'Information Security, Ethical Hacking, and Auditing'),
(6, 'Web Development', 'globe', 'HTML, CSS, JavaScript, React, and Java Web Applications'),
(7, 'Mobile Development', 'smartphone', 'Android, iOS, Flutter, and React Native'),
(8, 'UI UX Design', 'layout', 'Product Design, Wireframing, and User Experience'),
(9, 'Digital Marketing', 'trending-up', 'SEO, Content Strategy, and Growth Marketing'),
(10, 'Finance', 'dollar-sign', 'Financial Planning, Investment, and Accounting'),
(11, 'HR', 'users', 'Talent Acquisition, Placement, and Campus Relations'),
(12, 'Sales', 'shopping-bag', 'B2B Sales, Business Development, and Account Management')
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- Default Users (Password for all seed users is 'password123')
INSERT INTO users (id, name, email, password, phone, role, bio) VALUES
(1, 'System Administrator', 'admin@careerconnect.ai', 'password123', '9876543210', 'ADMIN', 'CareerConnect AI System Administrator'),
(2, 'TechCorp Recruiter', 'recruiter@techcorp.com', 'password123', '9876543211', 'RECRUITER', 'Senior Technical Recruiter at TechCorp Innovations'),
(3, 'GlobalSoft HR', 'hr@globalsoft.com', 'password123', '9876543212', 'RECRUITER', 'Campus Recruitment Lead at GlobalSoft'),
(4, 'Alex Johnson', 'alex.student@college.edu', 'password123', '9876543213', 'JOB_SEEKER', 'Final year Computer Science student specializing in Java & Web Dev'),
(5, 'Sarah Miller', 'sarah.m@college.edu', 'password123', '9876543214', 'JOB_SEEKER', 'Data Science & AI enthusiast looking for Entry-Level roles')
ON DUPLICATE KEY UPDATE email=VALUES(email);

-- Admin Record
INSERT INTO admin (user_id, permissions) VALUES (1, 'FULL_CONTROL')
ON DUPLICATE KEY UPDATE permissions=VALUES(permissions);

-- Companies
INSERT INTO companies (id, name, website, logo, description, location, industry) VALUES
(1, 'TechCorp Innovations', 'https://techcorp.com', 'techcorp_logo.png', 'Leading Cloud & Enterprise Software Solutions Company', 'Bangalore, India', 'IT & Software'),
(2, 'GlobalSoft Solutions', 'https://globalsoft.com', 'globalsoft_logo.png', 'Global Product Engineering & Digital Transformation Leader', 'Hyderabad, India', 'Information Technology'),
(3, 'Nexus AI Labs', 'https://nexusai.com', 'nexus_logo.png', 'Cutting edge Artificial Intelligence & Machine Learning Research', 'Pune, India', 'Artificial Intelligence')
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- Recruiters Link
INSERT INTO recruiters (id, user_id, company_id, position, status) VALUES
(1, 2, 1, 'Lead Technical Recruiter', 'APPROVED'),
(2, 3, 2, 'Campus Placement Manager', 'APPROVED')
ON DUPLICATE KEY UPDATE status=VALUES(status);

-- Skills
INSERT INTO skills (id, name) VALUES
(1, 'Java'), (2, 'Spring Boot'), (3, 'React'), (4, 'Python'),
(5, 'MySQL'), (6, 'AWS'), (7, 'Docker'), (8, 'Machine Learning'),
(9, 'Data Analysis'), (10, 'HTML/CSS/JS')
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- User Skills
INSERT INTO user_skills (user_id, skill_id) VALUES
(4, 1), (4, 3), (4, 5), (4, 10),
(5, 4), (5, 8), (5, 9), (5, 5)
ON DUPLICATE KEY UPDATE user_id=VALUES(user_id);

-- Education Seed
INSERT INTO education (user_id, degree, institution, field_of_study, start_year, end_year, grade) VALUES
(4, 'B.Tech in Computer Science', 'State Institute of Technology', 'Computer Science & Engineering', 2021, 2025, '8.9 CGPA'),
(5, 'B.Tech in Data Science', 'National Institute of Technology', 'Artificial Intelligence & Data', 2021, 2025, '9.2 CGPA')
ON DUPLICATE KEY UPDATE user_id=VALUES(user_id);

-- Experience Seed
INSERT INTO experience (user_id, title, company_name, location, start_date, end_date, description) VALUES
(4, 'Software Engineer Intern', 'DevSolutions Inc', 'Remote', '2024-05', '2024-08', 'Developed REST APIs using Java Servlets, React, and MySQL.')
ON DUPLICATE KEY UPDATE user_id=VALUES(user_id);

-- Jobs
INSERT INTO jobs (id, company_id, recruiter_id, category_id, title, description, requirements, location, job_type, salary_range, experience_level, status) VALUES
(1, 1, 1, 1, 'Graduate Software Engineer (Java & React)', 
 'We are looking for passionate graduate engineers to join our flagship cloud products team. You will build high performance web applications using Java Servlets/Spring and React.',
 'B.Tech CS/IT graduate. Strong fundamentals in Java, Data Structures, OOPs, SQL, and Web Technologies.', 
 'Bangalore, India', 'FULL_TIME', '₹8,00,000 - ₹12,00,000 LPA', 'Entry Level (Freshers)', 'ACTIVE'),

(2, 1, 1, 6, 'Frontend Web Developer Intern', 
 'Exciting 6-month internship opportunity for students skilled in React, HTML5, CSS3, and JavaScript. High conversion possibility to full-time.',
 'Hands-on experience with modern React, responsive design, REST APIs, and UI animations.', 
 'Hybrid - Bangalore', 'INTERNSHIP', '₹25,000 / month Stipend', 'Internship', 'ACTIVE'),

(3, 2, 2, 2, 'Junior Data Analyst', 
 'Join GlobalSoft AI analytics group to extract business insights from complex big data streams using SQL and Python.',
 'Proficiency in MySQL, Python, Pandas, Tableau/PowerBI, and statistical modeling.', 
 'Hyderabad, India', 'FULL_TIME', '₹7,50,000 - ₹10,00,000 LPA', 'Entry Level', 'ACTIVE'),

(4, 3, 1, 3, 'AI Systems Developer Trainee', 
 'Build next-gen Generative AI and NLP workflows. Excellent opportunity for college graduates with strong algorithmic background.',
 'Strong mathematical background, Python, PyTorch/TensorFlow, RESTful services, and Git.', 
 'Pune, India (Remote)', 'REMOTE', '₹10,00,000 - ₹15,00,000 LPA', 'Entry Level', 'ACTIVE')
ON DUPLICATE KEY UPDATE title=VALUES(title);

-- Sample Applications
INSERT INTO applications (id, job_id, user_id, resume_path, cover_letter, status, ai_score) VALUES
(1, 1, 4, 'alex_johnson_resume.pdf', 'I am excited to apply for the Graduate Software Engineer position. I have built full stack Java MVC applications and React components.', 'UNDER_REVIEW', 88),
(2, 3, 5, 'sarah_miller_resume.pdf', 'I hold a 9.2 CGPA in AI & Data Science with hands-on projects in Python and MySQL analytics.', 'ACCEPTED', 94)
ON DUPLICATE KEY UPDATE status=VALUES(status);

-- Sample Saved Jobs
INSERT INTO saved_jobs (user_id, job_id) VALUES
(4, 2), (4, 4)
ON DUPLICATE KEY UPDATE user_id=VALUES(user_id);

-- Sample Notifications
INSERT INTO notifications (user_id, title, message) VALUES
(4, 'Application Update', 'Your application for Graduate Software Engineer at TechCorp is now UNDER REVIEW.'),
(5, 'Shortlisted!', 'Congratulations! GlobalSoft Solutions has ACCEPTED your application for Junior Data Analyst.')
ON DUPLICATE KEY UPDATE user_id=VALUES(user_id);



USE careerconnect_db;
SHOW TABLES;