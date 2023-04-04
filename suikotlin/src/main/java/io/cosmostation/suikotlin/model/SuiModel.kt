package io.cosmostation.suikotlin.model

data class SuiObjectInfo(
    val objectId: String, val version: Int, val digest: String, val type: String, val owner: SuiObjectOwner, val previousTransaction: String
)

data class SuiObjectData(
    val data: SuiObjectInfo,
)

data class SuiNextCursor(val objectId: String)

data class SuiObjectDataResult(
    val data: List<SuiObjectData>, val hasNextPage: Boolean, val nextCursor: SuiNextCursor
)

data class SuiTransactionDataResult(val data: List<SuiTransaction>, val hasNextPage: Boolean, val nextCursor: String)

data class SuiTransactionBlockResponseOptions(val showInput: Boolean = false, val showRawInput: Boolean = false, val showEffects: Boolean = false, val showEvents: Boolean = false, val showObjectChanges: Boolean = false, val showBalanceChanges: Boolean = false)

data class SuiTransaction(val digest: String, val transaction: Any, val effects: Any, val balanceChanges: Any, val timestampMs: Long)
data class SuiObjectOwner(val AddressOwner: String)

data class SuiWrappedTxBytes(
    val txBytes: String, val gas: SuiObjectRef, val inputObjects: List<ImmOrOwnedMoveObject>
)

data class ImmOrOwnedMoveObject(val ImmOrOwnedMoveObject: SuiObjectRef)

data class SuiObjectRef(val objectId: String, val version: Int, val digest: String)

data class SuiTransactionQueryFilter(val filter: TransactionQuery, val options: SuiTransactionBlockResponseOptions)
sealed class TransactionQuery {
    class ToAddress(val ToAddress: String) : TransactionQuery()
    class FromAddress(val FromAddress: String) : TransactionQuery()
}
