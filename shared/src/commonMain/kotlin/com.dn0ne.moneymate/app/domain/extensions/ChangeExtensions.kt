package com.dn0ne.moneymate.app.domain.extensions

import com.dn0ne.moneymate.app.domain.entities.change.Change

fun List<Change>.optimizeChanges(): List<Change> {
    val deletedSpendingIds = filterIsInstance<Change.DeleteSpending>().map { it.documentId }
    val insertedSpendingIds = filterIsInstance<Change.InsertSpending>().map { it.document?.id }

    val deletedCategoryIds = filterIsInstance<Change.DeleteCategory>().map { it.documentId }
    val insertedCategoryIds = filterIsInstance<Change.InsertCategory>().map { it.document?.id }

    val optimizedList = filter { change ->
        when(change) {
            is Change.InsertCategory -> change.document?.id !in deletedCategoryIds
            is Change.UpdateCategory -> change.document?.id !in deletedCategoryIds
            is Change.DeleteCategory -> change.documentId !in insertedCategoryIds

            is Change.InsertSpending -> change.document?.id !in deletedSpendingIds
            is Change.UpdateSpending -> change.document?.id !in deletedSpendingIds
            is Change.DeleteSpending -> change.documentId !in insertedSpendingIds
        }
    }

    return optimizedList
}

fun List<Change>.filterDeleted(remoteChanges: List<Change>): List<Change> {
    val deletedSpendingIds = remoteChanges.filterIsInstance<Change.DeleteSpending>().map { it.documentId }
    val deletedCategoryIds = remoteChanges.filterIsInstance<Change.DeleteCategory>().map { it.documentId }

    return filter { change ->
        when (change) {
            is Change.DeleteCategory -> change.documentId !in deletedCategoryIds
            is Change.DeleteSpending -> change.documentId !in deletedSpendingIds
            is Change.UpdateCategory -> change.document?.id !in deletedCategoryIds
            is Change.UpdateSpending -> change.document?.id !in deletedSpendingIds
            else -> true
        }
    }
}