package com.dn0ne.moneymate.app.data.repository

import com.dn0ne.moneymate.app.domain.entities.change.Change
import com.dn0ne.moneymate.app.domain.entities.spending.Category
import com.dn0ne.moneymate.app.domain.entities.spending.Spending
import com.dn0ne.moneymate.app.domain.repository.ChangeRepository
import com.dn0ne.moneymate.app.domain.repository.SpendingRepository
import kotlinx.coroutines.flow.Flow
import org.mongodb.kbson.ObjectId

class SyncRepository(
    private val spendingRepository: SpendingRepository,
    private val changeRepository: ChangeRepository
) : SpendingRepository {
    override fun getSpendings(): Flow<List<Spending>> {
        return spendingRepository.getSpendings()
    }

    override suspend fun insertSpending(spending: Spending) {
        spendingRepository.insertSpending(spending)
        changeRepository.insertChange(
            Change.InsertSpending(document = spending)
        )
    }

    override suspend fun updateSpending(spending: Spending) {
        spendingRepository.updateSpending(spending)
        changeRepository.insertChange(
            Change.UpdateSpending(document = spending)
        )
    }

    override suspend fun deleteSpending(id: ObjectId) {
        changeRepository.insertChange(
            Change.DeleteSpending(documentId = id)
        )
        spendingRepository.deleteSpending(id)
    }

    override fun getCategories(): Flow<List<Category>> {
        return spendingRepository.getCategories()
    }

    override suspend fun insertCategory(category: Category) {
        spendingRepository.insertCategory(category)
        changeRepository.insertChange(
            Change.InsertCategory(document = category)
        )
    }

    override suspend fun deleteCategory(id: ObjectId) {
        changeRepository.insertChange(
            Change.DeleteCategory(documentId = id)
        )
        spendingRepository.deleteCategory(id)
    }

    override suspend fun updateCategory(category: Category) {
        spendingRepository.updateCategory(category)
        changeRepository.insertChange(
            Change.UpdateCategory(document = category)
        )
    }

}