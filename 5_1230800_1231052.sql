DROP DATABASE IF EXISTS Book_Store;
CREATE DATABASE Book_Store;
USE Book_Store;

-- =========================
-- Customers
-- =========================

CREATE TABLE Customers (
  Customer_Id INT AUTO_INCREMENT,
  Customer_First_Name VARCHAR(128) NOT NULL,
  Customer_Second_Name VARCHAR(128) NOT NULL,
  Customer_Email VARCHAR(128) NOT NULL UNIQUE,
  Customer_Password VARCHAR(255) NOT NULL,
  Customer_Profession ENUM(
    'Engineer','Developer','Teacher','Student','Doctor','Nurse','Pharmacist','Accountant',
    'Business Owner','Designer','Graphic Designer','Journalist','Architect','Researcher','Other'
  ) NOT NULL DEFAULT 'Other',
  Customer_Birth_Date DATE,
  Customer_Registration_Date DATE NOT NULL,
  Customer_Activation_Date DATE,
  Customer_Expiration_Date DATE,
  Budget_In_Dollars DECIMAL(10,1) NOT NULL DEFAULT 0,
  Disabled_Customer BOOLEAN NOT NULL DEFAULT FALSE,
  PRIMARY KEY (Customer_Id)
);

INSERT INTO Customers
(Customer_First_Name, Customer_Second_Name, Customer_Email, Customer_Password,
 Customer_Profession, Customer_Birth_Date, Customer_Registration_Date,
 Customer_Activation_Date, Customer_Expiration_Date, Budget_In_Dollars, Disabled_Customer)
VALUES

('Adam','Haddad','adam.haddad1@demo.com','pass123','Engineer','1998-03-12','2026-01-01','2026-01-02','2027-01-02',120.0,0),
('Lina','Khalil','lina.khalil2@demo.com','pass123','Teacher','2000-07-05','2026-01-01','2026-01-03','2027-01-03',80.0,0),
('Omar','Nasser','omar.nasser3@demo.com','pass123','Student','2003-11-20','2026-01-02','2026-01-04','2027-01-04',35.0,0),
('Sara','Yousef','sara.yousef4@demo.com','pass123','Doctor','1996-02-28','2026-01-02','2026-01-05','2027-01-05',300.0,0),
('Yazan','Barghouti','yazan.barghouti5@demo.com','pass123','Developer','1999-09-14','2026-01-03','2026-01-06','2027-01-06',150.0,0),
('Maya','AbuZaid','maya.abuzaid6@demo.com','pass123','Designer','2001-01-19','2026-01-03',NULL,NULL,60.0,0),
('Hadi','Salem','hadi.salem7@demo.com','pass123','Accountant','1997-06-10','2026-01-04','2026-01-07','2027-01-07',210.0,0),
('Rana','Odeh','rana.odeh8@demo.com','pass123','Nurse','1995-12-02','2026-01-04','2026-01-08','2027-01-08',95.0,0),
('Kareem','Awad','kareem.awad9@demo.com','pass123','Journalist','1998-08-23','2026-01-05',NULL,NULL,40.0,0),
('Dina','Hassan','dina.hassan10@demo.com','pass123','Other','2002-05-30','2026-01-05','2026-01-09','2027-01-09',25.0,0),

('Fadi','Sabbah','fadi.sabbah11@demo.com','pass123','Engineer','1994-04-17','2026-01-06','2026-01-10','2027-01-10',180.0,0),
('Noor','Zayed','noor.zayed12@demo.com','pass123','Teacher','1999-10-01','2026-01-06',NULL,NULL,70.0,0),
('Tariq','Mahmoud','tariq.mahmoud13@demo.com','pass123','Student','2004-02-11','2026-01-07','2026-01-11','2027-01-11',15.0,0),
('Hala','Farah','hala.farah14@demo.com','pass123','Doctor','1993-07-27','2026-01-07','2026-01-12','2027-01-12',500.0,0),
('Ali','Saeed','ali.saeed15@demo.com','pass123','Developer','2000-03-03','2026-01-08','2026-01-13','2027-01-13',130.0,0),
('Maram','Rashid','maram.rashid16@demo.com','pass123','Designer','1998-11-09','2026-01-08',NULL,NULL,55.0,0),
('Ibrahim','Qasim','ibrahim.qasim17@demo.com','pass123','Accountant','1996-01-25','2026-01-09','2026-01-14','2027-01-14',240.0,0),
('Layan','Najjar','layan.najjar18@demo.com','pass123','Nurse','1997-09-18','2026-01-09','2026-01-15','2027-01-15',110.0,0),
('Ziad','Kanaan','ziad.kanaan19@demo.com','pass123','Journalist','1995-06-06','2026-01-10',NULL,NULL,45.0,0),
('Reem','Hamdan','reem.hamdan20@demo.com','pass123','Other','2001-12-15','2026-01-10','2026-01-16','2027-01-16',20.0,0),

('Sami','Shawa','sami.shawa21@demo.com','pass123','Engineer','1992-02-08','2026-01-11','2026-01-17','2027-01-17',260.0,0),
('Jana','Mansour','jana.mansour22@demo.com','pass123','Teacher','1998-07-21','2026-01-11',NULL,NULL,65.0,0),
('Hussein','Darwish','hussein.darwish23@demo.com','pass123','Student','2005-03-19','2026-01-12','2026-01-18','2027-01-18',10.0,0),
('Nouran','Sultan','nouran.sultan24@demo.com','pass123','Doctor','1991-09-30','2026-01-12','2026-01-19','2027-01-19',420.0,0),
('Ameer','Saleh','ameer.saleh25@demo.com','pass123','Developer','1997-05-02','2026-01-13','2026-01-20','2027-01-20',160.0,0),
('Aya','Taha','aya.taha26@demo.com','pass123','Designer','2000-08-12','2026-01-13',NULL,NULL,50.0,0),
('Nabil','Khatib','nabil.khatib27@demo.com','pass123','Accountant','1995-10-26','2026-01-14','2026-01-21','2027-01-21',190.0,0),
('Ruba','Aziz','ruba.aziz28@demo.com','pass123','Nurse','1999-01-07','2026-01-14','2026-01-22','2027-01-22',105.0,0),
('Moataz','Sayed','moataz.sayed29@demo.com','pass123','Journalist','1996-04-04','2026-01-15',NULL,NULL,48.0,0),
('Leen','Karmi','leen.karmi30@demo.com','pass123','Other','2002-09-09','2026-01-15','2026-01-23','2027-01-23',30.0,0),

('Yousef','Hajjar','yousef.hajjar31@demo.com','pass123','Engineer','1993-11-11','2026-01-16','2026-01-24','2027-01-24',220.0,0),
('Mariam','Ghanem','mariam.ghanem32@demo.com','pass123','Teacher','2001-06-13','2026-01-16',NULL,NULL,75.0,0),
('Basel','Houri','basel.houri33@demo.com','pass123','Student','2004-12-28','2026-01-17','2026-01-25','2027-01-25',18.0,0),
('Salwa','Ayyad','salwa.ayyad34@demo.com','pass123','Doctor','1989-03-22','2026-01-17','2026-01-26','2027-01-26',600.0,0),
('Hamza','Nimr','hamza.nimr35@demo.com','pass123','Developer','1998-02-16','2026-01-18','2026-01-27','2027-01-27',140.0,0),
('Dana','Rizk','dana.rizk36@demo.com','pass123','Designer','1997-07-08','2026-01-18',NULL,NULL,58.0,0),
('Khaled','Asaad','khaled.asaad37@demo.com','pass123','Accountant','1994-09-05','2026-01-19','2026-01-28','2027-01-28',205.0,0),
('Haneen','Jaber','haneen.jaber38@demo.com','pass123','Nurse','1996-11-17','2026-01-19','2026-01-29','2027-01-29',115.0,0),
('Rami','Masri','rami.masri39@demo.com','pass123','Journalist','1992-06-25','2026-01-20',NULL,NULL,42.0,0),
('Alaa','Zahran','alaa.zahran40@demo.com','pass123','Other','2003-02-02','2026-01-20','2026-01-30','2027-01-30',22.0,0),

('Sameer','Nabulsi','sameer.nabulsi41@demo.com','pass123','Engineer','1991-05-14','2026-01-21','2026-01-31','2027-01-31',275.0,0),
('Rima','Abbas','rima.abbas42@demo.com','pass123','Teacher','1999-04-01','2026-01-21',NULL,NULL,68.0,0),
('Othman','Yahya','othman.yahya43@demo.com','pass123','Student','2005-08-07','2026-01-22','2026-02-01','2027-02-01',12.0,0),
('Hiba','Nouri','hiba.nouri44@demo.com','pass123','Doctor','1990-10-19','2026-01-22','2026-02-02','2027-02-02',450.0,0),
('Laith','Sami','laith.sami45@demo.com','pass123','Developer','1997-12-12','2026-01-23','2026-02-03','2027-02-03',155.0,0),
('Yara','Mousa','yara.mousa46@demo.com','pass123','Designer','2001-03-29','2026-01-23',NULL,NULL,52.0,0),
('Ayman','Rashed','ayman.rashed47@demo.com','pass123','Accountant','1996-08-18','2026-01-24','2026-02-04','2027-02-04',230.0,0),
('Lama','Issa','lama.issa48@demo.com','pass123','Nurse','1998-06-06','2026-01-24','2026-02-05','2027-02-05',108.0,0),
('Zain','Othman','zain.othman49@demo.com','pass123','Journalist','1993-01-31','2026-01-25',NULL,NULL,46.0,0),
('Mai','Husni','mai.husni50@demo.com','pass123','Other','2002-10-10','2026-01-25','2026-02-06','2027-02-06',28.0,0);


-- =========================
-- Staff + Admin/Worker
-- =========================
CREATE TABLE Staff (
  Staff_ID INT AUTO_INCREMENT,
  First_Name VARCHAR(128) NOT NULL,
  Last_Name VARCHAR(128) NOT NULL,
  Staff_Email VARCHAR(128) UNIQUE NOT NULL,
  Staff_Password VARCHAR(128) NOT NULL,
  Staff_Salary DECIMAL(10,1) NOT NULL,
  Staff_Birth_Date DATE NOT NULL,
  Staff_Hire_Date DATE NOT NULL,
  Staff_Status ENUM('Active','Inactive') NOT NULL DEFAULT 'Active',
  Disabled_Staff BOOLEAN NOT NULL DEFAULT FALSE,
  PRIMARY KEY(Staff_ID)
);

CREATE TABLE Staff_Contact (
  Contact_ID INT AUTO_INCREMENT PRIMARY KEY,
  Staff_ID INT NOT NULL,
  Contact_Number VARCHAR(128) NOT NULL,
  FOREIGN KEY (Staff_ID) REFERENCES Staff(Staff_ID)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

INSERT INTO Staff
(First_Name, Last_Name, Staff_Email, Staff_Password, Staff_Salary,
 Staff_Birth_Date, Staff_Hire_Date)
VALUES
('Emma','Stone','emma1@store.com','pass',2200,'1992-02-02','2021-01-01'),
('Noah','Green','noah1@store.com','pass',1800,'1997-06-10','2022-05-01'),
('Olivia','Hall','olivia1@store.com','pass',2500,'1988-12-25','2020-03-15');


CREATE TABLE Admin (
  Staff_ID INT PRIMARY KEY,
  Admin_Level VARCHAR(128) NOT NULL,
  FOREIGN KEY (Staff_ID) REFERENCES Staff(Staff_ID)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

CREATE TABLE Worker (
  Staff_ID INT PRIMARY KEY,
  Job_Title VARCHAR(128) NOT NULL,
  Shift_Time VARCHAR(128) NOT NULL,
  FOREIGN KEY (Staff_ID) REFERENCES Staff(Staff_ID)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

select * from admin ; 
-- =========================
-- Warehouse
-- =========================
CREATE TABLE Warehouse (
  Warehouse_ID INT AUTO_INCREMENT PRIMARY KEY,
  Warehouse_Name VARCHAR(100) NOT NULL,
  Warehouse_Address VARCHAR(200) NOT NULL,
  WarehouseDate_Of_Establishment DATE NOT NULL,
  Warehouse_Max_Storage INT NOT NULL,
  Warehouse_Current_Storage INT NOT NULL,
  Disabled_Warehouse BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE Warehouse_Contact (
  Contact_ID INT AUTO_INCREMENT PRIMARY KEY,
  Warehouse_ID INT NOT NULL,
  Contact_Number VARCHAR(128) NOT NULL,
  FOREIGN KEY (Warehouse_ID) REFERENCES Warehouse(Warehouse_ID)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

-- =========================
-- Product + Book + Stationery
-- =========================
CREATE TABLE Product (
  Product_ID INT AUTO_INCREMENT PRIMARY KEY,
  Product_Name VARCHAR(128) NOT NULL,
  Product_Description VARCHAR(1024),
  Product_Category ENUM('Book','Stationery') NOT NULL,
  Product_Company VARCHAR(128) NOT NULL,
  Product_Price DECIMAL(10,2) NOT NULL DEFAULT 0,
  Product_Stock INT NOT NULL DEFAULT 0,
  Disabled_Product BOOLEAN NOT NULL DEFAULT FALSE
);

INSERT INTO Product
(Product_Name, Product_Description, Product_Category,
 Product_Company, Product_Price, Product_Stock)
VALUES
('Clean Code','Programming Book','Book','Pearson',45.99,50),
('Effective Java','Java Book','Book','Addison-Wesley',55.99,40),
('Notebook','A4 Notebook','Stationery','Staples',3.50,200),
('Pen','Blue Ink Pen','Stationery','Bic',1.20,500),
('Design Patterns','Software Book','Book','OReilly',60.00,30);


CREATE TABLE Book (
  Product_ID INT PRIMARY KEY,
  Book_ISBN VARCHAR(128) UNIQUE NOT NULL,
  Book_Author_First_Name VARCHAR(128),
  Book_Author_Last_Name VARCHAR(128),
  Book_Publication_Year YEAR,
  Book_Genre VARCHAR(128),
  Book_Language VARCHAR(128),
  Book_Edition VARCHAR(128),
  Book_Number_Of_Pages INT,
  FOREIGN KEY (Product_ID) REFERENCES Product(Product_ID)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

CREATE TABLE Stationery_Item (
  Product_ID INT PRIMARY KEY,
  Stationery_Color VARCHAR(128),
  Stationery_Material VARCHAR(128),
  Stationery_Dimensions VARCHAR(128),
  Stationery_Production_Year YEAR,
  FOREIGN KEY (Product_ID) REFERENCES Product(Product_ID)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);


-- =========================
-- Payment Method
-- =========================
CREATE TABLE Payment_Method (
  Payment_Method_Id INT AUTO_INCREMENT PRIMARY KEY,
  Payment_Method_Name VARCHAR(128) NOT NULL UNIQUE,
  Method_Description VARCHAR(255),
  Disabled_Payment_Method BOOLEAN NOT NULL DEFAULT FALSE
);

-- =========================
-- Payment Plan  
-- =========================
CREATE TABLE Payment_Plan(
  Payment_Plan_ID INT AUTO_INCREMENT,
  Payment_Plan_Period_In_Months INT NOT NULL,
  Number_Of_Months_Before_The_Legal_Trial INT,
  Payment_Plan_Description VARCHAR(2048),
  Disabled_Plan BOOLEAN NOT NULL DEFAULT FALSE,
  PRIMARY KEY(Payment_Plan_ID)
);

INSERT INTO Payment_Plan (Payment_Plan_Period_In_Months, Payment_Plan_Description)
VALUES (1, 'Pay in Full - No installments');

CREATE TABLE `Transaction` (
  Transaction_ID INT AUTO_INCREMENT,
  Transaction_Date DATE NOT NULL,
  Transaction_Channel ENUM('Online','In store') NOT NULL DEFAULT 'In store',
  Transaction_Information VARCHAR(2048),
  Cost DECIMAL(10,2) NOT NULL,
  Amount_Paid DECIMAL(10,2) NOT NULL,
  Payment_Status ENUM('Pending', 'Paid', 'Failed', 'Refunded') NOT NULL,
  Disabled_Transaction BOOLEAN NOT NULL DEFAULT FALSE,

  Customer_ID INT NULL ,
  Payment_Method_Id INT NULL,
  Payment_Plan_Id INT NOT NULL DEFAULT 1,
  
  Order_Type ENUM('Sale', 'Rental') NOT NULL DEFAULT 'Sale',

  PRIMARY KEY(Transaction_ID),

  FOREIGN KEY (Customer_ID) REFERENCES Customers(Customer_Id)
	ON DELETE SET NULL
    ON UPDATE CASCADE,

  FOREIGN KEY (Payment_Method_Id) REFERENCES Payment_Method(Payment_Method_Id)
    ON DELETE SET NULL
    ON UPDATE CASCADE,

  FOREIGN KEY (Payment_Plan_Id) REFERENCES Payment_Plan(Payment_Plan_ID)
    ON DELETE RESTRICT
    ON UPDATE CASCADE
);


CREATE TABLE Transaction_Items (
  Transaction_ID INT NOT NULL,
  Product_ID INT NOT NULL,
  Quantity INT NOT NULL,
  Price_At_Time DECIMAL(10,2) NOT NULL,
  Is_Disabled BOOLEAN NOT NULL DEFAULT FALSE,
  PRIMARY KEY (Transaction_ID, Product_ID),

  FOREIGN KEY (Transaction_ID) REFERENCES `Transaction`(Transaction_ID)
    ON DELETE CASCADE
    ON UPDATE CASCADE,

  FOREIGN KEY (Product_ID) REFERENCES Product(Product_ID)
    ON DELETE RESTRICT
    ON UPDATE CASCADE
);

-- =========================
-- Review
-- =========================
CREATE TABLE Review (
  Review_Id INT AUTO_INCREMENT PRIMARY KEY,
  Customer_ID INT NOT NULL,
  Product_ID INT NOT NULL,
  Rating INT NOT NULL CHECK (Rating BETWEEN 1 AND 5),
  Comment VARCHAR(1024),
  Review_Date DATE,
  FOREIGN KEY (Customer_ID) REFERENCES Customers(Customer_Id) ON DELETE CASCADE,
  FOREIGN KEY (Product_ID) REFERENCES Product(Product_ID) ON DELETE CASCADE
);

INSERT INTO Review
(Customer_ID, Product_ID, Rating, Comment, Review_Date)
VALUES
(1,1,5,'Excellent book','2024-01-20'),
(2,3,4,'Good quality','2024-01-21'),
(3,5,5,'Must read','2024-01-22');


-- =========================
-- Rental + Sale
-- =========================
CREATE TABLE Rental (
  Transaction_ID INT PRIMARY KEY,
  Rental_Start_Date DATE NOT NULL,
  Rental_End_Date DATE NOT NULL,
  Return_Date DATE,
  Rental_Status ENUM('Active', 'Returned', 'Late', 'Cancelled') NOT NULL,
  Late_Fee_Per_Day DECIMAL(8,2) DEFAULT 1,
  FOREIGN KEY (Transaction_ID) REFERENCES `Transaction`(Transaction_ID)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

CREATE TABLE Sale (
  Transaction_ID INT PRIMARY KEY,
  Discount_Percentage DECIMAL(5,2) DEFAULT 0,
  FOREIGN KEY (Transaction_ID) REFERENCES `Transaction`(Transaction_ID)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

-- =========================
-- Seed data (optional)
-- =========================
INSERT INTO Payment_Method (Payment_Method_Name, Method_Description)
VALUES
('Cash','Pay in cash'),
('Card','Debit/Credit card'),
('Installments','Monthly installments');

INSERT INTO Staff
(First_Name, Last_Name, Staff_Email, Staff_Password, Staff_Salary, Staff_Birth_Date, Staff_Hire_Date, Staff_Status)
VALUES
('Admin','One','admin','admin',2000.0,'1990-01-01','2020-01-01','Active'),
('Worker','Two','worker','worker',1200.0,'1999-05-20','2022-06-01','Active');

 INSERT INTO Admin (Staff_ID, Admin_Level) VALUES (1,'Super');
 INSERT INTO Worker (Staff_ID, Job_Title, Shift_Time) VALUES (2,'Cashier','Morning');

 INSERT INTO Warehouse
 (Warehouse_Name, Warehouse_Address, WarehouseDate_Of_Establishment, Warehouse_Max_Storage, Warehouse_Current_Storage)
 VALUES
 ('25% Full',  'Test Address 1', '2020-01-01', 4000, 1000),
 ('50% Full',  'Test Address 2', '2020-01-01', 4000, 2000),
 ('100% Full', 'Test Address 3', '2020-01-01', 4000, 4000);
 
 CREATE TABLE Inventory (
    Warehouse_ID INT NOT NULL,
    Product_ID INT NOT NULL,
    Quantity_In_Warehouse INT NOT NULL DEFAULT 0,
    Is_Disabled BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (Warehouse_ID, Product_ID),

    FOREIGN KEY (Warehouse_ID) REFERENCES Warehouse(Warehouse_ID)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    FOREIGN KEY (Product_ID) REFERENCES Product(Product_ID)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);
