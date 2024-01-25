package com.dn0ne.moneymate.app.domain

import kotlinx.coroutines.flow.Flow
import org.mongodb.kbson.ObjectId

interface DataSource {
    /**
     * @return List of all [Spending]s wrapped in [Flow]
     */
    fun getSpendings(): Flow<List<Spending>>

    /**
     * Inserts the given [Spending] into the database
     * @param spending Spending to insert
     */
    suspend fun insertSpending(spending: Spending)

    /**
     * Updates the given [Spending] in the database
     * @param spending Spending to update
     */
    suspend fun updateSpending(spending: Spending)

    /**
     * Deletes the [Spending] with the given Id from database
     * @param id Id of the spending to delete
     */
    suspend fun deleteSpending(id: ObjectId)

    /**
     * @return List of all [Category]s wrapped in [Flow]
     */
    fun getCategories(): Flow<List<Category>>

    /**
     * Inserts the given [Category] into the database
     * @param category Category to insert
     */
    suspend fun insertCategory(category: Category)

    /**
     * Deletes the [Category] with the given Id from database
     * @param id Id of the [Category] to delete
     */
    suspend fun deleteCategory(id: ObjectId)

    /**
     * Updates the given [Category] in the database
     * @param category Spending to update
     */
    suspend fun updateCategory(category: Category)
}