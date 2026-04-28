-- ============================================
-- TTK Piano Music Center - Database Script
-- SQL Server 2019
-- ============================================

USE master;
GO

IF EXISTS (SELECT name FROM sys.databases WHERE name = 'TTKPiano')
    DROP DATABASE TTKPiano;
GO

CREATE DATABASE TTKPiano;
GO

USE TTKPiano;
GO

-- ============================================
-- TABLE: Users
-- ============================================
CREATE TABLE Users (
    userID      NVARCHAR(50)  PRIMARY KEY,
    password    NVARCHAR(255) NOT NULL,
    fullName    NVARCHAR(100) NOT NULL,
    email       NVARCHAR(100),
    phone       NVARCHAR(20),
    role        NVARCHAR(20)  NOT NULL DEFAULT 'customer',
    status      BIT           NOT NULL DEFAULT 1
);

-- ============================================
-- TABLE: Categories
-- ============================================
CREATE TABLE Categories (
    categoryID   INT IDENTITY(1,1) PRIMARY KEY,
    categoryName NVARCHAR(100) NOT NULL
);

-- ============================================
-- TABLE: Courses
-- ============================================
CREATE TABLE Courses (
    courseID       INT IDENTITY(1,1) PRIMARY KEY,
    courseName     NVARCHAR(200)  NOT NULL,
    image          NVARCHAR(500),
    description    NVARCHAR(MAX),
    tuitionFee     DECIMAL(18,2)  NOT NULL DEFAULT 0,
    startDate      DATE,
    endDate        DATE,
    categoryID     INT,
    status         NVARCHAR(20)   NOT NULL DEFAULT 'active',
    quantity       INT            NOT NULL DEFAULT 0,
    createDate     DATETIME       NOT NULL DEFAULT GETDATE(),
    lastUpdateDate DATETIME,
    lastUpdateUser NVARCHAR(50),
    CONSTRAINT FK_Courses_Categories
        FOREIGN KEY(categoryID) REFERENCES Categories(categoryID)
);

-- ============================================
-- TABLE: Orders
-- ============================================
CREATE TABLE Orders (
    orderID       INT IDENTITY(1,1) PRIMARY KEY,
    userID        NVARCHAR(50),
    guestName     NVARCHAR(100),
    guestEmail    NVARCHAR(100),
    guestPhone    NVARCHAR(20),
    orderDate     DATETIME      NOT NULL DEFAULT GETDATE(),
    paymentMethod NVARCHAR(50)  NOT NULL DEFAULT 'cash',
    paymentStatus NVARCHAR(50)  NOT NULL DEFAULT 'pending',
    totalAmount   DECIMAL(18,2) NOT NULL DEFAULT 0
);

-- ============================================
-- TABLE: OrderDetails
-- ============================================
CREATE TABLE OrderDetails (
    orderDetailID INT IDENTITY(1,1) PRIMARY KEY,
    orderID       INT           NOT NULL,
    courseID      INT           NOT NULL,
    quantity      INT           NOT NULL DEFAULT 1,
    tuitionFee    DECIMAL(18,2) NOT NULL DEFAULT 0,

    CONSTRAINT FK_OrderDetails_Orders
        FOREIGN KEY(orderID) REFERENCES Orders(orderID),

    CONSTRAINT FK_OrderDetails_Courses
        FOREIGN KEY(courseID) REFERENCES Courses(courseID)
);

-- ============================================
-- SAMPLE DATA
-- ============================================

-- Users
INSERT INTO Users (userID, password, fullName, email, phone, role) VALUES
('admin',     '123', N'Administrator',   'admin@ttkpiano.com', '0901234567', 'admin'),
('customer1', '123', N'Nguyễn Văn An',   'an@gmail.com',       '0912345678', 'customer'),
('customer2', '123', N'Trần Thị Bình',   'binh@gmail.com',     '0923456789', 'customer'),
('customer3', '123', N'Lê Hoàng Minh',   'minh@gmail.com',     '0934567890', 'customer');

-- Categories
INSERT INTO Categories (categoryName) VALUES
(N'Piano'),
(N'Guitar'),
(N'Drawing'),
(N'Violin'),
(N'Voice Training');

-- Courses
INSERT INTO Courses
(courseName, image, description, tuitionFee, startDate, endDate, categoryID, status, quantity)
VALUES
(N'Piano Cơ Bản',
'https://images.unsplash.com/photo-1520523839897-bd0b52f945a0?w=400',
N'Khóa học Piano dành cho người mới bắt đầu. Học viên sẽ được học các kỹ thuật cơ bản.',
3500000,'2026-01-01','2026-07-01',1,'active',14),

(N'Piano Nâng Cao',
'https://images.unsplash.com/photo-1611532736597-de2d4265fba3?w=400',
N'Khóa học Piano nâng cao dành cho học viên đã hoàn thành khóa cơ bản.',
5000000,'2024-04-01','2024-08-01',1,'active',10),

(N'Piano Thiếu Nhi',
'https://images.unsplash.com/photo-1515879218367-8466d910aaa4?w=400',
N'Khóa học Piano cho trẻ em từ 5-10 tuổi.',
2800000,'2024-03-15','2024-07-15',1,'active',20),

(N'Piano Jazz',
'https://images.unsplash.com/photo-1488841714725-bb4c32d1ac94?w=400',
N'Khóa học Piano Jazz cho người đam mê.',
5500000,'2024-05-01','2024-10-01',1,'active',5),

(N'Guitar Acoustic Cơ Bản',
'https://images.unsplash.com/photo-1510915361894-db8b60106cb1?w=400',
N'Khóa học Guitar Acoustic dành cho người mới bắt đầu.',
2500000,'2024-03-01','2024-06-01',2,'active',18),

(N'Guitar Electric',
'https://images.unsplash.com/photo-1564186763535-ebb21ef5277f?w=400',
N'Khóa học Guitar điện với các kỹ thuật chơi nâng cao.',
3000000,'2024-04-01','2024-08-01',2,'active',8),

(N'Guitar Fingerstyle',
'https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=400',
N'Khóa học Guitar Fingerstyle nâng cao.',
3500000,'2024-05-01','2024-09-01',2,'active',0),

(N'Vẽ Tranh Phong Cảnh',
'https://images.unsplash.com/photo-1541961017774-22349e4a1262?w=400',
N'Khóa học vẽ tranh phong cảnh.',
2000000,'2024-04-01','2024-07-01',3,'active',25),

(N'Vẽ Chân Dung',
'https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=400',
N'Khóa học vẽ chân dung nghệ thuật.',
2500000,'2024-04-15','2024-08-15',3,'active',12),

(N'Vẽ Màu Nước',
'https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=400',
N'Khóa học vẽ màu nước.',
2200000,'2024-05-01','2024-08-31',3,'active',15),

(N'Violin Cơ Bản',
'https://images.unsplash.com/photo-1465847899084-d164df4dedc6?w=400',
N'Khóa học violin cơ bản.',
4000000,'2024-05-01','2024-09-01',4,'active',6),

(N'Thanh Nhạc Cơ Bản',
'https://images.unsplash.com/photo-1516280440614-37939bbacd81?w=400',
N'Khóa học thanh nhạc cơ bản.',
3000000,'2024-06-01','2024-10-01',5,'active',17);

-- Orders
INSERT INTO Orders (guestName, guestEmail, guestPhone, totalAmount) VALUES
(N'quang','lmq@gm.com','0123456789',5500000),
(N'quang','qqq@gm.com','0123456789',6000000);

-- OrderDetails
INSERT INTO OrderDetails (orderID, courseID, quantity, tuitionFee) VALUES
(1,12,1,3000000),
(1,5,1,2500000),
(2,1,1,3500000),
(2,5,1,2500000);

GO
PRINT 'Database TTKPiano created successfully!';