package com.example.news.retrofit

enum class CategoryApiQuery(val value: String) {
    BUSINESS("business"),
    ENTERTAINMENT("entertainment"),
    GENERAL("general"),
    HEALTH("health"),
    SCIENCE("science"),
    SPORTS("sports"),
    TECHNOLOGY("technology");

    override fun toString(): String {
        return value
    }

    fun getKeyFromValue(value: String): CategoryApiQuery? {
        return entries.find { it.value == value }
    }
}