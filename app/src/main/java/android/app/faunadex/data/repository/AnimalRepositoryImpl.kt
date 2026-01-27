package android.app.faunadex.data.repository

import android.app.faunadex.domain.model.Animal
import android.app.faunadex.domain.repository.AnimalRepository
import android.app.faunadex.utils.LanguageManager
import android.content.Context
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AnimalRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val context: Context
) : AnimalRepository {

    private val animalsCollection = firestore.collection("animals")

    private fun getLanguageSuffix(): String {
        val language = LanguageManager.getLanguage(context)
        return if (language == LanguageManager.LANGUAGE_INDONESIAN) "id" else "en"
    }

    override suspend fun getAnimalById(animalId: String): Result<Animal> {
        return try {
            val document = animalsCollection.document(animalId).get().await()
            val lang = getLanguageSuffix()

            Log.d("AnimalRepositoryImpl", "Document exists: ${document.exists()}")
            Log.d("AnimalRepositoryImpl", "Using language: $lang")
            Log.d("AnimalRepositoryImpl", "Document data: ${document.data}")

            if (document.exists()) {
                val animal = Animal(
                    id = document.id,
                    // Localized fields
                    name = document.getString("name_$lang") ?: "",
                    category = document.getString("category_$lang") ?: "",
                    habitat = document.getString("habitat_$lang") ?: "",
                    description = document.getString("description_$lang") ?: "",
                    longDescription = document.getString("long_description_$lang") ?: "",
                    funFact = document.getString("fun_fact_$lang") ?: "",
                    diet = document.getString("diet_$lang") ?: "",
                    lifespan = document.getString("lifespan_$lang") ?: "",
                    specialTitle = document.getString("special_title_$lang") ?: "",
                    endemicStatus = document.getString("endemic_status_$lang") ?: "",
                    populationTrend = document.getString("population_trend_$lang") ?: "",
                    activityPeriod = document.getString("activity_period_$lang") ?: "",
                    protectionType = document.getString("protection_type_$lang") ?: "",
                    sizeCategory = document.getString("size_category_$lang") ?: "",
                    rarityLevel = document.getString("rarity_level_$lang") ?: "",
                    country = document.getString("country_$lang") ?: "",
                    city = document.getString("city_$lang") ?: "",
                    audioDescriptionUrl = document.getString("audio_url_$lang") ?: "",
                    audioFunFactUrl = document.getString("audio_fun_fact_url_$lang") ?: "",
                    // Non-localized fields
                    scientificName = document.getString("scientific_name") ?: "",
                    conservationStatus = document.getString("conservation_status") ?: "",
                    imageUrl = document.getString("image_url"),
                    weight = document.getString("weight") ?: "",
                    length = document.getString("length") ?: "",
                    isProtected = document.getBoolean("is_protected") ?: false,
                    populationPast = (document.getLong("population_past") ?: 0).toInt(),
                    populationPresent = (document.getLong("population_present") ?: 0).toInt(),
                    latitude = document.getDouble("latitude") ?: 0.0,
                    longitude = document.getDouble("longitude") ?: 0.0,
                    domain = document.getString("domain") ?: "",
                    kingdom = document.getString("kingdom") ?: "",
                    phylum = document.getString("phylum") ?: "",
                    taxonomyClass = document.getString("class") ?: "",
                    order = document.getString("order") ?: "",
                    family = document.getString("family") ?: "",
                    genus = document.getString("genus") ?: "",
                    species = document.getString("species") ?: "",
                    arModelUrl = document.getString("ar_model_url")
                )

                Log.d("AnimalRepositoryImpl", "Animal object created - name: '${animal.name}', audio: '${animal.audioDescriptionUrl}'")
                Result.success(animal)
            } else {
                Result.failure(Exception("Animal not found"))
            }
        } catch (e: Exception) {
            Log.e("AnimalRepositoryImpl", "Error getting animal", e)
            Result.failure(e)
        }
    }

    override suspend fun getAllAnimals(): Result<List<Animal>> {
        return try {
            val snapshot = animalsCollection.get().await()
            val lang = getLanguageSuffix()
            Log.d("AnimalRepositoryImpl", "getAllAnimals using language: $lang")

            val animals = snapshot.documents.mapNotNull { document ->
                try {
                    Animal(
                        id = document.id,
                        // Localized fields
                        name = document.getString("name_$lang") ?: "",
                        category = document.getString("category_$lang") ?: "",
                        habitat = document.getString("habitat_$lang") ?: "",
                        description = document.getString("description_$lang") ?: "",
                        longDescription = document.getString("long_description_$lang") ?: "",
                        funFact = document.getString("fun_fact_$lang") ?: "",
                        diet = document.getString("diet_$lang") ?: "",
                        lifespan = document.getString("lifespan_$lang") ?: "",
                        specialTitle = document.getString("special_title_$lang") ?: "",
                        endemicStatus = document.getString("endemic_status_$lang") ?: "",
                        populationTrend = document.getString("population_trend_$lang") ?: "",
                        activityPeriod = document.getString("activity_period_$lang") ?: "",
                        protectionType = document.getString("protection_type_$lang") ?: "",
                        sizeCategory = document.getString("size_category_$lang") ?: "",
                        rarityLevel = document.getString("rarity_level_$lang") ?: "",
                        country = document.getString("country_$lang") ?: "",
                        city = document.getString("city_$lang") ?: "",
                        audioDescriptionUrl = document.getString("audio_url_$lang") ?: "",
                        audioFunFactUrl = document.getString("audio_fun_fact_url_$lang") ?: "",
                        // Non-localized fields
                        scientificName = document.getString("scientific_name") ?: "",
                        conservationStatus = document.getString("conservation_status") ?: "",
                        imageUrl = document.getString("image_url"),
                        weight = document.getString("weight") ?: "",
                        length = document.getString("length") ?: "",
                        isProtected = document.getBoolean("is_protected") ?: false,
                        populationPast = (document.getLong("population_past") ?: 0).toInt(),
                        populationPresent = (document.getLong("population_present") ?: 0).toInt(),
                        latitude = document.getDouble("latitude") ?: 0.0,
                        longitude = document.getDouble("longitude") ?: 0.0,
                        domain = document.getString("domain") ?: "",
                        kingdom = document.getString("kingdom") ?: "",
                        phylum = document.getString("phylum") ?: "",
                        taxonomyClass = document.getString("class") ?: "",
                        order = document.getString("order") ?: "",
                        family = document.getString("family") ?: "",
                        genus = document.getString("genus") ?: "",
                        species = document.getString("species") ?: "",
                        arModelUrl = document.getString("ar_model_url")
                    )
                } catch (e: Exception) {
                    Log.e("AnimalRepositoryImpl", "Error parsing animal document", e)
                    null
                }
            }
            Result.success(animals)
        } catch (e: Exception) {
            Log.e("AnimalRepositoryImpl", "Error getting all animals", e)
            Result.failure(e)
        }
    }

    override suspend fun getAnimalsByCategory(category: String): Result<List<Animal>> {
        return try {
            val lang = getLanguageSuffix()
            Log.d("AnimalRepositoryImpl", "getAnimalsByCategory: category=$category, lang=$lang")

            val snapshot = animalsCollection
                .whereEqualTo("category_$lang", category)
                .get()
                .await()

            val animals = snapshot.documents.mapNotNull { document ->
                try {
                    Animal(
                        id = document.id,
                        // Localized fields
                        name = document.getString("name_$lang") ?: "",
                        category = document.getString("category_$lang") ?: "",
                        habitat = document.getString("habitat_$lang") ?: "",
                        description = document.getString("description_$lang") ?: "",
                        longDescription = document.getString("long_description_$lang") ?: "",
                        funFact = document.getString("fun_fact_$lang") ?: "",
                        diet = document.getString("diet_$lang") ?: "",
                        lifespan = document.getString("lifespan_$lang") ?: "",
                        specialTitle = document.getString("special_title_$lang") ?: "",
                        endemicStatus = document.getString("endemic_status_$lang") ?: "",
                        populationTrend = document.getString("population_trend_$lang") ?: "",
                        activityPeriod = document.getString("activity_period_$lang") ?: "",
                        protectionType = document.getString("protection_type_$lang") ?: "",
                        sizeCategory = document.getString("size_category_$lang") ?: "",
                        rarityLevel = document.getString("rarity_level_$lang") ?: "",
                        country = document.getString("country_$lang") ?: "",
                        city = document.getString("city_$lang") ?: "",
                        audioDescriptionUrl = document.getString("audio_url_$lang") ?: "",
                        audioFunFactUrl = document.getString("audio_fun_fact_url_$lang") ?: "",
                        // Non-localized fields
                        scientificName = document.getString("scientific_name") ?: "",
                        conservationStatus = document.getString("conservation_status") ?: "",
                        imageUrl = document.getString("image_url"),
                        weight = document.getString("weight") ?: "",
                        length = document.getString("length") ?: "",
                        isProtected = document.getBoolean("is_protected") ?: false,
                        populationPast = (document.getLong("population_past") ?: 0).toInt(),
                        populationPresent = (document.getLong("population_present") ?: 0).toInt(),
                        latitude = document.getDouble("latitude") ?: 0.0,
                        longitude = document.getDouble("longitude") ?: 0.0,
                        domain = document.getString("domain") ?: "",
                        kingdom = document.getString("kingdom") ?: "",
                        phylum = document.getString("phylum") ?: "",
                        taxonomyClass = document.getString("class") ?: "",
                        order = document.getString("order") ?: "",
                        family = document.getString("family") ?: "",
                        genus = document.getString("genus") ?: "",
                        species = document.getString("species") ?: "",
                        arModelUrl = document.getString("ar_model_url")
                    )
                } catch (e: Exception) {
                    Log.e("AnimalRepositoryImpl", "Error parsing animal document", e)
                    null
                }
            }
            Result.success(animals)
        } catch (e: Exception) {
            Log.e("AnimalRepositoryImpl", "Error getting animals by category", e)
            Result.failure(e)
        }
    }
}

