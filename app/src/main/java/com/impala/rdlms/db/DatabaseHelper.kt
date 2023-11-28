package com.impala.rdlms.db


import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import com.impala.rdlms.delivery.model.Product


const val DATABASENAME = "rd_ms"

class DatabaseHelper(var context: Context) : SQLiteOpenHelper(
    context,
    DATABASENAME, null,
    3
) {


    private val TABLE_PRODUCT = "product"


    //subjects
    private val COL_ID = "id"
    private val PRODUCT_NAME = "product_name"
    private val PRODUCT_ID = "product_id"
    private val INVOICE_ID = "invoice_id"
    private val QTY = "qty"
    private val TP = "tp"
    private val VAT = "vat"
    private val RECEIVED_QTY = "received_qty"
    private val RECEIVED_AMOUNT = "batch"




    private val prodTable = " CREATE TABLE " +
            TABLE_PRODUCT +
            " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            INVOICE_ID + " VARCHAR(256)  ," +
            PRODUCT_ID + " VARCHAR(256)  UNIQUE," +
            PRODUCT_NAME + " VARCHAR(256)  ," +
            QTY + " VARCHAR(256)  ," +
            TP + " VARCHAR(256)  ," +
            VAT + " VARCHAR(256)  ," +
            RECEIVED_QTY + " VARCHAR(256)  ," +
            RECEIVED_AMOUNT + " VARCHAR(256) )"


    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(prodTable)

    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        p0!!.execSQL("DROP TABLE IF EXISTS '$prodTable'");

    }


    @SuppressLint("Range")
    fun getAllProductData(invoiceId:String): MutableList<ProductModel> {
        val list: MutableList<ProductModel> = ArrayList()
        val db = this.readableDatabase
        val query = "Select * from $TABLE_PRODUCT WHERE invoice_id = ?"
        val result = db.rawQuery(query,arrayOf(invoiceId))

        if (result.moveToFirst()) {
            do {

                val prodId = result.getString(result.getColumnIndex(PRODUCT_ID))
                val prodName = result.getString(result.getColumnIndex(PRODUCT_NAME))
                val totalQty = result.getString(result.getColumnIndex(QTY))
                val receivedQty = result.getString(result.getColumnIndex(RECEIVED_QTY))
                val receivedAmount = result.getString(result.getColumnIndex(RECEIVED_AMOUNT))
                val tp = result.getString(result.getColumnIndex(TP))
                val vat = result.getString(result.getColumnIndex(VAT))
                val invoiceId = result.getString(result.getColumnIndex(INVOICE_ID))


                val model = ProductModel(prodId,prodName,totalQty,tp,vat,receivedQty,receivedAmount,invoiceId)
                list.add(model)
            } while (result.moveToNext())
        }
        return list
    }

    fun saveData(
        invoiceId: String,
        productId: String,
        productName: String,
        qty: String,
        tp: String,
        vat: String,
        receivedQty: String,
        receivedAmount: String,

    ) {
        val database = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(INVOICE_ID, invoiceId)
        contentValues.put(PRODUCT_ID, productId)
        contentValues.put(PRODUCT_NAME, productName)
        contentValues.put(QTY, qty)
        contentValues.put(TP, tp)
        contentValues.put(VAT, vat)
        contentValues.put(RECEIVED_AMOUNT, receivedAmount)
        contentValues.put(RECEIVED_QTY, receivedQty)


        val result = database.insert(TABLE_PRODUCT, null, contentValues)
        if (result == (0).toLong()) {
             Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
        } else {
              Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
        }
    }


    fun clearAllCartData() {
        val database = this.writableDatabase
        database.execSQL(" delete from cart ")
    }
    fun deleteCartItem(id: String) {
        val db = this.writableDatabase
        db.execSQL("delete from $TABLE_PRODUCT where SERVICE_ID='$id'")
    }

    fun updateReceivedQty(invoiceId: String, qty: String?,productId:String) {
        val database = this.writableDatabase
        val cv = ContentValues()
        cv.put(INVOICE_ID, invoiceId)
        cv.put(RECEIVED_QTY, qty)
        cv.put(PRODUCT_ID, productId)


        val dbUpdate = database.update(
            TABLE_PRODUCT,
            cv,
            " invoice_id = ? AND product_id = ? ",
            arrayOf(invoiceId,productId)
        ).toLong()

        if (dbUpdate == (0).toLong()) {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
        }
    }

    fun isExistData(invoiceId: String): Boolean {
        val database = this.writableDatabase
        val cursor: Cursor =  database.rawQuery(
            "Select * from product where invoice_id = $invoiceId",
            null as Array<String?>?
        )
        if (cursor.count <= 0) {
            cursor.close()
            return false
        }
        cursor.close()
        return true
    }

}