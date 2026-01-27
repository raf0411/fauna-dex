package android.app.faunadex.domain.model

import com.google.firebase.firestore.PropertyName

data class Animal(
    val id: String = "",

    // Localized fields - populated based on user's language preference
    val name: String = "",
    val category: String = "",
    val habitat: String = "",
    val description: String = "",
    val longDescription: String = "",
    val funFact: String = "",
    val diet: String = "",
    val lifespan: String = "",
    val specialTitle: String = "",
    val endemicStatus: String = "",
    val populationTrend: String = "",
    val activityPeriod: String = "",
    val protectionType: String = "",
    val sizeCategory: String = "",
    val rarityLevel: String = "",
    val country: String = "",
    val city: String = "",
    val audioDescriptionUrl: String = "",
    val audioFunFactUrl: String = "",

    // Non-localized fields - same for all languages
    @PropertyName("scientific_name")
    val scientificName: String = "",

    @PropertyName("conservation_status")
    val conservationStatus: String = "",

    @PropertyName("image_url")
    val imageUrl: String? = null,

    @PropertyName("weight")
    val weight: String = "",

    @PropertyName("length")
    val length: String = "",

    @PropertyName("is_protected")
    val isProtected: Boolean = false,

    @PropertyName("population_past")
    val populationPast: Int = 0,

    @PropertyName("population_present")
    val populationPresent: Int = 0,

    @PropertyName("latitude")
    val latitude: Double = 0.0,

    @PropertyName("longitude")
    val longitude: Double = 0.0,

    @PropertyName("domain")
    val domain: String = "",

    @PropertyName("kingdom")
    val kingdom: String = "",

    @PropertyName("phylum")
    val phylum: String = "",

    @PropertyName("class")
    val taxonomyClass: String = "",

    @PropertyName("order")
    val order: String = "",

    @PropertyName("family")
    val family: String = "",

    @PropertyName("genus")
    val genus: String = "",

    @PropertyName("species")
    val species: String = "",

    @PropertyName("ar_model_url")
    val arModelUrl: String? = null
)

