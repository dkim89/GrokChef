package com.dkapps.grokchef.ui.ingredients

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dkapps.grokchef.data.XRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException
import java.io.IOException
import javax.inject.Inject
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asStateFlow
import java.net.SocketTimeoutException

sealed interface IngredientsUiState {
    // Display loading state with text.
    data class Loading(val loadingMessage: String) : IngredientsUiState

    // Display ingredients on screen.
    data class Success(val ingredients: List<String>) : IngredientsUiState

    // Display error message on screen.
    data class Error(val errorMessage: String) : IngredientsUiState
}

@HiltViewModel
class IngredientsViewModel @Inject constructor(
    private val xRepository: XRepository,
    savedStateHandle: SavedStateHandle,
    private val application: Application
) : ViewModel() {
    companion object {
        // Define wait duration before displaying loading state update.
        const val LONG_REQUEST_WARNING_MILLIS = 10000L // 10S
    }

    val encodedImagePath = savedStateHandle.get<String>("encodedImagePath")
    private val _uiState = MutableStateFlow<IngredientsUiState>(
        IngredientsUiState.Loading(
            "Analyzing ingredients..."
        )
    )
    val uiState: StateFlow<IngredientsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            if (encodedImagePath != null) {
                Uri.decode(encodedImagePath).toUri().let { uri ->
                    val base64Image = readUriContent(application, uri)
                    if (base64Image != null) {
                        generateIngredients(base64Image)
                    }
                }
            } else {
                _uiState.update { IngredientsUiState.Error("No image URI found.") }
            }
        }
    }

    private suspend fun readUriContent(context: Context, uri: Uri): String? {
        return withContext(Dispatchers.IO) {
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val bytes = inputStream.readBytes()
                    Base64.encodeToString(bytes, Base64.NO_WRAP)
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                updateUiStateToError("Image file not found.")
                null
            } catch (e: IOException) {
                e.printStackTrace()
                updateUiStateToError("Could not read image file.")
                null
            } catch (e: Exception) {
                e.printStackTrace()
                updateUiStateToError("Unknown error while reading image.")
                null
            }
        }
    }

    private suspend fun generateIngredients(base64Image: String) {
        // Notify UI that request is taking longer than expected.
        val longRunningJob = viewModelScope.launch {
            delay(LONG_REQUEST_WARNING_MILLIS)
            _uiState.update {
                if (it is IngredientsUiState.Loading) {
                    IngredientsUiState.Loading("Still analyzing...")
                } else {
                    it
                }
            }
        }
        try {
            val response = xRepository.postFoodImageUnderstandingChat(base64Image)
            if (response.isSuccessful) {
                response.body()?.let { responseBody ->
                    if (responseBody.choices.isNotEmpty()) {
                        val ingredientsList =
                            responseBody.choices[0].message.content
                                .split(",")
                                .map { it.trim() }
                                .filter { it.isNotBlank() }
                        _uiState.update { IngredientsUiState.Success(ingredientsList) }
                    } else {
                        updateUiStateToError("No ingredients found while searching ingredients.")
                    }
                }
            } else {
                // Handle API error returned by the server (e.g., 4xx, 5xx status codes).
                val errorMessage =
                    response.errorBody()?.string() ?: "Unknown error occurred from server."
                Log.e("IngredientsViewModel", "API Error: ${response.code()} - $errorMessage")
                updateUiStateToError("Server failed to generate list of ingredients.")
            }
        } catch (e: SocketTimeoutException) {
            // Network request timed out.
            e.printStackTrace()
            updateUiStateToError("Request timed out by server when analyzing ingredients.")
        } catch (e: IOException) {
            // Network issues (e.g., no internet, host unreachable).
            e.printStackTrace()
            updateUiStateToError("Network error occurred when analyzing ingredients.")
        } catch (e: Exception) {
            // Catch any other unexpected network exceptions.
            e.printStackTrace()
            updateUiStateToError("An unknown error occurred while analyzing ingredients.")
        } finally {
            longRunningJob.cancel()
        }
    }

    fun removeIngredient(ingredientToRemove: String) {
        val currentState = _uiState.value
        if (currentState is IngredientsUiState.Success) {
            val updatedIngredients = currentState.ingredients.filter { it != ingredientToRemove }
            _uiState.update { IngredientsUiState.Success(updatedIngredients) }
        }
    }

    private fun updateUiStateToError(errorMessage: String) {
        _uiState.update { IngredientsUiState.Error(errorMessage) }
    }
}