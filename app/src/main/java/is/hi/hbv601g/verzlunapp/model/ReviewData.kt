package `is`.hi.hbv601g.verzlunapp.model

import java.time.OffsetDateTime
import java.util.UUID

data class ReviewData(
    val reviewId: UUID,
    val productId: UUID,
    val rating: Int,
    val comment: String?,
    val date: OffsetDateTime?,
    val reviewerName: String?,
    val reviewerEmail: String?
)