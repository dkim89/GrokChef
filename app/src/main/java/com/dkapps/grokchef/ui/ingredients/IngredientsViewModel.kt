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

data class IngredientsViewModelState(
    val isLoading: Boolean = false,
    val ingredients: List<String> = emptyList(),
    val errorMessages: List<String> = emptyList()
)

@HiltViewModel
class IngredientsViewModel @Inject constructor(
    private val xRepository: XRepository,
    savedStateHandle: SavedStateHandle,
    private val application: Application
) : ViewModel() {
    val encodedImagePath = savedStateHandle.get<String>("encodedImagePath")
    private val _viewModelState = MutableStateFlow(IngredientsViewModelState())
    val uiState: StateFlow<IngredientsViewModelState> = _viewModelState

    init {
        viewModelScope.launch {
            _viewModelState.update { state -> state.copy(isLoading = true) }
            if (encodedImagePath != null) {
                Uri.decode(encodedImagePath).toUri().let { uri ->
                    val base64Image = readUriContent(application, uri)
                    Log.d("base64Image", "Base64String: $base64Image")
                    if (base64Image != null) {
                        generateIngredients(base64Image)
                    }
                }
            } else {
                _viewModelState.update { state ->
                    state.copy(
                        isLoading = false,
                        ingredients = emptyList(),
                        errorMessages = listOf("No image URI found.")
                    )
                }
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
                _viewModelState.update { state ->
                    state.copy(
                        isLoading = false,
                        errorMessages = listOf("Image file not found.")
                    )
                }
                null
            } catch (e: IOException) {
                e.printStackTrace()
                _viewModelState.update { state ->
                    state.copy(
                        isLoading = false,
                        errorMessages = listOf("Could not read image file.")
                    )
                }
                null
            }
        }
    }

    private suspend fun generateIngredients(base64Image: String) {
        val response = xRepository.postImageUnderstandingIngredients(base64Image)
        if (response.isSuccessful) {
            response.body()?.let { responseBody ->
                if (responseBody.choices.isNotEmpty()) {
                    val ingredientsContentString = responseBody.choices[0].message.content
                    Log.d("contentsString", ingredientsContentString)
                    val ingredientsList = ingredientsContentString.split(",")
                    _viewModelState.update { state ->
                        state.copy(
                            isLoading = false,
                            ingredients = ingredientsList,
                            errorMessages = emptyList()
                        )
                    }
                } else {
                    _viewModelState.update { state ->
                        state.copy(
                            isLoading = false,
                            ingredients = emptyList(),
                            errorMessages = listOf("No ingredient data found in the response.")
                        )
                    }
                }
            }
        } else {
            // Handle API error (e.g., 4xx, 5xx status codes)
            val errorMessage = response.errorBody()?.string() ?: "Unknown error occurred"
            _viewModelState.update { state ->
                state.copy(
                    isLoading = false,
                    ingredients = emptyList(),
                    errorMessages = listOf("Failed to generate ingredients: $errorMessage")
                )
            }
        }
    }
}
