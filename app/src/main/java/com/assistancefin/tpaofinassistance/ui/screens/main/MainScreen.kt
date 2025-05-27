package com.assistancefin.tpaofinassistance.ui.screens.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aay.compose.barChart.BarChart
import com.aay.compose.barChart.model.BarParameters
import com.aay.compose.baseComponents.model.LegendPosition
import com.assistancefin.tpaofinassistance.data.db.TransactionEntity
import com.assistancefin.tpaofinassistance.ui.components.CardContainer
import com.assistancefin.tpaofinassistance.ui.screens.history.ErrorView
import com.assistancefin.tpaofinassistance.ui.screens.history.LoadingView
import com.assistancefin.tpaofinassistance.ui.theme.Green
import com.assistancefin.tpaofinassistance.ui.theme.Red
import com.assistancefin.tpaofinassistance.utils.formatNumber
import com.assistancefin.tpaofinassistance.utils.toShortDate

@Composable
fun MainScreen() {

    val context = LocalContext.current
    val viewModel = hiltViewModel<MainVM>()

    val uiState by viewModel.state.collectAsStateWithLifecycle()

    when (uiState) {
        MainState.Loading -> LoadingView()
        is MainState.Error -> ErrorView(msg = (uiState as MainState.Error).msg)
        is MainState.Success -> {
            val m = (uiState as MainState.Success).metrics
            MainContent(
                balance = m.balance,
                income = m.income,
                expense = m.expense,
                incomeTx = m.incomeTx,
                expenseTx = m.expenseTx
            )
        }
    }

}

@Composable
fun MainContent(
    balance: Long,
    income: Long,
    expense: Long,
    incomeTx: List<TransactionEntity>,
    expenseTx: List<TransactionEntity>,
    modifier: Modifier = Modifier
) {
    val systemBottomPadding = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MetricSheet(balance, income, expense)
        TransactionsBarChart(
            modifier = Modifier.weight(1f),
            transactions = incomeTx,
            barColor = Green,
            title = "Gelir"
        )
        TransactionsBarChart(
            modifier = Modifier.weight(1f),
            transactions = expenseTx,
            barColor = Red,
            title = "Harcamalar"
        )
        Spacer(Modifier.height(systemBottomPadding + 60.dp))
    }
}

@Composable
fun TransactionsBarChart(
    transactions: List<TransactionEntity>,
    barColor: Color,
    title: String,
    modifier: Modifier = Modifier
) {

    val bars by remember(transactions) {
        mutableStateOf(
            transactions.sortedBy { it.date }.map {
                it.sum.toDoubleOrNull() ?: 0.0
            }
        )
    }
    val labels by remember(transactions) {
        mutableStateOf(
            transactions.sortedBy { it.date }.map { it.date.toShortDate() }
        )
    }

    CardContainer(modifier) {
        BarChart(
            legendPosition = LegendPosition.TOP,
            chartParameters =
                listOf(BarParameters(title, bars, barColor)),
            xAxisData = labels,
            gridColor = Color.DarkGray,
            showXAxis = false,
            isShowGrid = true,
            showGridWithSpacer = true,
            animateChart = true,
            yAxisStyle = TextStyle(fontSize = 10.sp, color = Color.DarkGray),
            xAxisStyle = TextStyle(fontSize = 10.sp, color = Color.Transparent)
        )
    }

}

@Composable
fun MetricSheet(
    balance: Long,
    income: Long,
    expense: Long,
    modifier: Modifier = Modifier,
) {

    CardContainer(
        modifier = modifier
    ) {
        RowTextTitleValue(
            textColor = Color.Black,
            title = "Bakiyeniz",
            value = balance
        )
        Spacer(Modifier.height(5.dp))
        RowTextTitleValue(
            textColor = Green,
            title = "Gelir",
            value = income
        )
        Spacer(Modifier.height(5.dp))
        RowTextTitleValue(
            textColor = Red,
            title = "Harcamalar",
            value = expense
        )
    }
}

@Composable
fun RowTextTitleValue(
    modifier: Modifier = Modifier,
    title: String,
    value: Long,
    textColor: Color
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            color = textColor,
            fontWeight = FontWeight.Medium,
            maxLines = 1
        )
        Spacer(Modifier.width(5.dp))
        Text(
            text = "${formatNumber(value)} ₺",
            color = textColor,
            fontWeight = FontWeight.Medium,
            maxLines = 1
        )
    }
}