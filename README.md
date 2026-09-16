<p align="center"><img width="181" height="184" alt="Frame 26087191" src="https://github.com/user-attachments/assets/76aa3060-63b2-42a4-87a2-b51b7e2cde5a" /></p>
<h1 align="center">United Poultry</h1>
<p align="center">A comprehensive poultry business management app — built with Kotlin & MVVM.</p>


<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=flat&logo=android&logoColor=white" />
  <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=flat&logo=kotlin&logoColor=white" />
  <img src="https://img.shields.io/badge/Architecture-MVVM-1F3864?style=flat" />
  <img src="https://img.shields.io/badge/Koin-DI-orange?style=flat" />
  <img src="https://img.shields.io/badge/Status-In%20Development-yellow?style=flat" />
</p>

United Poultry digitizes and streamlines the complete day-to-day operations of a poultry distribution business — bringing products, orders, dealers, riders, deliveries, expenses, collections, and balances into one centralized platform, in place of manual record keeping.

---

## 📱 Screenshots

<p align="center">
  <img width="200" alt="United Poultry screenshot" src="https://github.com/user-attachments/assets/e6644047-0bba-41a1-bac3-70f94af9b89a" />
   <img width="200" alt="United Poultry screenshot" src="https://github.com/user-attachments/assets/845d9744-2567-4b1b-9904-23f6acc3b184" />
   <img width="200" alt="United Poultry screenshot" src="https://github.com/user-attachments/assets/247e052a-f24f-4533-8fd3-520fd2943bb5" />
   <img width="200" alt="United Poultry screenshot" src="https://github.com/user-attachments/assets/76803efb-fb5b-4ca0-b6a9-b166041fff96" />
    <img width="200" alt="United Poultry screenshot" src="https://github.com/user-attachments/assets/5d998625-8518-44bc-94ab-be8ba2f90575" />
      <img width="200" alt="United Poultry screenshot" src="https://github.com/user-attachments/assets/89f2d9d8-4d73-4f9a-afbd-f1f2bd922045" />
       <img width="200" alt="United Poultry screenshot" src="https://github.com/user-attachments/assets/2fc09dd6-77ee-427e-9905-917949db4017" />
       <img width="400" alt="United Poultry screenshot" src="https://github.com/user-attachments/assets/abc21c60-40b0-44f1-aad2-c1b54b5c9429" />
        <img width="200" alt="United Poultry screenshot" src="https://github.com/user-attachments/assets/219e9359-6249-4396-b726-3e27ffceaa6b" />
  
  <img width="200" alt="United Poultry screenshot" src="https://github.com/user-attachments/assets/0f8c3534-69ba-4cc5-a8e0-be9d8c3d7977" />
 
  
 


 
 
 
</p>

---

## 👨‍💼 Admin Panel

Centralized control over the entire business. Administrators can manage:

- Users, dealers, and riders
- Poultry products and product categories (Shop Module)
- Orders, new sales, and business transactions
- Expenses and expense heads
- Collections and payment records
- Previous balances and outstanding amounts
- Rider and delivery-related activities
- Reports and analytics (Admin Report Module)
- App-wide settings (Admin Settings Module)
- Business and operational records / history

## 👤 Rider / User Panel

Role-based access for employees, dealers, and riders. Depending on their role, users can:

- View their dashboard and assigned business activities
- Manage assigned orders and deliveries
- Track their own expenses (Rider Expense)
- View balances and collection information
- Handle product exchanges and waste returns
- Monitor their operational progress and history

## 🐔 Product Management

Supports multiple poultry product categories: **Petti, Tray, Cotton, Shopper, Liquid** — managed through the admin workflow with organized product records.

## 💰 Expense Management

Administrators can manage different **expense heads** and maintain expense records; riders can also track their own expenses individually.

## 💵 Collections & Balances

Handles financial records tied to customers and transactions:

- Collection records
- Previous balances and outstanding amounts
- Decimal-based collection values
- Transaction-related financial information

## ♻️ Exchange & Waste Return

Dedicated workflows for handling product exchanges and waste returns — keeping stock and business records accurate when products come back from the field.

## 🚚 Dealer & Rider Operations

Dedicated workflows connect the multiple parties involved in poultry distribution — dealers interact with the business per their assigned workflow, while riders manage delivery-related activities and their own dashboard, linking orders, users, and deliveries within one system.

## 📊 Unified Business Flow

```
Products → Orders → Dealers → Riders → Deliveries → Expenses → Collections → Balances → Business Records
```

---

## 📂 Project Structure

Organized by feature/module — each package owns a self-contained screen or capability, split cleanly across admin and rider responsibilities:

```
com.example.unitedpoultry/
 ├─ AdminHome/             # Admin dashboard entry point
 ├─ AdminDashBoard/        # Admin overview
 ├─ AdminArea/             # Admin-managed areas/regions
 ├─ adminproduct/          # Admin: product management
 ├─ AdminShopModule/       # Admin: shop/catalog management
 ├─ AdminExpense/          # Admin: expense heads & records
 ├─ AdminRiderModule/      # Admin: manage riders
 ├─ AdminReportModule/     # Admin: reports & analytics
 ├─ AdminSettingModule/    # Admin: app settings
 ├─ NewSale/               # New sale / order creation
 ├─ Collection/            # Collection records
 ├─ History/               # Transaction & activity history
 ├─ Notification/          # Notifications
 ├─ Profile/                # User profile
 ├─ Authentications/       # Login & auth
 ├─ RiderDashBoard/        # Rider dashboard
 ├─ RiderArea/             # Rider-assigned areas
 ├─ rider_home/            # Rider home screen
 ├─ rider_expense/         # Rider expense tracking
 ├─ ShopModule/             # Shop/product browsing
 ├─ exchange_return/       # Product exchange handling
 ├─ waste_return/          # Waste return handling
 ├─ status_check/          # Status/health checks
 ├─ Welcome/               # Onboarding/welcome screen
 ├─ Splash/                # Splash screen
 ├─ network/               # API/network layer
 ├─ util/                  # Shared utilities
 ├─ modules/               # Shared/common modules
 ├─ BaseActivity            # Shared base Activity
 ├─ MyApplication           # Application class
 ├─ RotateTransformation    # Image transform utility
 └─ SessionManager          # Session/auth state handling
```

---

## 🛠️ Tech Stack

- **Platform:** Android
- **Language:** Kotlin
- **Architecture:** MVVM
- **UI:** XML
- **Networking:** REST APIs
- **Dependency Injection:** Koin
- **Asynchronous Programming:** Kotlin Coroutines

---

## 🚀 Getting Started

```bash
git clone https://github.com/casdevelopment/United_poultry.git
```

1. Open the project in **Android Studio**
2. Let Gradle sync finish
3. Run on an emulator or physical device (min SDK: 24 , target SDK: 36)

---

<p align="center"><i>Built and maintained by <a href="https://github.com/Basit-Ali-android-Developer">Basit Ali</a></i></p>
