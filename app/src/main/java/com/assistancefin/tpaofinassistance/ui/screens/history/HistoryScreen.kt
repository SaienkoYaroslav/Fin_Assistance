package com.assistancefin.tpaofinassistance.ui.screens.history

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.assistancefin.tpaofinassistance.data.db.TransactionEntity
import com.assistancefin.tpaofinassistance.ui.components.BaseAlertDialog
import com.assistancefin.tpaofinassistance.ui.components.CardContainer
import com.assistancefin.tpaofinassistance.ui.theme.Green
import com.assistancefin.tpaofinassistance.ui.theme.LightGrey
import com.assistancefin.tpaofinassistance.ui.theme.Red
import com.assistancefin.tpaofinassistance.ui.theme.RedBg
import com.assistancefin.tpaofinassistance.utils.formatDateTr
import com.assistancefin.tpaofinassistance.utils.formatNumber

@Composable
fun HistoryScreen() {

    val viewModel = hiltViewModel<HistoryVM>()
    val context = LocalContext.current
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { eff ->
            when (eff) {
                is HistoryEffect.ShowToast ->
                    Toast.makeText(context, eff.msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    when (uiState) {
        HistoryState.Loading -> LoadingView()

        is HistoryState.Error -> ErrorView(
            msg = (uiState as HistoryState.Error).msg,
        )

        is HistoryState.Success ->
            HistoryContent(
                list = (uiState as HistoryState.Success).data,
                onDelete = viewModel::onDelete
            )
    }

}

@Composable
fun LoadingView(modifier: Modifier = Modifier) {
    Box(
        modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) { CircularProgressIndicator(color = RedBg) }
}

@Composable
fun ErrorView(
    modifier: Modifier = Modifier,
    msg: String,
) {
    val systemBottomPadding = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 60.dp + systemBottomPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        CardContainer {
            Text(
                text = msg,
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                overflow = TextOverflow.Ellipsis
            )
        }


    }
}

@Composable
fun HistoryContent(
    list: List<TransactionEntity>,
    onDelete: (TransactionEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val systemBottomPadding = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()
    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
        ) {

            if (list.isNotEmpty()) {
                items(
                    items = list,
                    key = { it.id ?: it.hashCode() }
                ) { item ->
                    HistoryItem(
                        item = item,
                        onDelete = onDelete
                    )
                }
                item {
                    Spacer(Modifier.height(systemBottomPadding + 70.dp))
                }
            } else {
                item {
                    Text(
                        modifier = Modifier.padding(10.dp),
                        text = "Liste boş...",
                        color = LightGrey,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

            }


        }
    }
}

@Composable
fun HistoryItem(
    modifier: Modifier = Modifier,
    item: TransactionEntity,
    onDelete: (TransactionEntity) -> Unit
) {
    val displayDate by remember(item.date) {
        derivedStateOf { formatDateTr(item.date) }
    }
    var expanded by remember { mutableStateOf(false) }
    var openDeleteDialog by remember { mutableStateOf(false) }

    val sumColor = if (item.type == "GELIR") Green else Red

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 5.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 5.dp),
        shape = RoundedCornerShape(5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                modifier = Modifier.size(50.dp),
                painter = painterResource(item.categoryImgId),
                contentDescription = null
            )
            Spacer(Modifier.width(5.dp))
            Column {
                Text(
                    text = displayDate,
                    color = LightGrey,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = item.description,
                    color = Color.Black,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(
                Modifier
                    .width(5.dp)
                    .weight(1f)
            )

            Text(
                text = "${formatNumber(item.sum.toLong())} ₺",
                color = sumColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.width(3.dp))
            IconButton(
                modifier = Modifier
                    .size(25.dp),
                onClick = {
                    expanded = true
                }
            ) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null,
                    tint = Color.Black
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    containerColor = Color.White
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "Sil",
                                color = Color.Red
                            )
                        },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                tint = Color.Red
                            )
                        },
                        onClick = {
                            expanded = false
                            openDeleteDialog = true
                        }
                    )
                }
            }
        }
    }

    if (openDeleteDialog) {
        BaseAlertDialog(
            onDismissRequest = {
                openDeleteDialog = false
            },
            confirmButtonClick = {
                openDeleteDialog = false
                onDelete(item)
            }
        )
    }

}

