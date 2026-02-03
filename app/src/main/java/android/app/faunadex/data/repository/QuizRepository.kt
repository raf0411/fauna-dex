package android.app.faunadex.data.repository

import android.app.faunadex.domain.model.Question
import android.app.faunadex.domain.model.Quiz
import android.app.faunadex.domain.model.QuizAttempt
import android.app.faunadex.domain.model.UserAnswer
import android.app.faunadex.domain.repository.QuizRepository
import android.app.faunadex.domain.repository.QuizStats
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class QuizRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : QuizRepository {

    // TODO: Replace with actual Firestore calls when ready
    // For now, using dummy data that matches Firebase structure

    private val quizzesCollection = firestore.collection("quizzes")
    private val questionsCollection = firestore.collection("questions")
    private val attemptsCollection = firestore.collection("quiz_attempts")

    // Dummy data (will be replaced with Firestore queries)
    private val dummyQuizzes = listOf(
        Quiz(
            id = "quiz_1",
            title = "Animal Habitats",
            imageUrl = "https://images.unsplash.com/photo-1446891574402-9b1e68b5e58e?w=400&h=300&fit=crop",
            totalQuestions = 5,
            educationLevel = "SMP",
            category = "Habitat",
            difficulty = "easy",
            xpReward = 100,
            timeLimitSeconds = 30,
            questionIds = listOf("q1", "q2", "q3", "q4", "q5"),
            isActive = true,
            createdAt = Date(),
            description = "Test your knowledge about different animal habitats around the world."
        ),
        Quiz(
            id = "quiz_2",
            title = "Species Classification",
            imageUrl = "https://images.unsplash.com/photo-1444080748397-f442aa95c3e5?w=400&h=300&fit=crop",
            totalQuestions = 5,
            educationLevel = "SMA",
            category = "Classification",
            difficulty = "medium",
            xpReward = 150,
            timeLimitSeconds = 30,
            questionIds = listOf("q6", "q7", "q8", "q9", "q10"),
            isActive = true,
            createdAt = Date(),
            description = "Master the taxonomy and classification of animal species."
        ),
        Quiz(
            id = "quiz_3",
            title = "Endangered Animals",
            imageUrl = "https://images.unsplash.com/photo-1500463959177-e0869687df26?q=80&w=1170&auto=format&fit=crop",
            totalQuestions = 5,
            educationLevel = "SMP",
            category = "Conservation",
            difficulty = "medium",
            xpReward = 120,
            timeLimitSeconds = 30,
            questionIds = listOf("q11", "q12", "q13", "q14", "q15"),
            isActive = true,
            createdAt = Date(),
            description = "Learn about critically endangered species and conservation efforts worldwide."
        )
    )

    private val dummyQuestions = listOf(
        // Quiz 1 Questions
        Question("q1", "quiz_1", "What is the natural habitat of the Komodo Dragon?", "multiple_choice",
            listOf("Arctic Tundra", "Tropical Islands", "Desert", "Rainforest"), 1, "Komodo dragons are native to Indonesian islands.", "easy", 0),
        Question("q2", "quiz_1", "Which habitat do polar bears primarily live in?", "multiple_choice",
            listOf("Desert", "Arctic", "Savanna", "Jungle"), 1, "Polar bears live in the Arctic regions.", "easy", 1),
        Question("q3", "quiz_1", "Where do camels naturally thrive?", "multiple_choice",
            listOf("Desert", "Ocean", "Mountains", "Swamp"), 0, "Camels are adapted to desert environments.", "easy", 2),
        Question("q4", "quiz_1", "What is the primary habitat of penguins?", "multiple_choice",
            listOf("Tropical Forest", "Antarctic", "Desert", "Grassland"), 1, "Most penguins live in Antarctica.", "easy", 3),
        Question("q5", "quiz_1", "Which animals live in the Savanna?", "multiple_choice",
            listOf("Penguins", "Lions", "Polar Bears", "Dolphins"), 1, "Lions are common in African savannas.", "easy", 4),

        // Quiz 2 Questions
        Question("q6", "quiz_2", "What is the scientific name of the Komodo Dragon?", "multiple_choice",
            listOf("Varanus komodoensis", "Panthera tigris", "Elephas maximus", "Rhinoceros sondaicus"), 0, "Varanus komodoensis is the scientific name.", "medium", 0),
        Question("q7", "quiz_2", "Which class do birds belong to?", "multiple_choice",
            listOf("Mammalia", "Reptilia", "Aves", "Amphibia"), 2, "Birds belong to class Aves.", "medium", 1),
        Question("q8", "quiz_2", "What phylum do all vertebrates belong to?", "multiple_choice",
            listOf("Arthropoda", "Chordata", "Mollusca", "Annelida"), 1, "Chordata includes all vertebrates.", "medium", 2),
        Question("q9", "quiz_2", "Which kingdom do animals belong to?", "multiple_choice",
            listOf("Plantae", "Fungi", "Animalia", "Protista"), 2, "All animals belong to Kingdom Animalia.", "medium", 3),
        Question("q10", "quiz_2", "What order do cats belong to?", "multiple_choice",
            listOf("Primates", "Carnivora", "Rodentia", "Cetacea"), 1, "Cats belong to order Carnivora.", "medium", 4),

        // Quiz 3 Questions
        Question("q11", "quiz_3", "Which animal is critically endangered?", "multiple_choice",
            listOf("House Cat", "Javan Rhino", "Pigeon", "Rat"), 1, "Javan Rhino is critically endangered.", "medium", 0),
        Question("q12", "quiz_3", "What is the main threat to endangered species?", "multiple_choice",
            listOf("Natural Selection", "Habitat Loss", "Evolution", "Migration"), 1, "Habitat loss is a major threat.", "medium", 1),
        Question("q13", "quiz_3", "Which organization focuses on species conservation?", "multiple_choice",
            listOf("WHO", "NASA", "WWF", "FIFA"), 2, "WWF (World Wildlife Fund) focuses on conservation.", "medium", 2),
        Question("q14", "quiz_3", "How many Javan Rhinos are estimated to be left?", "multiple_choice",
            listOf("Less than 100", "Around 500", "Over 1000", "Extinct"), 0, "Less than 100 Javan Rhinos remain.", "medium", 3),
        Question("q15", "quiz_3", "What does IUCN stand for?", "multiple_choice",
            listOf("International Union for Conservation of Nature", "Indonesian Union of Natural Conservation", "International University Conservation Network", "International United Conservation Nations"), 0, "IUCN is the International Union for Conservation of Nature.", "medium", 4)
    )

    private val userAttempts = mutableMapOf<String, MutableList<QuizAttempt>>()

    override suspend fun getQuizzes(educationLevel: String?): Result<List<Quiz>> {
        return try {
            // Simulate network delay
            delay(500)

            // TODO: Replace with Firestore query
            // val snapshot = quizzesCollection
            //     .whereEqualTo("is_active", true)
            //     .apply { educationLevel?.let { whereEqualTo("education_level", it) } }
            //     .get()
            //     .await()

            val filteredQuizzes = if (educationLevel != null) {
                dummyQuizzes.filter { it.educationLevel == educationLevel }
            } else {
                dummyQuizzes
            }

            Result.success(filteredQuizzes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getQuizById(quizId: String): Result<Quiz> {
        return try {
            delay(300)

            // TODO: Replace with Firestore query
            // val document = quizzesCollection.document(quizId).get().await()
            // val quiz = document.toObject(Quiz::class.java)

            val quiz = dummyQuizzes.find { it.id == quizId }
                ?: return Result.failure(Exception("Quiz not found"))

            Result.success(quiz)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getQuizzesByCategory(category: String): Result<List<Quiz>> {
        return try {
            delay(400)

            // TODO: Replace with Firestore query
            // val snapshot = quizzesCollection
            //     .whereEqualTo("category", category)
            //     .whereEqualTo("is_active", true)
            //     .get()
            //     .await()

            val quizzes = dummyQuizzes.filter { it.category == category }
            Result.success(quizzes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getQuestionsByQuizId(quizId: String): Result<List<Question>> {
        return try {
            delay(400)

            // TODO: Replace with Firestore query
            // val snapshot = questionsCollection
            //     .whereEqualTo("quiz_id", quizId)
            //     .orderBy("order_index")
            //     .get()
            //     .await()

            val questions = dummyQuestions
                .filter { it.quizId == quizId }
                .sortedBy { it.orderIndex }

            Result.success(questions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getQuestionById(questionId: String): Result<Question> {
        return try {
            delay(200)

            val question = dummyQuestions.find { it.id == questionId }
                ?: return Result.failure(Exception("Question not found"))

            Result.success(question)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun startQuizAttempt(userId: String, quizId: String): Result<QuizAttempt> {
        return try {
            val quiz = getQuizById(quizId).getOrNull()
                ?: return Result.failure(Exception("Quiz not found"))

            val attemptId = UUID.randomUUID().toString()
            val attempt = QuizAttempt(
                id = attemptId,
                userId = userId,
                quizId = quizId,
                totalQuestions = quiz.totalQuestions,
                isCompleted = false,
                startedAt = Date()
            )

            // TODO: Save to Firestore
            // attemptsCollection.document(attemptId).set(attempt).await()

            // Store in memory for now
            if (!userAttempts.containsKey(userId)) {
                userAttempts[userId] = mutableListOf()
            }
            userAttempts[userId]?.add(attempt)

            Result.success(attempt)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun submitQuizAttempt(attempt: QuizAttempt): Result<Unit> {
        return try {
            delay(300)

            // TODO: Update Firestore
            // attemptsCollection.document(attempt.id)
            //     .set(attempt.copy(isCompleted = true, completedAt = Date()))
            //     .await()

            // Update in memory
            val userAttemptsList = userAttempts[attempt.userId]
            val index = userAttemptsList?.indexOfFirst { it.id == attempt.id } ?: -1
            if (index >= 0 && userAttemptsList != null) {
                userAttemptsList[index] = attempt.copy(isCompleted = true, completedAt = Date())
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserQuizAttempts(userId: String, quizId: String?): Result<List<QuizAttempt>> {
        return try {
            delay(300)

            // TODO: Query Firestore
            // val query = attemptsCollection
            //     .whereEqualTo("user_id", userId)
            //     .apply { quizId?.let { whereEqualTo("quiz_id", it) } }
            //     .orderBy("started_at", Query.Direction.DESCENDING)

            val attempts = userAttempts[userId]?.filter {
                quizId == null || it.quizId == quizId
            } ?: emptyList()

            Result.success(attempts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserCompletedQuizIds(userId: String): Result<List<String>> {
        return try {
            delay(200)

            // TODO: Query Firestore
            // val snapshot = attemptsCollection
            //     .whereEqualTo("user_id", userId)
            //     .whereEqualTo("is_completed", true)
            //     .get()
            //     .await()

            val completedQuizIds = userAttempts[userId]
                ?.filter { it.isCompleted }
                ?.map { it.quizId }
                ?.distinct()
                ?: emptyList()

            Result.success(completedQuizIds)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserQuizStats(userId: String): Result<QuizStats> {
        return try {
            delay(300)

            val attempts = userAttempts[userId] ?: emptyList()
            val completed = attempts.filter { it.isCompleted }

            val stats = QuizStats(
                totalQuizzesTaken = attempts.size,
                totalQuizzesCompleted = completed.size,
                totalXpEarned = completed.sumOf { it.xpEarned },
                averageScore = if (completed.isNotEmpty()) {
                    completed.map { it.score }.average()
                } else 0.0,
                bestScore = completed.maxOfOrNull { it.score } ?: 0
            )

            Result.success(stats)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
