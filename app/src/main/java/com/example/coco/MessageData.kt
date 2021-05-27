package com.example.coco

data class MessageData(
        val session_id: Int,
        val message: String,
        val lat: String? = null,
        val lng: String? = null,
        val tel: String? = null,
        val hospital_info: HospitalData? = null
)
