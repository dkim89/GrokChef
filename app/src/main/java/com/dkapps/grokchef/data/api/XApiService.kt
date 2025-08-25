package com.dkapps.grokchef.data.api

import com.dkapps.grokchef.data.model.GrokChatCompletionRequest
import com.dkapps.grokchef.data.model.GrokChatCompletionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface XApiService {

    /**
     * https://docs.x.ai/docs/guides/image-understanding
     */
    @POST("/v1/chat/completions")
    suspend fun postChatCompletion(
        @Body request: GrokChatCompletionRequest): Response<GrokChatCompletionResponse>
}