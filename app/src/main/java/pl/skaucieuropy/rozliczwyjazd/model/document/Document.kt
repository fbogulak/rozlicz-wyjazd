package pl.skaucieuropy.rozliczwyjazd.model.document

import java.util.*

data class Document(
    val id: Long,
    var number: String,
    var type: String,
    var date: Date,
    var category: String,
    var amount: Double,
    var comment: String,
    var isFromTroopAccount: Boolean
)