-- Tạo bảng categories
DROP TABLE IF EXISTS `categories`;
CREATE TABLE categories (
                            id CHAR(36) PRIMARY KEY,
                            name VARCHAR(255) NOT NULL UNIQUE,
                            description TEXT NULL,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Tạo bảng users
DROP TABLE IF EXISTS `users`;
CREATE TABLE users (
                       id CHAR(36) PRIMARY KEY,
                       username VARCHAR(255) NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       first_name VARCHAR(255) NOT NULL,
                       last_name VARCHAR(255) NOT NULL,
                       role ENUM('ADMIN', 'USER') DEFAULT 'USER',
                       gender BOOLEAN NOT NULL,
                       phone_number VARCHAR(255) NOT NULL,
                       date_of_birth TIMESTAMP NOT NULL,
                       mail VARCHAR(255) NOT NULL,
                       avatar VARCHAR(255) NULL,
                       enable BOOLEAN NOT NULL,
                       address VARCHAR(255),
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tạo bảng products
DROP TABLE IF EXISTS `products`;
CREATE TABLE products (
                          id CHAR(36) PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          description TEXT NOT NULL,
                          price DECIMAL(10, 2) NOT NULL,
                          stock_quantity INT NOT NULL,
                          total_reviews INT NOT NULL DEFAULT 0,
                          sold_quantity INT NOT NULL DEFAULT 0,
                          category_id CHAR(36) NOT NULL,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Tạo bảng coupons
DROP TABLE IF EXISTS `coupons`;
CREATE TABLE coupons (
                         id CHAR(36) PRIMARY KEY,
                         code VARCHAR(50) NOT NULL UNIQUE,
                         discount_amount DECIMAL(10, 2) NOT NULL,
                         expiry_date DATE NOT NULL,
                         is_active BOOLEAN DEFAULT TRUE
);

-- Tạo bảng orders
DROP TABLE IF EXISTS `orders`;
CREATE TABLE orders (
                        id CHAR(36) PRIMARY KEY,
                        user_id CHAR(36) NOT NULL,
                        total_price DECIMAL(10, 2) NOT NULL,
                        status VARCHAR(50) NOT NULL,
                        shipping_address TEXT NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Tạo bảng order_details
DROP TABLE IF EXISTS `order_details`;
CREATE TABLE order_details (
                               id CHAR(36) PRIMARY KEY,
                               order_id CHAR(36) NOT NULL,
                               product_id CHAR(36) NOT NULL,
                               quantity INT NOT NULL,
                               price DECIMAL(10, 2) NOT NULL, -- Giá tại thời điểm đặt hàng
                               FOREIGN KEY (order_id) REFERENCES orders(id),
                               FOREIGN KEY (product_id) REFERENCES products(id)
);

-- Tạo bảng reviews
DROP TABLE IF EXISTS `reviews`;
CREATE TABLE reviews (
                         id CHAR(36) PRIMARY KEY,
                         product_id CHAR(36) NOT NULL,
                         user_id CHAR(36) NOT NULL,
                         rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
                         comment TEXT NULL,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         FOREIGN KEY (product_id) REFERENCES products(id),
                         FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Tạo bảng carts
DROP TABLE IF EXISTS `carts`;
CREATE TABLE carts (
                       id CHAR(36) PRIMARY KEY,
                       user_id CHAR(36) NOT NULL,
                       product_id CHAR(36) NOT NULL,
                       quantity INT NOT NULL DEFAULT 1,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       FOREIGN KEY (user_id) REFERENCES users(id),
                       FOREIGN KEY (product_id) REFERENCES products(id)
);

-- Tạo bảng favorites
DROP TABLE IF EXISTS `favorites`;
CREATE TABLE favorites (
                           user_id CHAR(36) NOT NULL,
                           product_id CHAR(36) NOT NULL,
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           FOREIGN KEY (user_id) REFERENCES users(id),
                           FOREIGN KEY (product_id) REFERENCES products(id)
);

-- Tạo bảng product_media
DROP TABLE IF EXISTS `product_media`;
CREATE TABLE product_media (
                               id CHAR(36) PRIMARY KEY,
                               base_name VARCHAR(255) NOT NULL,
                               public_url VARCHAR(2083) NOT NULL,
                               product_id CHAR(36),
                               FOREIGN KEY (product_id) REFERENCES products(id)
);

-- Tạo bảng product_coupons
DROP TABLE IF EXISTS `product_coupons`;
CREATE TABLE product_coupons (
                                 product_id CHAR(36),
                                 coupon_id CHAR(36),
                                 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                 PRIMARY KEY (product_id, coupon_id),
                                 FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE ON UPDATE CASCADE,
                                 FOREIGN KEY (coupon_id) REFERENCES coupons(id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Tạo bảng payments
DROP TABLE IF EXISTS `payments`;
CREATE TABLE payments (
                          id CHAR(36) PRIMARY KEY,
                          order_id CHAR(36) NOT NULL,
                          payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          amount DECIMAL(10, 2) NOT NULL,
                          payment_method ENUM('Credit Card', 'PayPal', 'Cash') DEFAULT 'Credit Card',
                          FOREIGN KEY (order_id) REFERENCES orders(id)
);

-- Tạo bảng news
DROP TABLE IF EXISTS `news`;
CREATE TABLE news (
                      id CHAR(36) PRIMARY KEY,
                      title VARCHAR(255) NOT NULL,
                      content TEXT NOT NULL,
                      source VARCHAR(255) NULL,
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Tạo bảng news_media
DROP TABLE IF EXISTS `news_media`;
CREATE TABLE news_media (
                            id CHAR(36) PRIMARY KEY,
                            base_name VARCHAR(255) NOT NULL,
                            public_url VARCHAR(2083) NOT NULL,
                            news_id CHAR(36),
                            FOREIGN KEY (news_id) REFERENCES news(id)
);

-- Tạo bảng OTPs
DROP TABLE IF EXISTS `OTPs`;
CREATE TABLE OTPs (
                      id VARCHAR(36) NOT NULL PRIMARY KEY,
                      otp VARCHAR(255) UNIQUE,
                      mail VARCHAR(255) NOT NULL,
                      expiration_time TIMESTAMP,
                      create_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      used BOOLEAN DEFAULT FALSE,
                      created_by VARCHAR(36),
                      FOREIGN KEY (created_by) REFERENCES users(id)
);
