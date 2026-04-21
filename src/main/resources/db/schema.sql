-- ================================================================
-- VinRECIPE — Database Schema (MySQL 8.x compatible with Aiven defaultdb)
-- Run this script once to set up the database.
-- ================================================================


-- ================================================================
-- ROOMS (create first to avoid FK cycle)
-- ================================================================
CREATE TABLE IF NOT EXISTS rooms (
    room_id    INT AUTO_INCREMENT PRIMARY KEY,
    room_name  VARCHAR(100) NOT NULL,
    leader_id  INT
);

-- ================================================================
-- USERS
-- ================================================================
CREATE TABLE IF NOT EXISTS users (
    user_id    INT AUTO_INCREMENT PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    email      VARCHAR(100) NOT NULL UNIQUE,
    role       ENUM('ADMIN', 'NORMAL_STUDENT', 'ROOM_LEADER') NOT NULL DEFAULT 'NORMAL_STUDENT',
    room_id    INT DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (room_id) REFERENCES rooms(room_id) ON DELETE SET NULL
);

-- Add FK from rooms.leader_id → users.user_id (cannot do in CREATE TABLE due to order)
ALTER TABLE rooms
    ADD CONSTRAINT fk_room_leader
    FOREIGN KEY (leader_id) REFERENCES users(user_id) ON DELETE SET NULL;

-- ================================================================
-- TAGS
-- ================================================================
CREATE TABLE IF NOT EXISTS tags (
    tag_id INT AUTO_INCREMENT PRIMARY KEY,
    name   VARCHAR(50) NOT NULL UNIQUE
);

-- ================================================================
-- RECIPES
-- ================================================================
CREATE TABLE IF NOT EXISTS recipes (
    recipe_id    INT AUTO_INCREMENT PRIMARY KEY,
    title        VARCHAR(200) NOT NULL,
    description  TEXT,
    instructions TEXT,
    prep_time    INT DEFAULT 0 COMMENT 'minutes',
    cook_time    INT DEFAULT 0 COMMENT 'minutes',
    rating       DECIMAL(2,1) DEFAULT 0.0,
    servings     INT DEFAULT 1,
    image_url    VARCHAR(500),
    author_id    INT,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (author_id) REFERENCES users(user_id) ON DELETE SET NULL
);

-- ================================================================
-- INGREDIENTS
-- ================================================================
CREATE TABLE IF NOT EXISTS ingredients (
    ingredient_id  INT AUTO_INCREMENT PRIMARY KEY,
    recipe_id      INT NOT NULL,
    name           VARCHAR(100) NOT NULL,
    quantity       DECIMAL(10,2) DEFAULT 0,
    unit           VARCHAR(30)   DEFAULT '',
    price_estimate DECIMAL(10,2) DEFAULT 0,
    FOREIGN KEY (recipe_id) REFERENCES recipes(recipe_id) ON DELETE CASCADE
);

-- ================================================================
-- RECIPE ↔ TAG (many-to-many)
-- ================================================================
CREATE TABLE IF NOT EXISTS recipe_tags (
    recipe_id INT NOT NULL,
    tag_id    INT NOT NULL,
    PRIMARY KEY (recipe_id, tag_id),
    FOREIGN KEY (recipe_id) REFERENCES recipes(recipe_id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id)    REFERENCES tags(tag_id) ON DELETE CASCADE
);

-- ================================================================
-- SHOPPING LISTS
-- ================================================================
CREATE TABLE IF NOT EXISTS shopping_lists (
    list_id    INT AUTO_INCREMENT PRIMARY KEY,
    user_id    INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS shopping_list_recipes (
    list_id   INT NOT NULL,
    recipe_id INT NOT NULL,
    PRIMARY KEY (list_id, recipe_id),
    FOREIGN KEY (list_id)   REFERENCES shopping_lists(list_id) ON DELETE CASCADE,
    FOREIGN KEY (recipe_id) REFERENCES recipes(recipe_id) ON DELETE CASCADE
);

-- ================================================================
-- SEED DATA (demo accounts & recipes)
-- ================================================================

-- Insert demo users
INSERT IGNORE INTO users (username, password, email, role) VALUES
    ('admin',    'admin123',   'admin@vinuni.edu.vn',     'ADMIN'),
    ('nhan',     'nhan123',    'nhan@vinuni.edu.vn',       'ROOM_LEADER'),
    ('trang',    'trang123',   'trang@vinuni.edu.vn',      'NORMAL_STUDENT'),
    ('nguyen',   'nguyen123',  'nguyen@vinuni.edu.vn',     'NORMAL_STUDENT'),
    ('kiet',     'kiet123',    'kiet@vinuni.edu.vn',       'NORMAL_STUDENT'),
    ('phuong',   'phuong123',  'phuong@vinuni.edu.vn',     'NORMAL_STUDENT');

-- Insert demo room
INSERT IGNORE INTO rooms (room_name, leader_id) VALUES
    ('VinUni Dorm A-101', 2);  -- nhan is leader (user_id=2)

-- Update students with room_id
UPDATE users SET room_id = 1 WHERE username IN ('nhan','trang','nguyen','kiet','phuong');

-- Insert tags
INSERT IGNORE INTO tags (name) VALUES
    ('Breakfast'), ('Lunch'), ('Dinner'), ('Vegetarian'), ('Vegan'),
    ('Quick'), ('Healthy'), ('Spicy'), ('Asian'), ('Western');

-- Insert demo recipes
INSERT IGNORE INTO recipes (title, description, instructions, prep_time, cook_time, rating, servings, author_id) VALUES
    ('Phở Bò',
     'Traditional Vietnamese beef noodle soup with rich broth.',
     '1. Char onion and ginger.\n2. Simmer beef bones for 3 hours.\n3. Season broth.\n4. Cook noodles.\n5. Assemble bowl with sliced beef and herbs.',
     20, 180, 4.8, 2, 2),

    ('Cơm Chiên Trứng',
     'Quick fried rice with egg — perfect for leftover rice.',
     '1. Heat pan with oil.\n2. Add garlic, stir fry.\n3. Add rice, stir for 3 minutes.\n4. Push rice to side, crack eggs.\n5. Mix together. Season with soy sauce.',
     5, 10, 4.2, 1, 3),

    ('Salad Gà Nướng',
     'Grilled chicken salad with fresh vegetables and lime dressing.',
     '1. Marinate chicken with salt, pepper, lemon.\n2. Grill chicken 20 min.\n3. Slice chicken.\n4. Mix greens, tomato, cucumber.\n5. Toss with lime dressing.',
     15, 20, 4.5, 2, 4),

    ('Mì Xào Bò',
     'Stir-fried noodles with beef and vegetables.',
     '1. Marinate beef slices.\n2. Boil noodles until al dente.\n3. Stir-fry beef and vegetables.\n4. Add noodles and sauce.\n5. Toss to combine.',
     10, 15, 4.3, 2, 2),

    ('Bánh Mì Trứng',
     'Vietnamese egg sandwich on crispy baguette.',
     '1. Toast baguette.\n2. Fry eggs sunny side up.\n3. Spread butter and pate.\n4. Add eggs, cucumber, cilantro.\n5. Season with soy sauce.',
     5, 5, 4.6, 1, 5);

-- Insert ingredients for Phở Bò (recipe_id=1)
INSERT IGNORE INTO ingredients (recipe_id, name, quantity, unit, price_estimate) VALUES
    (1, 'beef bones',  500, 'g',   25000),
    (1, 'rice noodles',200, 'g',   8000),
    (1, 'beef slices', 150, 'g',   30000),
    (1, 'onion',       1,   'pcs', 3000),
    (1, 'ginger',      30,  'g',   2000),
    (1, 'star anise',  3,   'pcs', 1000);

-- Insert ingredients for Cơm Chiên Trứng (recipe_id=2)
INSERT IGNORE INTO ingredients (recipe_id, name, quantity, unit, price_estimate) VALUES
    (2, 'cooked rice', 300, 'g',   5000),
    (2, 'egg',         2,   'pcs', 6000),
    (2, 'garlic',      3,   'cloves', 1000),
    (2, 'soy sauce',   15,  'ml',  1000),
    (2, 'oil',         15,  'ml',  2000);

-- Insert ingredients for Salad Gà Nướng (recipe_id=3)
INSERT IGNORE INTO ingredients (recipe_id, name, quantity, unit, price_estimate) VALUES
    (3, 'chicken breast', 200, 'g',  25000),
    (3, 'lettuce',        100, 'g',  5000),
    (3, 'tomato',         1,   'pcs', 5000),
    (3, 'cucumber',       1,   'pcs', 3000),
    (3, 'lime',           1,   'pcs', 2000),
    (3, 'garlic',         2,   'cloves', 500);

-- Insert ingredients for Mì Xào Bò (recipe_id=4)
INSERT IGNORE INTO ingredients (recipe_id, name, quantity, unit, price_estimate) VALUES
    (4, 'egg noodles', 200, 'g',  10000),
    (4, 'beef slices', 150, 'g',  30000),
    (4, 'bok choy',    100, 'g',  5000),
    (4, 'soy sauce',   20,  'ml', 1000),
    (4, 'garlic',      3,   'cloves', 500);

-- Insert ingredients for Bánh Mì Trứng (recipe_id=5)
INSERT IGNORE INTO ingredients (recipe_id, name, quantity, unit, price_estimate) VALUES
    (5, 'baguette',   1,   'pcs', 8000),
    (5, 'egg',        2,   'pcs', 6000),
    (5, 'butter',     10,  'g',   3000),
    (5, 'cucumber',   0.5, 'pcs', 1500),
    (5, 'soy sauce',  10,  'ml',  500);

-- Tag recipes
INSERT IGNORE INTO recipe_tags (recipe_id, tag_id) VALUES
    (1, 3), (1, 9),   -- Phở: Dinner, Asian
    (2, 2), (2, 6), (2, 9),    -- Cơm chiên: Lunch, Quick, Asian
    (3, 2), (3, 7),   -- Salad: Lunch, Healthy
    (4, 3), (4, 9),   -- Mì Xào: Dinner, Asian
    (5, 1), (5, 6);   -- Bánh Mì: Breakfast, Quick
