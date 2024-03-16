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
}