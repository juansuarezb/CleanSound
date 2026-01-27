package model

data class UserProfile(
    val uid: String = "",
    val username: String = "",
    val email: String = "",
    val photoUrl: String = "",
    val createdAt: Any? = null
)
