package `is`.hi.hbv601g.verzlunapp.model

import java.util.UUID

data class ReviewRequest(
    val productId: UUID,
    val rating: Int,
    val comment: String,
    val reviewerName: String,
    val reviewerEmail: String
)