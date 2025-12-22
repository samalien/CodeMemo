package com.samaali.codememo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_exercise")
data class UserExercise(
    @PrimaryKey(autoGenerate = true)
    override val id: Int = 0,
    override val name: String,
    override val description: String,
    override val pseudocode: String,
    override val python: String,
    override val exampleInput: String = "",
    override val exampleOutput: String = "",
    override val category: String? = null  // ← Optionnel, null par défaut
) : CodeItem