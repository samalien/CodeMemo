package com.samaali.codememo.data.model

interface CodeItem {
    val id: Int
    val name: String
    val description: String
    val pseudocode: String
    val python: String
    val exampleInput: String
    val exampleOutput: String
    val category: String?  // ← Rendu optionnel avec String?
}