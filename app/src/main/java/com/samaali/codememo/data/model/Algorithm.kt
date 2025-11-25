package com.samaali.codememo.data.model

data class Algorithm(
    val id: Int,
    val name: String,
    val category: String,
    val description: String,
    val pseudocode: String,
    val python: String,
    val exampleInput: String,
    val exampleOutput: String
)