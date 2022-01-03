package com.example.mtmstask.database

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class FireStoreDatabase {

    private val sourceRef: String = "Source"

    private var database: FirebaseFirestore? = null

    private fun getInstance(): FirebaseFirestore {
        database = FirebaseFirestore.getInstance()
        if (database == null) {
            database = FirebaseFirestore.getInstance()
        }
        return database!!
    }

    fun getLocationRef(): CollectionReference {
        return getInstance().collection(sourceRef)
    }
}
