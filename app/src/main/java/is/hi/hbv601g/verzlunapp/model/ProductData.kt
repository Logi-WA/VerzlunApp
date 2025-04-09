package `is`.hi.hbv601g.verzlunapp.model

import java.util.UUID

data class ProductData(
    val id: UUID,
    val name: String?,
    val price: Double?,
    val description: String?,
    val rating: Double?,
    val brand: String?,
    val tags: List<String>?,
    val categoryName: String?
)