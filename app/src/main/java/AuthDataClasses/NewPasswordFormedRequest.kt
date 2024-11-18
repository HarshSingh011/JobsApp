package AuthDataClasses

data class NewPasswordFormedRequest(
    val contact: String = "",
    val newPassword: String = "",
    val confirmPassword: String = ""
)
