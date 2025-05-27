package com.assistancefin.tpaofinassistance.ui.screens.adding;

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assistancefin.tpaofinassistance.R
import com.assistancefin.tpaofinassistance.data.db.TransactionEntity
import com.assistancefin.tpaofinassistance.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AddingVM @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    /* ---------- STATE ---------- */
    private val _uiState = MutableStateFlow(AddingUiState())
    val uiState: StateFlow<AddingUiState> = _uiState.asStateFlow()

    /* --------- EFFECTS --------- */
    private val _effects = Channel<AddingUiEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    /* ---------- EVENTS --------- */
    fun onEvent(ev: AddingUiEvent) {
        when (ev) {
            is AddingUiEvent.DescriptionChanged -> {
                val txt = ev.text
                _uiState.update {
                    it.copy(
                        description = txt,
                        descriptionError = txt.isBlank(),
                        canSubmit = txt.isNotBlank()
                    )
                }
            }

            is AddingUiEvent.SumChanged -> {
                val t = ev.text
                _uiState.update {
                    it.copy(
                        sum = t,
                        sumError = t.isBlank()
                    ).recalcSubmit()
                }
            }

            is AddingUiEvent.CategoryChanged ->
                _uiState.update { it.copy(categoryId = ev.id) }

            is AddingUiEvent.TabChanged -> _uiState.update {
                it.copy(selectedType = ev.type)
            }

            AddingUiEvent.Submit -> submit()
        }
    }

    private fun AddingUiState.recalcSubmit(): AddingUiState =
        copy(canSubmit = description.isNotBlank() && sum.isNotBlank())


    /* ---------- SUBMIT ---------- */
    private fun submit() = viewModelScope.launch {
        val s = _uiState.value
        if (s.isSubmitting) return@launch

        // базова валідація
        val descOk = s.description.trim().isNotEmpty()
        val sumOk = s.sum.trim().isNotEmpty()

        if (!descOk || !sumOk) {
            _uiState.update {
                it.copy(
                    descriptionError = !descOk,
                    sumError = !sumOk
                )
            }
            return@launch
        }
        // блокуємо кнопку
        _uiState.update { it.copy(isSubmitting = true) }

        /* зберігаємо в БД */
        try {
            val entity = TransactionEntity(
                description = s.description.trim(),
                sum = s.sum.trim(),
                date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                type = s.selectedType.name,
                categoryImgId = s.categoryId
            )
            repository.insertTransaction(entity)

            _effects.send(AddingUiEffect.ShowToast(R.string.adding_succes))
            _effects.send(AddingUiEffect.NavigateBack)
        } catch (e: Exception) {
            _uiState.update { it.copy(isSubmitting = false) }   // розблокуємо
            _effects.send(AddingUiEffect.ShowToastStr("Error: ${e.localizedMessage}"))
        }
    }


}

enum class TxType { HARCAMALAR, GELIR }

/** Стан екрана */
data class AddingUiState(
    val description: String = "",
    val sum: String = "",
    val descriptionError: Boolean = false,
    val canSubmit: Boolean = false,
    val sumError: Boolean = false,
    val selectedType: TxType = TxType.HARCAMALAR,
    val categoryId: Int = R.drawable.food_ic,
    val isSubmitting: Boolean = false
)

/** Події від UI */
sealed interface AddingUiEvent {
    data class DescriptionChanged(val text: String) : AddingUiEvent
    data class SumChanged(val text: String) : AddingUiEvent
    data class TabChanged(val type: TxType) : AddingUiEvent
    data class CategoryChanged(val id: Int) : AddingUiEvent
    object Submit : AddingUiEvent
}

/** Побічні ефекти (одноразові події) */
sealed interface AddingUiEffect {
    data class ShowToast(@StringRes val resId: Int) : AddingUiEffect
    data class ShowToastStr(val text: String) : AddingUiEffect
    object NavigateBack : AddingUiEffect
}