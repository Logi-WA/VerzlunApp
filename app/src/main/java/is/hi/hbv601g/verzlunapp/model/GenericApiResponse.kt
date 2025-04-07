package `is`.hi.hbv601g.verzlunapp.model

data class GenericApiResponse<T>(
    val success: Boolean,
    val message: String?,
    val data: T?,
    val timestamp: String?
)
