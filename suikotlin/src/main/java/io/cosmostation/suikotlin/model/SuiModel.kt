package io.cosmostation.suikotlin.model

data class SuiObjectInfo(val objectId: String, val version: Int, val digest: String, val type: String, val content: Any, val owner: SuiObjectOwner, val previousTransaction: String)

data class SuiObjectData(val data: SuiObjectInfo)

//data class SuiObjectDataResult(val data: List<SuiObjectData>, val hasNextPage: Boolean, val nextCursor: String)
data class SuiObjectDataResult(val data: List<SuiObjectData>, val hasNextPage: Boolean)

data class SuiTransactionDataResult(val data: List<SuiTransaction>, val hasNextPage: Boolean, val nextCursor: String)

data class SuiTransactionBlockResponseOptions(val showInput: Boolean = false, val showRawInput: Boolean = false, val showEffects: Boolean = false, val showEvents: Boolean = false, val showObjectChanges: Boolean = false, val showBalanceChanges: Boolean = false)

data class SuiTransaction(val digest: String, val transaction: Any, val effects: Any, val balanceChanges: Any, val timestampMs: Long)

data class SuiObjectOwner(val AddressOwner: String)

data class SuiWrappedTxBytes(val txBytes: String, val gas: List<SuiObjectRef>, val inputObjects: List<ImmOrOwnedMoveObject>)

data class ImmOrOwnedMoveObject(val ImmOrOwnedMoveObject: SuiObjectRef)

data class SuiObjectRef(val objectId: String, val version: Int, val digest: String)

data class SuiTransactionQueryFilter(val filter: TransactionQuery, val options: SuiTransactionBlockResponseOptions)

data class SuiObjectDataOptions(val showType: Boolean = true, val showContent: Boolean = false, val showDisplay: Boolean = false, val showStorageRebate: Boolean = false, val showPreviousTransaction: Boolean = false, val showOwner: Boolean = false, val showBcs: Boolean = false)

data class SuiObjectDataFilter(val Package: String)

data class SuiObjectResponseQuery(val filter: SuiObjectDataFilter?, val options: SuiObjectDataOptions?)
sealed class TransactionQuery {
    class ToAddress(val ToAddress: String) : TransactionQuery()
    class FromAddress(val FromAddress: String) : TransactionQuery()
}
