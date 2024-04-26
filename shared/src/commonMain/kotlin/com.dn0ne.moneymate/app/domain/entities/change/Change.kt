package com.dn0ne.moneymate.app.domain.entities.change

import com.dn0ne.moneymate.app.domain.entities.spending.Category
import com.dn0ne.moneymate.app.domain.entities.spending.Spending
import io.realm.kotlin.types.RealmObject
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.mongodb.kbson.ObjectId

@Serializable
sealed class Change: RealmObject {
    @Contextual
    abstract var changeId: ObjectId

    @Serializable
    @SerialName("Change.InsertSpending")
    class InsertSpending(): Change(), RealmObject {
        @Contextual override var changeId: ObjectId = ObjectId()
        var document: Spending? = null

        constructor(document: Spending): this() {
            this.document = document
        }
    }

    @Serializable
    @SerialName("Change.UpdateSpending")
    class UpdateSpending(): Change(), RealmObject {
        @Contextual override var changeId: ObjectId = ObjectId()
        var document: Spending? = null

        constructor(document: Spending): this() {
            this.document = document
        }
    }

    @Serializable
    @SerialName("Change.DeleteSpending")
    class DeleteSpending(): Change(), RealmObject {
        @Contextual override var changeId: ObjectId = ObjectId()
        @Contextual var documentId: ObjectId = ObjectId()

        constructor(documentId: ObjectId): this() {
            this.documentId = documentId
        }
    }

    @Serializable
    @SerialName("Change.InsertCategory")
    class InsertCategory(): Change(), RealmObject {
        @Contextual override var changeId: ObjectId = ObjectId()
        var document: Category? = null

        constructor(document: Category): this() {
            this.document = document
        }
    }

    @Serializable
    @SerialName("Change.UpdateCategory")
    class UpdateCategory(): Change(), RealmObject {
        @Contextual override var changeId: ObjectId = ObjectId()
        var document: Category? = null

        constructor(document: Category): this() {
            this.document = document
        }
    }

    @Serializable
    @SerialName("Change.DeleteCategory")
    class DeleteCategory(): Change(), RealmObject {
        @Contextual override var changeId: ObjectId = ObjectId()
        @Contextual var documentId: ObjectId = ObjectId()

        constructor(documentId: ObjectId): this() {
            this.documentId = documentId
        }
    }
}

/*
@Serializable
sealed class Change: RealmObject {
    @Contextual
    abstract val changeId: ObjectId

    @Serializable
    @SerialName("Change.InsertSpending")
    data class InsertSpending(
        @Contextual override val changeId: ObjectId = ObjectId(),
        val document: Spending
    ) : Change()

    @Serializable
    @SerialName("Change.UpdateSpending")
    data class UpdateSpending(
        @Contextual override val changeId: ObjectId = ObjectId(),
        val document: Spending
    ) : Change()

    @Serializable
    @SerialName("Change.DeleteSpending")
    data class DeleteSpending(
        @Contextual override val changeId: ObjectId = ObjectId(),
        val document: Spending
    ) : Change()

    @Serializable
    @SerialName("Change.InsertCategory")
    data class InsertCategory(
        @Contextual override val changeId: ObjectId = ObjectId(),
        val document: Category
    ) : Change()

    @Serializable
    @SerialName("Change.UpdateCategory")
    data class UpdateCategory(
        @Contextual override val changeId: ObjectId = ObjectId(),
        val document: Category
    ) : Change()

    @Serializable
    @SerialName("Change.DeleteCategory")
    data class DeleteCategory(
        @Contextual override val changeId: ObjectId = ObjectId(),
        val document: Category
    ) : Change()
}*/
