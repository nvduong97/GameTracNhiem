
CREATE DATABASE Game;

USE Game;
Go

CREATE TABLE [User](
	userID INT IDENTITY(1,1) PRIMARY KEY,
	account NVARCHAR(40),
	password NVARCHAR(40),
	status int,
	point float,
);

INSERT INTO [User] 
VALUES	('duongnv', '123456',0,2),
		('quyen', '123456',0,1),
		('lany', '123456',0,1);
GO
UPDATE [User] SET status = 0 WHERE status = 1
SELECT * FROM [User] ORDER BY point DESC

CREATE TABLE Question(
	ID INT IDENTITY(1,1) PRIMARY KEY,
	question NVARCHAR(300) NOT NULL,
	answerA NVARCHAR(100),
	answerB NVARCHAR(100),
	answerC NVARCHAR(100),
	answerD NVARCHAR(100),
	correctAnswer int,
);

INSERT INTO Question 
VALUES	(N'An Dương Vương đặt quốc hiệu nước ta là gì?', N'Âu Lạc', N'Vạn Xuân', N'Đại Cồ Việt', N'Đại Việt', 1),
	(N'Đại dương nào sâu nhất?', N'Bắc Băng Dương', N'Ấn Độ Dương', N'Đại Tây Dương', N'Thái Bình Dương', 4),
	(N'Đàn bầu còn có tên gọi nào khác', N'Đàn ghita', N'Đàn độc huyền', N'Đàn bí', N'Đàn tranh', 2),
	(N'Con sông nào chảy qua xích đạo 2 lần?', N'Sông Ấn', N'Sông Hằng', N'Sông volga', N'Sông Công-go', 4),
	(N'Xưởng phim hoạt hình lớn nhất thế giới?', N'Pixar', N'Walt Disney', N'DreamWorks Animation', N'Studio Ghibli', 2),
	(N'Hành tinh lớn nhất thái dương hệ?', N'Mộc tinh', N'Hỏa tinh', N'Thiên Vương tinh', N'Hải Vương tinh', 1),
	(N'Con sông dài nhất thế giới?', N'Sông mê kong', N'Sông Nil', N'Sông Amazon', N'Sông volga', 2),
	(N'Dòng sông lớn nhất thế giới?', N'Sông mê kong', N'Sông Nil', N'Sông Amazon', N'Sông volga', 3),
	(N'Tên khách của chùa Một Cột?', N'Chùa Diên Khánh', N'chùa Diên Hựu', N'Chùa Diên Phúc', N'Chùa Diên Ngọ', 2),
	(N'Hòn đảo lớn nhất Việt Nam?', N'Cô tô', N'Cát Bà', N'Bạch Long Vỹ', N'Phú Quốc', 4),
	(N'Chiều dài của Vạn Lý Trường Thành?', N'6677 km', N'6887 km', N'6688 km', N'6788 km', 4),
	(N'Trong các loại vật liệu, vật nào cứng nhất?', N'Kim cương', N'Titan', N'Thép', N'Crom', 1),
	(N'Đất nước nào phát minh ra giấy?', N'Mỹ', N'Trung Quốc', N'Ấn Độ', N'Nhật Bản', 2);
GO

SELECT TOP 5 * FROM Question ORDER BY CHECKSUM(NEWID())