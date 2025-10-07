"""
Authentication module for handling user authentication
"""

from datetime import datetime, timedelta
import jwt
from typing import Optional, Dict, Tuple

from ..models.user import User
from ..config import Config


class Authenticator:
    def __init__(self, config: Config):
        self.config = config
        self.secret_key = config.SECRET_KEY

    def create_token(self, user: User) -> str:
        """
        Create a JWT token for the user
        """
        payload = {
            "user_id": user.user_id,
            "username": user.username,
            "role": user.role,
            "exp": datetime.utcnow()
            + timedelta(hours=self.config.TOKEN_EXPIRATION_HOURS),
        }
        return jwt.encode(payload, self.secret_key, algorithm="HS256")

    def verify_token(self, token: str) -> Tuple[bool, Optional[Dict]]:
        """
        Verify a JWT token and return the payload if valid
        """
        try:
            payload = jwt.decode(token, self.secret_key, algorithms=["HS256"])
            return True, payload
        except jwt.ExpiredSignatureError:
            return False, {"error": "Token has expired"}
        except jwt.InvalidTokenError:
            return False, {"error": "Invalid token"}

    def login(
        self, user: User, password: str, salt: str
    ) -> Tuple[bool, Optional[str], str]:
        """
        Attempt to log in a user
        Returns: (success, token, message)
        """
        if not user.is_active:
            return False, None, "Account is deactivated"

        if user.login_attempts >= self.config.MAX_LOGIN_ATTEMPTS:
            return False, None, "Account is locked due to too many failed attempts"

        if user.verify_password(password, salt):
            user.login_attempts = 0
            user.last_login = datetime.utcnow()
            token = self.create_token(user)
            return True, token, "Login successful"
        else:
            user.login_attempts += 1
            return False, None, "Invalid credentials"

    def logout(self, token: str) -> bool:
        """
        Logout a user by invalidating their token
        Note: In a real implementation, you might want to maintain a blacklist of invalidated tokens
        """
        success, _ = self.verify_token(token)
        return success

    def check_permission(self, token: str, required_role: str) -> bool:
        """
        Check if the user has the required role
        """
        success, payload = self.verify_token(token)
        if not success:
            return False

        user_role = payload.get("role", "")
        # Simple role hierarchy: admin > moderator > user
        role_hierarchy = {"admin": 3, "moderator": 2, "user": 1}

        user_level = role_hierarchy.get(user_role, 0)
        required_level = role_hierarchy.get(required_role, 0)

        return user_level >= required_level
