"""
Main entry point for the authentication system
"""

import logging
from pathlib import Path
from typing import Optional, Tuple, Dict
import os


def delete_all_files_in_folder(folder):
    for root, dirs, files in os.walk(folder):
        for file in files:
            file_path = os.path.join(root, file)
            try:
                os.remove(file_path)
                print(f"Deleted: {file_path}")
            except Exception as e:
                print(f"Failed to delete {file_path}: {e}")


# 用法示例：程序启动时删除 text 目录下所有文件
delete_all_files_in_folder(r"D:\WebAuthSystem\WebAuthSystem\text")

from config import Config
from models.user import User
from auth.authenticator import Authenticator
from database.db_utils import DatabaseManager


class AuthSystem:
    def __init__(self):
        self.config = Config()
        self.db = DatabaseManager(self.config)
        self.auth = Authenticator(self.config)
        self._setup_logging()

    def _setup_logging(self) -> None:
        """
        Configure logging for the authentication system
        """
        logging.basicConfig(
            level=logging.INFO,
            format="%(asctime)s - %(name)s - %(levelname)s - %(message)s",
            handlers=[logging.FileHandler("auth_system.log"), logging.StreamHandler()],
        )

    def register_user(
        self, username: str, email: str, password: str, role: str = "user"
    ) -> Tuple[bool, str]:
        """
        Register a new user
        Returns: (success, message)
        """
        # Validate input
        if len(password) < self.config.MIN_PASSWORD_LENGTH:
            return (
                False,
                f"Password must be at least {self.config.MIN_PASSWORD_LENGTH} characters",
            )

        # Check if user exists
        if self.db.get_user_by_username(username):
            return False, "Username already exists"
        if self.db.get_user_by_email(email):
            return False, "Email already exists"

        # Create new user
        password_hash, salt = User.hash_password(password)
        new_user = User(
            username=username, email=email, password_hash=password_hash, role=role
        )

        user_id = self.db.create_user(new_user, password)
        if user_id:
            logging.info(f"New user registered: {username}")
            return True, "User registered successfully"
        return False, "Failed to register user"

    def login(self, username: str, password: str) -> Tuple[bool, Optional[str], str]:
        """
        Login a user
        Returns: (success, token, message)
        """
        user_data = self.db.get_user_by_username(username)
        if not user_data:
            return False, None, "User not found"

        user = User.from_dict(user_data)
        success, token, message = self.auth.login(user, password, user_data["salt"])

        if success:
            self.db.update_login_attempts(user.user_id, 0)
            logging.info(f"User logged in: {username}")
        else:
            self.db.update_login_attempts(user.user_id, user.login_attempts + 1)
            logging.warning(f"Failed login attempt for user: {username}")

        return success, token, message

    def logout(self, token: str) -> bool:
        """
        Logout a user
        """
        success = self.auth.logout(token)
        if success:
            logging.info("User logged out successfully")
        return success

    def verify_token(self, token: str) -> Tuple[bool, Dict]:
        """
        Verify a user's token
        """
        return self.auth.verify_token(token)

    def check_permission(self, token: str, required_role: str) -> bool:
        """
        Check if a user has the required role
        """
        return self.auth.check_permission(token, required_role)


def main():
    """
    Main function for testing the authentication system
    """
    auth_system = AuthSystem()

    # Example usage
    success, message = auth_system.register_user(
        "test_user", "test@example.com", "password123"
    )
    print(f"Register result: {message}")

    success, token, message = auth_system.login("test_user", "password123")
    if success:
        print(f"Login successful. Token: {token}")

        # Verify token
        is_valid, payload = auth_system.verify_token(token)
        print(f"Token valid: {is_valid}")
        if is_valid:
            print(f"Token payload: {payload}")

        # Check permissions
        has_permission = auth_system.check_permission(token, "user")
        print(f"Has user permission: {has_permission}")

        # Logout
        logout_success = auth_system.logout(token)
        print(f"Logout successful: {logout_success}")
    else:
        print(f"Login failed: {message}")


if __name__ == "__main__":
    main()
