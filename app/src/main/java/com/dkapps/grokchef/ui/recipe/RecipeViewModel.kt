package com.dkapps.grokchef.ui.recipe

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dkapps.grokchef.data.XRepository
import com.dkapps.grokchef.ui.ingredients.IngredientsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject

sealed interface RecipeUiState {
    // Display loading state with text.
    data class Loading(val loadingMessage: String) : RecipeUiState

    // Display recipe on screen.
    data class Success(val recipe: String) : RecipeUiState

    // Display error message on screen.
    data class Error(val errorMessage: String) : RecipeUiState
}

@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val xRepository: XRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    companion object {
        // Define wait duration before displaying loading state update.
        const val LONG_REQUEST_WARNING_MILLIS = 10000L // 10S
    }

    val ingredientsList = savedStateHandle.get<String>("ingredientsList") ?: ""

    private val _uiState = MutableStateFlow<RecipeUiState>(
        RecipeUiState.Loading(
            "Generating recipe..."
        )
    )
    val uiState: StateFlow<RecipeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            if (ingredientsList.isNotEmpty()) {
                generateRecipe(ingredientsList)
            } else {
                _uiState.update { RecipeUiState.Error("Ingredients list is empty.") }
            }
        }
    }

    private suspend fun generateRecipe(ingredientsList: String) {
        // Notify UI that request is taking longer than expected.
        val longRunningJob = viewModelScope.launch {
            delay(LONG_REQUEST_WARNING_MILLIS)
            _uiState.update {
                if (it is RecipeUiState.Loading) {
                    RecipeUiState.Loading("Looking up additional cookbooks...")
                } else {
                    it
                }
            }
        }
        try {
            val response = xRepository.postRecipeLiveSearchChat(ingredientsList)
            if (response.isSuccessful) {
                response.body()?.let { responseBody ->
                    if (responseBody.choices.isNotEmpty()) {
                        val recipe = responseBody.choices[0].message.content
                        Log.d("RecipeViewModel", "Recipe: $recipe")
                        _uiState.update { RecipeUiState.Success(recipe) }
                    } else {
                        updateUiStateToError("Could not find a recipe with these ingredients.")
                    }
                }
            } else {
                // Handle API error returned by the server (e.g., 4xx, 5xx status codes).
                val errorMessage =
                    response.errorBody()?.string() ?: "Unknown error occurred from server."
                Log.e("RecipeViewModel", "API Error: ${response.code()} - $errorMessage")
                updateUiStateToError("Server failed to generate recipe.")
            }
        } catch (e: SocketTimeoutException) {
            // Network request timed out.
            e.printStackTrace()
            updateUiStateToError("Request timed out by server when generating recipe.")
        } catch (e: IOException) {
            // Network issues (e.g., no internet, host unreachable).
            e.printStackTrace()
            updateUiStateToError("Network error occurred when generating recipe.")
        } catch (e: Exception) {
            // Catch any other unexpected network exceptions.
            e.printStackTrace()
            updateUiStateToError("An unknown error occurred while generating recipe.")
        } finally {
            longRunningJob.cancel()
        }
    }

    private fun updateUiStateToError(errorMessage: String) {
        _uiState.update { RecipeUiState.Error(errorMessage) }
    }
}