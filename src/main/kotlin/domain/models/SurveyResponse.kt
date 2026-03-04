import kotlinx.serialization.Serializable

@Serializable
data class SurveyResponse(
    val id: Int = 0,
    val idUser: Int,
    val streakMilestone: Int,
    val answers: String,
    val avgSleepLast10: Double? = null,
    val avgBatteryLast10: Double? = null,
    val answeredAt: String
)