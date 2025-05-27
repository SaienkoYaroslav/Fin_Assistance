package com.assistancefin.tpaofinassistance.ui.screens.history;

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assistancefin.tpaofinassistance.data.db.TransactionEntity
import com.assistancefin.tpaofinassistance.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryVM @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    private val _state = MutableStateFlow<HistoryState>(HistoryState.Loading)
    val state: StateFlow<HistoryState> = _state.asStateFlow()

    private val _effects = Channel<HistoryEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    init {
        repository.getAllTransaction()
            .onEach { list -> _state.value = HistoryState.Success(list) }
            .catch { e -> _state.value = HistoryState.Error(e.message ?: "Unknown error") }
            .launchIn(viewModelScope)
    }

    fun onDelete(item: TransactionEntity) = viewModelScope.launch {
        repository.deleteTransaction(item)
        _effects.send(HistoryEffect.ShowToast("Silindi"))
    }

}

/** Стан екрана */
sealed class HistoryState {
    data object Loading : HistoryState()
    data class Success(val data: List<TransactionEntity>) : HistoryState()
    data class Error(val msg: String) : HistoryState()
}

sealed interface HistoryEffect {
    data class ShowToast(val msg: String) : HistoryEffect
}