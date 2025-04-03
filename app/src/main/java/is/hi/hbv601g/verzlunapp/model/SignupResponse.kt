package `is`.hi.hbv601g.verzlunapp.model

import java.util.UUID

data class UserData(
    val id: UUID,
    val name: String?,
    val email: String?
)

data class SignupResponse(
    val success: Boolean,
    val message: String?,
    val data: UserData?,
    val timestamp: String?
)