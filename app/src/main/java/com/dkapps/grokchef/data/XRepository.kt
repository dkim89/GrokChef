package com.dkapps.grokchef.data

import android.util.Log
import com.dkapps.grokchef.data.api.XApiService
import com.dkapps.grokchef.data.model.GrokImageCompletionRequest
import com.dkapps.grokchef.data.model.GrokImageCompletionResponse
import com.dkapps.grokchef.data.model.ImageUrl
import com.dkapps.grokchef.data.model.ImageUrlContentPart
import com.dkapps.grokchef.data.model.Message
import com.dkapps.grokchef.data.model.TextContentPart
import retrofit2.Response
import javax.inject.Inject
class XRepository @Inject constructor(
    private val xApiService: XApiService,
) {
    suspend fun postImageUnderstandingIngredients(base64ImageUri: String): Response<GrokImageCompletionResponse> {
        val grokImageCompletionRequest = GrokImageCompletionRequest(
            model = "grok-4",
            messages = listOf(
                Message(
                    role = "user",
                    content = listOf(
                        ImageUrlContentPart(
                            type = "image_url",
                            imageUrl = ImageUrl(
                                url = "data:image/jpeg;base64,${base64ImageUri}",
                                detail = "auto"
                            )
                        ),
                        TextContentPart(
                            type = "text",
                            text = "Can you return a string list of food ingredients from this image using comma separated values?"
                        )
                    )
                )
            ))
        return xApiService.postChatCompletion(grokImageCompletionRequest)
    }
}