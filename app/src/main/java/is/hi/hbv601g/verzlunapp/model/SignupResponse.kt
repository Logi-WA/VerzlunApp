package `is`.hi.hbv601g.verzlunapp.model

data class SignupResponse(
    val success: Boolean,
    val message: String?,
    val userId: Long?,
    val username: String?
)
