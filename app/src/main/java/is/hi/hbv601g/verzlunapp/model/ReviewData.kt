package `is`.hi.hbv601g.verzlunapp.model

import java.util.UUID

data class ReviewData(
    val reviewId: UUID,
    val productId: UUID,
    val rating: Int,
    val comment: String?,
    val date: String?,
    val reviewerName: String?,
    val reviewerEmail: String?
)