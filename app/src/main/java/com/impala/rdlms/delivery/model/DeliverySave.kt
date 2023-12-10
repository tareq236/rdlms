package com.impala.rdlms.delivery.model

data class DeliverySave(
    var billing_doc_no: String,
    var billing_date: String,
    var route_code: String,
    var partner: String,
    var gate_pass_no: String,
    var da_code: String,
    var vehicle_no: String,
    var delivery_latitude: String,
    var delivery_longitude: String,
    var transport_type: String,
    var delivery_status: String,
    var last_status: String,
    var type: String,
    var cash_collection: Double,
    var cash_collection_latitude: String?,
    var cash_collection_longitude: String?,
    var cash_collection_status: String?,
    var deliverys: List<DeliveryList>
)

data class DeliveryList(
    var matnr: String,
    var batch: String,
    var quantity: Int,
    var tp: Double,
    var vat: Double,
    var net_val: Double,
    var delivery_quantity: Int,
    var delivery_net_val: Double,
    var return_quantity: Int,
    var return_net_val: Double,
    var id: Int?
)
