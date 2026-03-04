import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class SurveyRequest(
    val streakMilestone: Int,
    val answers: JsonObject
)