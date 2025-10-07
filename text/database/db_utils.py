"""
Database utilities for the authentication system
"""

import sqlite3
from typing import Optional, List, Dict, Any
import logging
from pathlib import Path

from ..config import Config
from ..models.user import User


class DatabaseManager:
    def __init__(self, config: Config):
        self.config = config
        self.db_path = Path(config.DATABASE_NAME)
        self._init_db()

    def _init_db(self) -> None:
        """
        Initialize the database and create necessary tables
        """
        try:
            with self._get_connection() as conn:
                cursor = conn.cursor()

                # Create users table
                cursor.execute("""
                    CREATE TABLE IF NOT EXISTS users (
                        user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                        username TEXT UNIQUE NOT NULL,
                        email TEXT UNIQUE NOT NULL,
                        password_hash TEXT NOT NULL,
                        salt TEXT NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        is_active BOOLEAN DEFAULT TRUE,
                        role TEXT DEFAULT 'user',
                        last_login TIMESTAMP,
                        login_attempts INTEGER DEFAULT 0
                    )
                """)

                conn.commit()
        except sqlite3.Error as e:
            logging.error(f"Database initialization error: {e}")
            raise

    def _get_connection(self) -> sqlite3.Connection:
        """
        Get a database connection
        """
        try:
            conn = sqlite3.connect(self.db_path)
            conn.row_factory = sqlite3.Row
            return conn
        except sqlite3.Error as e:
            logging.error(f"Database connection error: {e}")
            raise

    def create_user(self, user: User, password: str) -> Optional[int]:
        """
        Create a new user in the database
        Returns: user_id if successful, None if failed
        """
        try:
            password_hash, salt = User.hash_password(password)
            with self._get_connection() as conn:
                cursor = conn.cursor()
                cursor.execute(
                    """
                    INSERT INTO users (username, email, password_hash, salt, role, is_active)
                    VALUES (?, ?, ?, ?, ?, ?)
                """,
                    (
                        user.username,
                        user.email,
                        password_hash,
                        salt,
                        user.role,
                        user.is_active,
                    ),
                )
                conn.commit()
                return cursor.lastrowid
        except sqlite3.IntegrityError:
            logging.error(f"User creation failed: Username or email already exists")
            return None
        except sqlite3.Error as e:
            logging.error(f"Database error during user creation: {e}")
            return None

    def get_user_by_username(self, username: str) -> Optional[Dict[str, Any]]:
        """
        Retrieve user data by username
        """
        try:
            with self._get_connection() as conn:
                cursor = conn.cursor()
                cursor.execute("SELECT * FROM users WHERE username = ?", (username,))
                row = cursor.fetchone()
                return dict(row) if row else None
        except sqlite3.Error as e:
            logging.error(f"Database error while fetching user: {e}")
            return None

    def get_user_by_email(self, email: str) -> Optional[Dict[str, Any]]:
        """
        Retrieve user data by email
        """
        try:
            with self._get_connection() as conn:
                cursor = conn.cursor()
                cursor.execute("SELECT * FROM users WHERE email = ?", (email,))
                row = cursor.fetchone()
                return dict(row) if row else None
        except sqlite3.Error as e:
            logging.error(f"Database error while fetching user: {e}")
            return None

    def update_user(self, user: User) -> bool:
        """
        Update user information
        """
        try:
            with self._get_connection() as conn:
                cursor = conn.cursor()
                cursor.execute(
                    """
                    UPDATE users
                    SET email = ?, is_active = ?, role = ?, last_login = ?, login_attempts = ?
                    WHERE user_id = ?
                """,
                    (
                        user.email,
                        user.is_active,
                        user.role,
                        user.last_login,
                        user.login_attempts,
                        user.user_id,
                    ),
                )
                conn.commit()
                return True
        except sqlite3.Error as e:
            logging.error(f"Database error during user update: {e}")
            return False

    def delete_user(self, user_id: int) -> bool:
        """
        Delete a user from the database
        """
        try:
            with self._get_connection() as conn:
                cursor = conn.cursor()
                cursor.execute("DELETE FROM users WHERE user_id = ?", (user_id,))
                conn.commit()
                return True
        except sqlite3.Error as e:
            logging.error(f"Database error during user deletion: {e}")
            return False

    def get_all_users(self) -> List[Dict[str, Any]]:
        """
        Retrieve all users from the database
        """
        try:
            with self._get_connection() as conn:
                cursor = conn.cursor()
                cursor.execute("SELECT * FROM users")
                return [dict(row) for row in cursor.fetchall()]
        except sqlite3.Error as e:
            logging.error(f"Database error while fetching all users: {e}")
            return []

    def update_login_attempts(self, user_id: int, attempts: int) -> bool:
        """
        Update the number of login attempts for a user
        """
        try:
            with self._get_connection() as conn:
                cursor = conn.cursor()
                cursor.execute(
                    "UPDATE users SET login_attempts = ? WHERE user_id = ?",
                    (attempts, user_id),
                )
                conn.commit()
                return True
        except sqlite3.Error as e:
            logging.error(f"Database error updating login attempts: {e}")
            return False
