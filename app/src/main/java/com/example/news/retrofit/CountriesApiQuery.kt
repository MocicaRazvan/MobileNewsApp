package com.example.news.retrofit

enum class CountriesApiQuery(val value: String) {
    US("us"),
    RO("ro");

    override fun toString(): String {
        return value
    }

    fun getKeyFromValue(value: String): CountriesApiQuery? {
        return entries.find { it.value == value }
    }

}