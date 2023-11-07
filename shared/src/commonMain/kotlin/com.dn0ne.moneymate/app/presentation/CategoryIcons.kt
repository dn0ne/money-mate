package com.dn0ne.moneymate.app.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Apartment
import androidx.compose.material.icons.rounded.BakeryDining
import androidx.compose.material.icons.rounded.BeachAccess
import androidx.compose.material.icons.rounded.BrokenImage
import androidx.compose.material.icons.rounded.BusinessCenter
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Checkroom
import androidx.compose.material.icons.rounded.DirectionsBike
import androidx.compose.material.icons.rounded.DirectionsBoat
import androidx.compose.material.icons.rounded.DirectionsBus
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.DirectionsSubway
import androidx.compose.material.icons.rounded.ElectricBolt
import androidx.compose.material.icons.rounded.ElectricMeter
import androidx.compose.material.icons.rounded.EvStation
import androidx.compose.material.icons.rounded.Fastfood
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Flight
import androidx.compose.material.icons.rounded.GasMeter
import androidx.compose.material.icons.rounded.Handyman
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Kitchen
import androidx.compose.material.icons.rounded.LocalBar
import androidx.compose.material.icons.rounded.LocalCafe
import androidx.compose.material.icons.rounded.LocalGasStation
import androidx.compose.material.icons.rounded.LocalMall
import androidx.compose.material.icons.rounded.LocalPizza
import androidx.compose.material.icons.rounded.LocalShipping
import androidx.compose.material.icons.rounded.LocalTaxi
import androidx.compose.material.icons.rounded.MedicalServices
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.Redeem
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.Savings
import androidx.compose.material.icons.rounded.Storefront
import androidx.compose.material.icons.rounded.Train
import androidx.compose.material.icons.rounded.TwoWheeler
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.ui.graphics.vector.ImageVector

object CategoryIcons {

    private val map = mapOf(
        // Food
        "restaurant" to Icons.Rounded.Restaurant,
        "cafe" to Icons.Rounded.LocalCafe,
        "fastfood" to Icons.Rounded.Fastfood,
        "fridge" to Icons.Rounded.Kitchen,
        "bar" to Icons.Rounded.LocalBar,
        "pizza" to Icons.Rounded.LocalPizza,
        "bakery" to Icons.Rounded.BakeryDining,
        // Transport
        "car" to Icons.Rounded.DirectionsCar,
        "airplane" to Icons.Rounded.Flight,
        "bus" to Icons.Rounded.DirectionsBus,
        "bike" to Icons.Rounded.DirectionsBike,
        "train" to Icons.Rounded.Train,
        "motorcycle" to Icons.Rounded.TwoWheeler,
        "boat" to Icons.Rounded.DirectionsBoat,
        "taxi" to Icons.Rounded.LocalTaxi,
        "subway" to Icons.Rounded.DirectionsSubway,
        // Related to Transport,
        "gas" to Icons.Rounded.LocalGasStation,
        "ev" to Icons.Rounded.EvStation,
        // Places
        "home" to Icons.Rounded.Home,
        "apartment" to Icons.Rounded.Apartment,
        "checkroom" to Icons.Rounded.Checkroom,
        "storefront" to Icons.Rounded.Storefront,
        "fitness" to Icons.Rounded.FitnessCenter,
        "business" to Icons.Rounded.BusinessCenter,
        "beach" to Icons.Rounded.BeachAccess,
        "mall" to Icons.Rounded.LocalMall,
        // Home
        "electricity" to Icons.Rounded.ElectricBolt,
        "electric-meter" to Icons.Rounded.ElectricMeter,
        "gas-meter" to Icons.Rounded.GasMeter,
        "water" to Icons.Rounded.WaterDrop,
        // Other
        "medicine" to Icons.Rounded.MedicalServices,
        "shipping" to Icons.Rounded.LocalShipping,
        "place" to Icons.Rounded.Place,
        "instruments" to Icons.Rounded.Handyman,
        "payments" to Icons.Rounded.Payments,
        "savings" to Icons.Rounded.Savings,
        "gift" to Icons.Rounded.Redeem,
        "category" to Icons.Rounded.Category
    )

    val icons: List<ImageVector> get() = map.values.toList()
    val names: List<String> get() = map.keys.toList()

    fun getIconByName(name: String): ImageVector {
        return map[name] ?: Icons.Rounded.BrokenImage
    }
}