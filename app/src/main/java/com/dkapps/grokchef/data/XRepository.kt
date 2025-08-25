package com.dkapps.grokchef.data

import com.dkapps.grokchef.data.api.XApiService
import com.dkapps.grokchef.data.model.GrokChatCompletionRequest
import com.dkapps.grokchef.data.model.GrokChatCompletionResponse
import com.dkapps.grokchef.data.model.ImageUrl
import com.dkapps.grokchef.data.model.ImageUrlContentPart
import com.dkapps.grokchef.data.model.Message
import com.dkapps.grokchef.data.model.TextContentPart
import retrofit2.Response
import javax.inject.Inject

class XRepository @Inject constructor(
    private val xApiService: XApiService,
) {
    suspend fun postFoodImageUnderstandingChat(base64ImageUri: String): Response<GrokChatCompletionResponse> {
        val foodImageUnderstandingRequest = GrokChatCompletionRequest(
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
            )
        )
        return xApiService.postChatCompletion(foodImageUnderstandingRequest)
    }

    suspend fun postRecipeLiveSearchChat(ingredients: String): Response<GrokChatCompletionResponse> {
        val recipeLiveSearchRequest = GrokChatCompletionRequest(
            model = "grok-4",
            messages = listOf(
                Message(
                    role = "user",
                    content = listOf(
                        TextContentPart(
                            type = "text",
                            text = "Can you show me a food recipe in with html body tags, not including <body> tags, using the following list of ingredients: : $ingredients"
                        )
                    )
                )
            )
        )
        return xApiService.postChatCompletion(recipeLiveSearchRequest)
    }
}