"""
User model for the authentication system
"""

from datetime import datetime
from typing import Optional, Dict
import hashlib
import secrets


class User:
    def __init__(
        self,
        username: str,
        email: str,
        password_hash: str,
        user_id: Optional[int] = None,
        created_at: Optional[datetime] = None,
        is_active: bool = True,
        role: str = "user",
    ):
        self.user_id = user_id
        self.username = username
        self.email = email
        self.password_hash = password_hash
        self.created_at = created_at or datetime.utcnow()
        self.is_active = is_active
        self.role = role
        self.last_login = None
        self.login_attempts = 0

    @staticmethod
    def hash_password(password: str, salt: Optional[str] = None) -> tuple[str, str]:
        """
        Hash a password with a salt using SHA-256
        """
        if salt is None:
            salt = secrets.token_hex(16)

        salted_password = password + salt
        hashed = hashlib.sha256(salted_password.encode()).hexdigest()
        return hashed, salt

    def verify_password(self, password: str, salt: str) -> bool:
        """
        Verify a password against the stored hash
        """
        attempted_hash, _ = self.hash_password(password, salt)
        return attempted_hash == self.password_hash

    def to_dict(self) -> Dict:
        """
        Convert user object to dictionary
        """
        return {
            "user_id": self.user_id,
            "username": self.username,
            "email": self.email,
            "created_at": self.created_at.isoformat(),
            "is_active": self.is_active,
            "role": self.role,
            "last_login": self.last_login.isoformat() if self.last_login else None,
        }

    @classmethod
    def from_dict(cls, data: Dict) -> "User":
        """
        Create a user object from dictionary
        """
        return cls(
            user_id=data.get("user_id"),
            username=data["username"],
            email=data["email"],
            password_hash=data["password_hash"],
            created_at=datetime.fromisoformat(data["created_at"])
            if data.get("created_at")
            else None,
            is_active=data.get("is_active", True),
            role=data.get("role", "user"),
        )

    def __repr__(self) -> str:
        return f"<User {self.username}>"
