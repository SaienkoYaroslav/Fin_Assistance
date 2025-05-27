package com.assistancefin.tpaofinassistance.ui.screens.adding

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.assistancefin.tpaofinassistance.R
import com.assistancefin.tpaofinassistance.nav.LocalNavController
import com.assistancefin.tpaofinassistance.ui.components.BaseTextField
import com.assistancefin.tpaofinassistance.ui.components.CardContainer
import com.assistancefin.tpaofinassistance.ui.theme.LightGrey
import com.assistancefin.tpaofinassistance.ui.theme.RedBg
import kotlinx.coroutines.launch

@Composable
fun AddingScreen() {

    val context = LocalContext.current
    val viewModel = hiltViewModel<AddingVM>()
    val navHostController = LocalNavController.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { eff ->
            when (eff) {
                is AddingUiEffect.ShowToast ->
                    Toast.makeText(context, eff.resId, Toast.LENGTH_SHORT).show()
                is AddingUiEffect.ShowToastStr ->
                    Toast.makeText(context, eff.text, Toast.LENGTH_SHORT).show()
                AddingUiEffect.NavigateBack ->
                    navHostController.popBackStack()
            }
        }
    }

    AddingContent(
        uiState = uiState,
        onDescriptionChanged = { viewModel.onEvent(AddingUiEvent.DescriptionChanged(it)) },
        onSumChanged = { viewModel.onEvent(AddingUiEvent.SumChanged(it)) },
        onTabChanged = { viewModel.onEvent(AddingUiEvent.TabChanged(it)) },
        onCategoryChanged = { viewModel.onEvent(AddingUiEvent.CategoryChanged(it)) },
        onSubmit = { viewModel.onEvent(AddingUiEvent.Submit) }

    )

}

@Composable
fun AddingContent(
    uiState: AddingUiState,
    onDescriptionChanged: (String) -> Unit,
    onSumChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onTabChanged: (TxType) -> Unit,
    onCategoryChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val systemBottomPadding = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()
    val pagerState = rememberPagerState(
        initialPage = if (uiState.selectedType == TxType.HARCAMALAR) 0 else 1,
        pageCount = { 2 }
    )
    val scope = rememberCoroutineScope()

    /** ---- Синхронізація swipe → Tab ---- */
    LaunchedEffect(pagerState.currentPage) {
        val newType = if (pagerState.currentPage == 0) TxType.HARCAMALAR else TxType.GELIR
        if (newType != uiState.selectedType) onTabChanged(newType)
    }

    Column {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = RedBg,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                    color = Color.White,          // ← потрібний колір
                    height = 3.dp                 // опційно: товщина лінії
                )
            }
        ) {
            listOf(TxType.HARCAMALAR, TxType.GELIR).forEachIndexed { index, type ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        scope.launch { pagerState.animateScrollToPage(index) }
                        onTabChanged(type)                       // повідомляємо VM
                    },
                    selectedContentColor = Color.White,
                    unselectedContentColor = LightGrey,
                    text = {
                        Text(
                            if (type == TxType.HARCAMALAR) stringResource(R.string.expense) else stringResource(
                                R.string.income
                            ),
                            fontSize = 18.sp,
                            fontWeight = if (pagerState.currentPage == index) FontWeight.Medium else FontWeight.Normal
                        )
                    }
                )
            }
        }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            pageSpacing = 0.dp,
            key = { it }          // допомагає Compose запам’ятовувати стан
        ) { _ /*page*/ ->

            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(10.dp))
                Spacer(Modifier.weight(1f))
                CardContainer {
                    BaseTextField(
                        label = stringResource(R.string.description),
                        value = uiState.description,
                        onValueChange = onDescriptionChanged,
                        isError = uiState.descriptionError,
                        errorMessage = stringResource(R.string.field_cant_be_empty)
                    )
                }
                Spacer(Modifier.height(10.dp))
                Spacer(Modifier.weight(1f))
                CardContainer {
                    BaseTextField(
                        label = stringResource(R.string.sum_name),
                        value = uiState.sum,
                        maxChar = 7,
                        showSuffix = true,
                        onValueChange = onSumChanged,
                        isError = uiState.sumError,
                        errorMessage = stringResource(R.string.field_cant_be_empty),
                        keyboardType = KeyboardType.Number
                    )
                }
                Spacer(Modifier.height(10.dp))
                Spacer(Modifier.weight(1f))
                Text(
                    text = stringResource(R.string.category),
                    color = Color.Black,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
                CategoriesSheet(
                    selectedItem = uiState.categoryId,
                    onItemSelected = onCategoryChanged
                )
                Spacer(Modifier.weight(1f))
                Button(
                    onClick = onSubmit,
                    enabled = !uiState.isSubmitting,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RedBg,
                        disabledContainerColor = RedBg.copy(alpha = 0.6f),
                        contentColor = Color.White,
                        disabledContentColor = Color.White
                    ),
                    shape = RoundedCornerShape(5.dp),
                    elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 5.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp)
                ) {
                    Text(
                        modifier = Modifier.padding(5.dp),
                        text = stringResource(R.string.add),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(Modifier.weight(1f))
                Spacer(Modifier.height(systemBottomPadding + 60.dp))
            }
        }
    }

}

@Composable
fun CategoriesSheet(
    modifier: Modifier = Modifier,
    selectedItem: Int?,
    onItemSelected: (Int) -> Unit
) {
    Column(modifier = modifier) {
        listOf(
            R.drawable.car_ic,
            R.drawable.food_ic,
            R.drawable.gift_ic,
            R.drawable.health_ic,
            R.drawable.clothes_ic,
            R.drawable.donation_ic,
            R.drawable.beauty_ic,
            R.drawable.home_ic,
        ).chunked(4).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                rowItems.forEach { category ->
                    CategoryItem(
                        imgId = category,
                        isSelected = category == selectedItem,
                        onClick = { onItemSelected(category) },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Якщо елементів у рядку менше 4 — додаємо пусті блоки
                repeat(4 - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}


@Composable
fun CategoryItem(
    modifier: Modifier = Modifier,
    imgId: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {

    val borderColor = if (isSelected) RedBg else Color.Transparent

    Column(
        modifier = modifier
            .padding(1.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(2.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier.size(70.dp),
            painter = painterResource(imgId),
            contentDescription = null
        )
    }
}