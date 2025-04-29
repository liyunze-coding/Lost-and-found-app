package com.deakin.lostandfound.model

import java.util.Date

data class Item(
    var id: Int? = null,
    var name: String,
    var lostOrFound: String,
    var phone: String,
    var description: String,
    var date: Date,
    var location: String
)
