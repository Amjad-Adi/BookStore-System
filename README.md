### **Please refer to the video or install the JAR file to gain a better understanding of the program's structure and its compatibility.**
### **The JAR file is located in the listed files above, meanwhile the video is located in:**

## Video Link
You can access the Video here: [Google Drive File](https://drive.google.com/file/d/1W9MYGRgI_fHh_EXF6JnLwUUXAe6z-7SF/view?usp=sharing)

## :)
<img width="1007" height="45" alt="image" src="https://github.com/user-attachments/assets/f6d6a431-da6b-4b31-aebf-d47cbfb595cc" />

### **Database Architecture**

**Entities:**
- `Customers` - Customer profiles and account information
- `Staff` - Employee records and credentials
- `Admin` / `Worker` - Specialized staff roles
- `Product` - Universal product catalog
- `Book` / `Stationery_Item` - Product specializations
- `Warehouse` - Storage facility information
- `Inventory` - Product-warehouse stock mapping
- `Transaction` - Order/rental records
- `Transaction_Items` - Order line items
- `Sale` / `Rental` - Transaction type specializations
- `Payment_Method` - Payment option definitions
- `Payment_Plan` - Installment plan configurations
- `Review` - Customer feedback system
- `Warehouse_Contact` / `Staff_Contact` - Contact information

**Relationships:**
- Customer → Transaction (1:M)
- Staff → Transaction (1:M) [Managed By]
- Transaction → Product (M:M) [Transaction_Items]
- Warehouse → Product (M:M) [Inventory]
- Customer → Review (1:M)
- Product → Review (1:M)
- Payment Method → Transaction (1:M)
- Payment Plan → Transaction (1:M)
- Specialized subtypes using table inheritance (IS-A relationships)

### **Design Patterns**
- **DAO (Data Access Object):** `AuthDAO`, `CustomerAccountDAO` for database abstraction
- **Session Management:** `Session` class for state maintenance
- **Scene Router Pattern:** `SceneRouter`, `SceneManager`, `SceneUtil` for navigation
- **Resource Helper:** `ResourceHelper` for asset loading
- **Encryption Utility:** `AESCrypto` for security operations
- **MVC Architecture:** Controller classes (`LoginController`, `RegisterController`) separate business logic from UI

### **Security Implementation**
```java
// Password Encryption Example
AESCrypto.aesEncrypt(password, secretKey, iv)
AESCrypto.sha256(password)
AESCrypto.md5(password)

// Session Security
StaffSession(staffId, role, fullName, encryptedPassword)
CustomerSession(customerId, fullName, email, encryptedPassword)
```

---

## 📈 Database Normalization

The system achieves **Third Normal Form (3NF)** through:

### **1NF (First Normal Form)**
- Eliminated repeating groups (multi-valued attributes)
- Atomic values in all columns
- Contact numbers moved to separate tables (`Staff_Contact`, `Warehouse_Contact`)
- Transaction items in dedicated junction table

### **2NF (Second Normal Form)**
- Full functional dependency on primary keys
- Composite keys in junction tables (`Transaction_Items`, `Inventory`)
- Attributes depend on entire key, not partial keys

### **3NF (Third Normal Form)**
- No transitive dependencies
- Separate tables for lookup data (`Payment_Method`, `Payment_Plan`)
- Stock quantity tied to warehouse-product relationship
- Derived values calculated dynamically (age, available storage)

**Benefits:**
- Minimal data redundancy
- Update anomaly prevention
- Data integrity enforcement
- Efficient query performance

---

## 🚀 Installation & Setup

### **Prerequisites**
```bash
- Java JDK 17 or higher
- JavaFX SDK 21 or higher
- MySQL Server 8.0 or higher
- Maven (optional, for building from source)
```

### **Database Setup**
1. Install MySQL and create a database:
```sql
mysql -u root -p
source 5_1230800_1231052.sql
```

2. Configure database connection in `DBConnection.java`:
```java
private static final String URL = "jdbc:mysql://localhost:3306/Book_Store";
private static final String USER = "your_username";
private static final String PASSWORD = "your_password";
```

### **Application Launch**
```bash
# Option 1: Run the JAR file
java -jar BookStore_System.jar

# Option 2: Build from source
mvn clean install
mvn javafx:run

# Option 3: Use the launcher
java -cp BookStore_System.jar com.example.database.Launcher
```

### **Default Credentials**
```
Admin Login:
Email: admin
Password: admin

Worker Login:
Email: worker
Password: worker

Customer Login:
Email: adam.haddad1@demo.com
Password: pass123
```

---

## 💼 Use Cases

### **For Administrators**
- Manage complete product catalog (books and stationery)
- Configure payment methods and installment plans
- Oversee staff accounts and assignments
- Monitor warehouse capacity and stock distribution
- Generate business reports and analytics
- Review customer feedback and ratings
- Handle system configurations and security

### **For Workers**
- Process customer transactions (sales and rentals)
- Manage order items and quantities
- Track payment status
- Handle returns and rental due dates
- Assist customers with product searches
- Update transaction information

### **For Customers**
- Browse books and stationery catalog
- Make purchases or rent items
- Choose payment methods and plans
- Track order history
- Submit product reviews and ratings
- Manage account budget
- View transaction details

### **For Warehouse Managers**
- Track inventory levels across locations
- Monitor storage capacity utilization
- Manage product distribution
- Update stock quantities
- Generate inventory reports

## 🎓 Academic Contribution

This project was developed as part of the **COMP333** course at Birzeit University, demonstrating:

- **Database Design Principles:** Entity-Relationship modeling, normalization theory, referential integrity
- **Software Engineering:** MVC architecture, design patterns, modular development
- **Security Best Practices:** Encryption, authentication, authorization
- **Real-World Application:** Partnership with Ramallah Public Library for requirements gathering
- **Professional Development:** Team collaboration, documentation, version control

### **Learning Outcomes**
✅ Complex database schema design and implementation  
✅ Enterprise application architecture  
✅ Multi-user system development  
✅ Security implementation in desktop applications  
✅ Business logic modeling and transaction processing  
✅ UI/UX design with JavaFX  
✅ Professional software documentation  

---

## 📝 Project Structure

```
BookStore_System/
├── src/
│   ├── main/
│   │   ├── java/com/example/database/
│   │   │   ├── HelloApplication.java      # Main application entry
│   │   │   ├── Launcher.java              # Alternative launcher
│   │   │   ├── controllers/
│   │   │   │   ├── LoginController.java   # Login UI logic
│   │   │   │   └── RegisterController.java# Registration UI logic
│   │   │   ├── dao/
│   │   │   │   ├── AuthDAO.java           # Authentication data access
│   │   │   │   └── CustomerAccountDAO.java# Customer operations
│   │   │   ├── util/
│   │   │   │   ├── DBConnection.java      # Database connection pool
│   │   │   │   ├── AESCrypto.java         # Encryption utilities
│   │   │   │   ├── Session.java           # User session management
│   │   │   │   ├── SceneManager.java      # Scene state manager
│   │   │   │   ├── SceneRouter.java       # Navigation router
│   │   │   │   ├── SceneUtil.java         # Scene utilities
│   │   │   │   └── ResourceHelper.java    # Resource loader
│   │   └── resources/
│   │       ├── fxml/                      # UI layout files
│   │       └── Images/                    # Application assets
├── database/
│   └── 5_1230800_1231052.sql             # Database schema & seed data
├── docs/
│   └── 5_1230800_1231052.pdf             # Project documentation
├── BookStore_System.jar                   # Executable application
└── README.md                              # This file
```

---

## 🔒 Security Features

- **Password Encryption:** AES-128 CBC mode with customizable keys and initialization vectors
- **Hashing Support:** SHA-128 for secure password hashing, MD5 for legacy support
- **Session Security:** Encrypted session credentials, timeout management
- **SQL Injection Prevention:** Parameterized queries using PreparedStatements
- **Role-Based Access:** Enforced privileges at application and database layers
- **Soft Deletes:** Data retention with disabled flags instead of permanent deletion
- **Audit Logging:** Timestamp tracking for all critical operations

---

## 📚 Documentation

- **Project Report:** [5_1230800_1231052.pdf](docs/5_1230800_1231052.pdf)
  - Detailed ER diagrams
  - Normalization analysis
  - Entity descriptions
  - Relationship documentation
  - Query specifications
  
- **Database Schema:** [5_1230800_1231052.sql](database/5_1230800_1231052.sql)
  - Complete DDL statements
  - Sample data insertions
  - Constraint definitions
  - Index optimizations

---

## 🤝 Client Information

**Al-Bireh Public Library (Ramallah & Al-Bireh)**
- **Address:** Municipality Building, behind Al-Ein Mosque, Al-Ein, Ramallah & Al-Bireh
- **Phone:** 02-2404549
- **Email:** librarians2palestine@gmail.com
- **Website:** [Library Website](https://librarianswithpalestine.org/featured-projects-members/publiclibraries/el-bireh-public-library-ramallah/)

---

## 👨‍💻 Development Team

**Students:**
- **Amjad Adi** - ID: 1230800
- **Waseem Yahya** - ID: 1231052

**Supervisor:**
- **Dr. Bassem Sayrafi** - Department of Computer Science, Birzeit University

**Institution:**
- Birzeit University
- Faculty of Engineering and Technology
- Department of Computer Science
- Course: COMP333 - Database Systems

---

## 📅 Project Timeline

- **Requirement Analysis:** December 2024
- **Database Design:** December 2024 - January 2025
- **Implementation:** January 2025
- **Testing & Refinement:** January 2025
- **Documentation:** January 2025
- **Submission:** January 18, 2025

---

## 🌟 Future Enhancements

- **Web Interface:** Browser-based access for remote customers
- **Mobile Application:** iOS/Android apps for on-the-go transactions
- **Barcode Scanning:** Automated product identification and checkout
- **Email Notifications:** Order confirmations, rental reminders, late fee alerts
- **Advanced Analytics:** Machine learning for demand forecasting and recommendations
- **Multi-Language Support:** Arabic and English interfaces
- **RESTful API:** Integration with third-party systems
- **Cloud Deployment:** Scalable multi-tenant architecture
- **Payment Gateway Integration:** Online payment processing
- **RFID Integration:** Automated inventory tracking

---

## 📄 License

This project is developed for academic purposes at Birzeit University.  
© 2025 Amjad Adi & Waseem Yahya. All rights reserved.

---

## 🙏 Acknowledgments

Special thanks to:
- **Dr. Bassem Sayrafi** for supervision and guidance
- **Ramallah Public Library** for partnership and requirements input
- **Birzeit University** for providing the academic framework
- **Department of Computer Science** for technical resources and support

---

## 📧 Contact

For inquiries, suggestions, or collaboration opportunities:

**Project Repository:** [Contact through Birzeit University]  
**Academic Supervisor:** Dr. Bassem Sayrafi  
**Institution:** Birzeit University - Department of Computer Science

---

<div align="center">
</div>
