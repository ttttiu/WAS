"""
Configuration settings for the authentication system
"""


class Config:
    # Database configuration
    DATABASE_NAME = "auth_system.db"

    # Authentication settings
    TOKEN_EXPIRATION_HOURS = 24
    MIN_PASSWORD_LENGTH = 8
    MAX_LOGIN_ATTEMPTS = 3

    # Security settings
    SECRET_KEY = "your-secret-key-here"  # Change this in production
    SALT_LENGTH = 16

    # Session settings
    SESSION_TIMEOUT = 3600  # 1 hour in seconds

    # API settings
    API_VERSION = "v1"
    BASE_URL = "http://localhost:5000"
