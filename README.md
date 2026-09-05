# Luxe Vista Resort - Mobile Application

An Android mobile application designed to provide users with a convenient and user-friendly experience for exploring and booking resort accommodations and services. This app features interactive interfaces, local database management using SQLite, and distinct portals for Guests and Administrators.

## 🎥 Video Demonstration
**[Watch the App Demo on YouTube](https://youtu.be/9yPTSlVx5r0?si=-JH2cBUTpHBL4Xgd)**

## 📱 Key Features
* **User Authentication:** Secure login and registration for guests.
* **Role-Based Access:** Dedicated `ADMIN` and `GUEST` roles.
* **Room & Service Booking:** Users can browse available rooms and spa/dining services, view prices, and book dates.
* **Admin Dashboard:** Administrators can view today's bookings, manage room inventory, and add new services dynamically.
* **Local Persistence:** All data, bookings, and user profiles are securely stored on the device using a custom SQLite implementation (`DatabaseHelper`).

## 🛠️ Technologies Used
* **Platform:** Android (Java)
* **IDE:** Android Studio
* **Database:** SQLite (Local Device Storage)
* **UI/UX:** Native XML Layouts, Toast notifications, Custom Recycler Views

## 🚀 Getting Started

1. **Clone the repository**
   ```bash
   git clone https://github.com/HimashaWeerasekara02/LuxeVistaResort.git
   ```
2. **Open in Android Studio**
   * Launch Android Studio.
   * Select `File > Open` and navigate to the cloned directory.
   * Wait for Gradle to sync completely.
3. **Run the Application**
   * Connect an Android device or start an Emulator.
   * Click the **Run** button (green play icon).

## 🔐 Default Admin Credentials
For testing purposes, the application initializes with a default administrator account:
* **Email:** `admin@luxevista.com`
* **Password:** `admin123`
