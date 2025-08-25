package com.dkapps.grokchef.data.model

import com.google.gson.annotations.SerializedName

/**
 * Represents the request body for a Grok Chat Completion,
 * supporting both text and image input for multi-modal understanding.
 * https://docs.x.ai/docs/guides/image-understanding
 */
data class GrokChatCompletionRequest(
    val model: String, // e.g., "grok-4" or the specific image model name
    @SerializedName("messages") val messages: List<Message>
)

data class Message(
    val role: String, // Typically "user" for the input message
    val content: List<ContentPart> // List of text and image parts
)

// Sealed interface to allow for different types of content parts
sealed interface ContentPart {
    val type: String
}

data class TextContentPart(
    override val type: String = "text",
    val text: String
) : ContentPart

data class ImageUrlContentPart(
    override val type: String = "image_url",
    @SerializedName("image_url")
    val imageUrl: ImageUrl // Represents the image source
) : ContentPart

data class ImageUrl(
    val url: String, // Can be a direct public URL or a Base64 encoded image string (e.g., "data:image/jpeg;base64,...")
    val detail: String? = null // Optional: "low", "high", or "auto" for image quality/processing. Check Grok docs for support.
)