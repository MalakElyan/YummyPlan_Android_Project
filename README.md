# 🥗 YummyPlan - Android Meal Management & Weekly Planner

---

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white) 
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![SQLite](https://img.shields.io/badge/SQLite-003B57?style=for-the-badge&logo=sqlite&logoColor=white) 
![Android Studio](https://img.shields.io/badge/Android\_Studio-3DDC84?style=for-the-badge&logo=android-studio&logoColor=white) 
![Git](https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white)


**YummyPlan** is a fully-featured Android application designed for meal management and weekly diet planning. Built with **Java** in **Android Studio**, it utilizes an **SQLite** database for efficient, fast local data storage and seamless performance.

---

## 📱 Key Features

* **Weekly Meal Planning:** Effortlessly organize and manage daily and weekly meal schedules.
* **Session & User Management:** Secure user registration and login with persistent session handling via `SharedPreferences`.
* **Dynamic & Smooth Data Display:** Powered by `RecyclerView` with custom adapters for high-performance rendering of meal lists.
* **Real-time Animations & Updates:** Instant list updates upon item deletion or modification using `notifyItemRemoved` for an interactive UX.
* **User Data Protection:** Confirmation `AlertDialogs` to prevent accidental deletions.
* **Modern & Safe UI:** Integrated with **View Binding** for maximum layout execution speed and compile-time null safety.

---

## 🛠️ Tech Stack & Architecture

* **Programming Language:** Java
* **IDE:** Android Studio
* **Local Database:** SQLite (`SQLiteOpenHelper`)
* **Architecture & UI Components:** 
  * View Binding (Type Safety & Null Safety)
  * RecyclerView & ViewHolder Pattern
  * Custom Adapters & Dynamic Layouts
  * XML Layouts with `tools:` Attributes for UI Design Preview
* **Local Data Storage:** `SharedPreferences` (Session Management)
* **Version Control:** Git & GitHub

---

## 🏗️ Technical Highlights

### 1. SQLite Database Persistence
The custom `DatabaseHelper` class extends `SQLiteOpenHelper` to handle dynamic database creation (`onCreate`) on first launch and execute database queries efficiently.

### 2. View Binding & Performance Optimization
* Standard `findViewById` calls are replaced with **View Binding** to avoid `NullPointerException` issues and improve layout rendering efficiency.
* Strict implementation of the **ViewHolder Pattern** inside the `RecyclerView` ensures view recycling and optimizes memory usage during scrolling.

---

## 🚀 How to Run

1. Clone the repository:
   ```
   git clone [https://github.com/MalakElyan/YummyPlan_Android_Project.git](https://github.com/MalakElyan/YummyPlan_Android_Project.git)
    ```
2. Open the project in Android Studio.
3. Wait for the Gradle Sync to complete.
4. Run the app on an Android Emulator or physical device using Run ▶.

---

👩‍💻 Author & Developer
Developer: Malak Elyan.
Specialization: Mobile Application Development & UI/UX Design

---
## 🔗 Connect with Me
- 🐙 **GitHub:** [@MalakElyan](https://github.com/MalakElyan) 
- 💼 **LinkedIn:** [Malak Elyan](https://www.linkedin.com/in/malak-elyan) 

---
⭐ If you found this project useful, feel free to star it!
