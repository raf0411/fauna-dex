package android.app.faunadex.data.repository

import android.app.faunadex.presentation.quiz.QuizDetail

object QuizRepository {
    private val dummyQuizzes = listOf(
        QuizDetail(
            id = "1",
            title = "Animal Habitats",
            imageUrl = "https://images.unsplash.com/photo-1446891574402-9b1e68b5e58e?w=400&h=300&fit=crop",
            totalQuestions = 10,
            educationLevel = "SMP",
            description = "Test your knowledge about different animal habitats around the world. Learn where various species live and what makes each habitat unique. From rainforests to deserts, explore the diverse environments that animals call home."
        ),
        QuizDetail(
            id = "2",
            title = "Species Classification",
            imageUrl = "https://images.unsplash.com/photo-1444080748397-f442aa95c3e5?w=400&h=300&fit=crop",
            totalQuestions = 15,
            educationLevel = "SMA",
            description = "Master the taxonomy and classification of animal species. This comprehensive quiz covers kingdom, phylum, class, order, family, genus, and species categories. Challenge yourself with questions about how scientists organize the animal kingdom."
        ),
        QuizDetail(
            id = "3",
            title = "Endangered Animals",
            imageUrl = "https://images.unsplash.com/photo-1500463959177-e0869687df26?q=80&w=1170&auto=format&fit=crop",
            totalQuestions = 8,
            educationLevel = "SMP",
            description = "Learn about critically endangered species and conservation efforts worldwide. Discover what threats these animals face and how we can help protect them. This quiz highlights the importance of biodiversity and habitat preservation."
        ),
        QuizDetail(
            id = "4",
            title = "Bird Species",
            imageUrl = "https://images.unsplash.com/photo-1441974231531-c6227db76b6e?w=400&h=300&fit=crop",
            totalQuestions = 12,
            educationLevel = "SMP",
            description = "Identify and learn about fascinating bird species from around the world. From majestic eagles to colorful parrots, test your knowledge about avian behavior, migration patterns, and unique characteristics. Explore the incredible diversity of birds."
        ),
        QuizDetail(
            id = "5",
            title = "Marine Life",
            imageUrl = "https://images.unsplash.com/photo-1505142468610-359e7d316be0?w=400&h=300&fit=crop",
            totalQuestions = 14,
            educationLevel = "SMA",
            description = "Dive into the ocean and discover amazing marine creatures. Learn about deep-sea animals, coral reef inhabitants, and the complex ecosystems beneath the waves. This quiz covers everything from sharks to seahorses."
        ),
        QuizDetail(
            id = "6",
            title = "Mammal Adaptation",
            imageUrl = "https://images.unsplash.com/photo-1484406566174-9da000fda645?w=400&h=300&fit=crop",
            totalQuestions = 11,
            educationLevel = "SMP",
            description = "Explore how mammals adapt to their environments. From arctic foxes to desert camels, learn about physical and behavioral adaptations that help animals survive in extreme conditions. Understand evolution and natural selection."
        )
    )

    fun getQuizzes(): List<QuizDetail> = dummyQuizzes

    fun getQuizById(quizId: String): QuizDetail? = dummyQuizzes.find { it.id == quizId }

    fun getCompletedQuizzes(): List<QuizDetail> = dummyQuizzes.takeLast(2)

    fun getAvailableQuizzes(): List<QuizDetail> = dummyQuizzes.dropLast(2)
}
