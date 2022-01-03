package com.example.mtmstask.map.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mtmstask.BuildConfig
import com.example.mtmstask.database.dao.SourceDao
import com.example.mtmstask.database.model.SourceModel
import com.example.mtmstask.handleData.ErrorLiveData
import com.example.mtmstask.network.ApiManager
import com.example.mtmstask.network.WebServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapViewModel : ViewModel() {

    private var sourceDao: SourceDao = SourceDao()
    private val apiManager = ApiManager().getClient()!!.create(WebServices::class.java)

    private var placesList: MutableList<String> = mutableListOf()

    var handleData = ErrorLiveData<List<SourceModel>>()
    var handlePlaces = ErrorLiveData<List<String>>()

    fun getAllSource() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                sourceDao.getAllSource({
                    it.let {
                        val sourceList: List<SourceModel> =
                            it.toObjects(SourceModel::class.java)

                        handleData.postSuccess(sourceList)
                    }

                }, {
                    handleData.postError("Something went wrong")
                })
            } catch (error: Exception) {
                handleData.postConnectionError("Internet Connection Failure")
            }
        }
    }

    fun getAllPlaces(place: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = apiManager.getPlaces(place, BuildConfig.apiKey)

                if (response.isSuccessful) {
                    for (x in response.body()!!.predictions!!) {
                        placesList.addAll(listOf(x!!.description.toString()))
                    }
                    handlePlaces.postSuccess(placesList)
                }
            } catch (error: Exception) {
                handlePlaces.postConnectionError("Internet Connection Failure")
            }
        }
    }
}