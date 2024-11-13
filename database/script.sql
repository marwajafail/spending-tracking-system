-- Database: Spending

DROP DATABASE IF EXISTS "Spending";

CREATE DATABASE "Spending"
    WITH
   -- OWNER = imira
    ENCODING = 'UTF8'
    LC_COLLATE = 'C'
    LC_CTYPE = 'C'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1
    IS_TEMPLATE = False;

-- Switch to the Spending database
\c Spending

-- Table creation for roles
CREATE TABLE public.sp_role (
    role_id SERIAL PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    name VARCHAR(50) NOT NULL
);

-- Table creation for profiles
CREATE TABLE public.sp_profile (
    profile_id SERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    profile_pic VARCHAR(255)
);

-- Table creation for users
CREATE TABLE public.sp_user (
    uid SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    status BOOLEAN NOT NULL,
    profile_id INT REFERENCES public.sp_profile(profile_id),
    role_id INT REFERENCES public.sp_role(role_id),
    is_enabled BOOLEAN NOT NULL,
    reset_token VARCHAR(255),
    token_expiration_time TIMESTAMP,
    verification_code VARCHAR(255)
);

-- Table creation for categories
CREATE TABLE public.sp_category (
    category_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    uid INT REFERENCES public.sp_user(uid)
);

-- Insert roles
INSERT INTO sp_role (role_id, description, name) VALUES
(1, 'Standard user with limited privileges', 'User'),
(2, 'Administrator with full privileges', 'Admin');

-- Insert profile
INSERT INTO public.sp_profile (
    profile_id, first_name, last_name, profile_pic
) VALUES (
    1, 'Fatema', 'Marhoon', 'upload-dir/BIBF.jpeg'
);

-- Insert user
INSERT INTO public.sp_user (
    uid, email, password, status, profile_id, role_id, is_enabled, reset_token, token_expiration_time, verification_code
) VALUES (
    1, 'ifatima.marhoon@gmail.com', '$2a$10$pqS8QfbIBFtNS9mHbY8KcOkQ/htsvH8FXw3EGH.dtZB7ClyQOXiIG', true, 1, 2, true, NULL, NULL, NULL
);

-- Insert categories
INSERT INTO public.sp_category (
    category_id, name, uid
) VALUES
    (1, 'Groceries', 1),
    (2, 'Rent', 1),
    (3, 'Utilities', 1),
    (4, 'Transportation', 1),
    (5, 'Entertainment', 1),
    (6, 'Healthcare', 1),
    (7, 'Insurance', 1),
    (8, 'Dining Out', 1),
    (9, 'Savings', 1),
    (10, 'Miscellaneous', 1);
