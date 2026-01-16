package android.app.faunadex.domain.model

import com.google.firebase.firestore.PropertyName

data class Animal(
    val id: String = "",

    @PropertyName("name")
    val name: String = "",

    @PropertyName("scientific_name")
    val scientificName: String = "",

    @PropertyName("category")
    val category: String = "",

    @PropertyName("habitat")
    val habitat: String = "",

    @PropertyName("description")
    val description: String = "",

    @PropertyName("conservation_status")
    val conservationStatus: String = "",

    @PropertyName("image_url")
    val imageUrl: String? = null,

    @PropertyName("fun_fact")
    val funFact: String = "",

    @PropertyName("diet")
    val diet: String = "",

    @PropertyName("lifespan")
    val lifespan: String = "",

    @PropertyName("weight")
    val weight: String = "",

    @PropertyName("length")
    val length: String = "",

    @PropertyName("special_title")
    val specialTitle: String = "",

    @PropertyName("endemic_status")
    val endemicStatus: String = "",

    @PropertyName("population_trend")
    val populationTrend: String = "",

    @PropertyName("activity_period")
    val activityPeriod: String = "",

    @PropertyName("is_protected")
    val isProtected: Boolean = false,

    @PropertyName("protection_type")
    val protectionType: String = "Protected by Law",

    @PropertyName("size_category")
    val sizeCategory: String = "",

    @PropertyName("rarity_level")
    val rarityLevel: String = ""
)

