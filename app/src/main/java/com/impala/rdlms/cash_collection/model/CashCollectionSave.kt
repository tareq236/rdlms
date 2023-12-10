package com.impala.rdlms.cash_collection.model

import com.impala.rdlms.delivery.model.DeliveryList

data class CashCollectionSave(
    var billing_doc_no: String,
    var last_status: String,
    var type: String,
    var cash_collection: Double,
    var cash_collection_latitude: String?,
    var cash_collection_longitude: String?,
    var cash_collection_status: String?,
    var deliverys: List<CashCollectionList>
)

data class CashCollectionList(
    var return_quantity: Int,
    var return_net_val: Double,
    var id: Int?
)
