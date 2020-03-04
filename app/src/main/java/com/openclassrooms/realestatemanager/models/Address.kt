package com.openclassrooms.realestatemanager.models

data class Address(val streetNumber: String? = null,
                   val street: String? = null,
                   val apartmentNumber: String? = null,
                   val city: String? = null,
                   val postalCode: String? = null,
                   val country: String? = null
)
