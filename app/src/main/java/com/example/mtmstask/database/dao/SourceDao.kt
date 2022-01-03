package com.example.mtmstask.database.dao

import com.example.mtmstask.database.FireStoreDatabase
import com.example.mtmstask.database.model.SourceModel
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.QuerySnapshot

class SourceDao {

    private var fireStoreDatabase: FireStoreDatabase = FireStoreDatabase()

/*    fun addSource(user: SourceModel) {
        val reference = fireStoreDatabase.getLocationRef().document()
        user.uuIds = reference.id
        reference.set(user)
    }*/

    fun getAllSource(
        onSuccessListener: OnSuccessListener<QuerySnapshot>,
        onFailureListener: OnFailureListener
    ) {
        fireStoreDatabase.getLocationRef()
            .get().addOnSuccessListener(onSuccessListener)
            .addOnFailureListener(onFailureListener)
    }
}