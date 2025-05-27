package com.assistancefin.tpaofinassistance.ui.screens.main;

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assistancefin.tpaofinassistance.data.db.TransactionEntity
import com.assistancefin.tpaofinassistance.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MainVM @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    private val _state = MutableStateFlow<MainState>(MainState.Loading)
    val state: StateFlow<MainState> = _state.asStateFlow()

    init {
        repository.getAllTransaction()                           // Flow<List<TransactionEntity>>
            .map { list -> list.toMetrics() }              // ← агрегуємо
            .onEach { _state.value = MainState.Success(it) }
            .catch { _state.value = MainState.Error(it.message ?: "Unknown") }
            .launchIn(viewModelScope)
    }

    /** розширення, що рахує метрики */
    private fun List<TransactionEntity>.toMetrics(): MainMetrics {
        var income = 0L
        var expense = 0L
        var balance = 0L
        val incomeTx = filter { it.type == "GELIR" }
        val expenseTx = filter { it.type == "HARCAMALAR" }

        for (tx in this) {
            val sum = tx.sum.toLongOrNull() ?: 0L
            when (tx.type) {
                "GELIR" -> {
                    income += sum
                    balance += sum
                }

                "HARCAMALAR" -> {
                    expense += sum
                    balance -= sum
                }
            }
        }
        return MainMetrics(
            balance = balance,
            income = income,
            expense = expense,
            incomeTx = incomeTx,
            expenseTx = expenseTx
        )
    }

}

/** Сумарні числа + дані для графіків */
data class MainMetrics(
    val balance: Long,              // Bakiyeniz
    val income: Long,               // Gelir
    val expense: Long,              // Harcamalar
    val incomeTx: List<TransactionEntity>,
    val expenseTx: List<TransactionEntity>
)

/** Екранні стани */
sealed class MainState {
    data object Loading : MainState()
    data class Success(val metrics: MainMetrics) : MainState()
    data class Error(val msg: String) : MainState()
}