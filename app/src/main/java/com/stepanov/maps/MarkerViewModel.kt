package com.stepanov.maps

import androidx.lifecycle.ViewModel
import com.yandex.mapkit.map.PlacemarkMapObject

class MarkerViewModel: ViewModel() {
    var markers: ArrayList<PlacemarkMapObject> = arrayListOf()
}