package jurisprudence_hub_be.common.validation;

import java.util.regex.Pattern;

public final class PasswordValidator {

    private static final Pattern STRONG_PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$");

    private PasswordValidator() {
    }

    public static boolean isStrong(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasUppercase = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLowercase = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);

        return hasUppercase && hasLowercase && hasDigit;
    }

    public static boolean isVeryStrong(String password) {
        if (password == null) {
            return false;
        }
        return STRONG_PASSWORD_PATTERN.matcher(password).matches();
    }

    public static String getStrengthMessage(String password) {
        if (password == null) {
            return "Password cannot be null";
        }
        if (password.length() < 8) {
            return "Password must be at least 8 characters long";
        }
        if (!password.chars().anyMatch(Character::isUpperCase)) {
            return "Password must contain at least one uppercase letter";
        }
        if (!password.chars().anyMatch(Character::isLowerCase)) {
            return "Password must contain at least one lowercase letter";
        }
        if (!password.chars().anyMatch(Character::isDigit)) {
            return "Password must contain at least one digit";
        }
        return "Password is strong";
    }
}
