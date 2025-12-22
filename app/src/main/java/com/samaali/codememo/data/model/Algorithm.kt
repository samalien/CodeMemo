package com.samaali.codememo.data.model

data class Algorithm(
    override val id: Int,
    override val name: String,
    override val description: String,
    override val category: String,
    override val pseudocode: String,
    override val python: String,
    override val exampleInput: String,
    override val exampleOutput: String
) : CodeItem