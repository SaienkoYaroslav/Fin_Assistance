package com.assistancefin.tpaofinassistance.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

fun ViewModel.updateText(newText: String, oldText: MutableStateFlow<String>) {
    oldText.value = newText
}

fun filterIntegerInput(input: String): String {
    // Беремо лише цифри
    val digits = input.filter { it.isDigit() }

    val result = StringBuilder()
    for (c in digits) {
        // Якщо результат ще порожній (result.isEmpty()) і користувач ввів '0',
        // то пропускаємо цю '0'.
        // Інакше додаємо в result
        if (result.isEmpty() && c == '0') {
            // skip
        } else {
            result.append(c)
        }
    }

    // Якщо вийшло більше 7 цифр, обрізаємо
    if (result.length > 8) {
        result.setLength(8)
    }

    return result.toString()
}

fun formatNumber(number: Long): String {
    val symbols = DecimalFormatSymbols(Locale.getDefault()).apply {
        groupingSeparator = ' ' // Використовуємо пробіл як роздільник тисяч
    }
    val formatter = DecimalFormat("#,###", symbols)
    return formatter.format(number)
}

fun formatDouble(number: Double): String {
    val symbols = DecimalFormatSymbols(Locale.getDefault()).apply {
        groupingSeparator = ' ' // Використовуємо пробіл як роздільник тисяч
    }
    val formatter = DecimalFormat("#,###.#", symbols)
    return formatter.format(number)
}

fun isInternetAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val network = connectivityManager.activeNetwork ?: return false
    val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
    return when {
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        else -> false
    }
}

private val trLocale = Locale("tr", "TR")
private val uiFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", trLocale)

/** "2025-05-12" → "12 mayıs 2025"  */
fun formatDateTr(raw: String): String =
    runCatching {
        LocalDate.parse(raw)           // очікуємо ISO-8601
            .format(uiFormatter)
            .lowercase(trLocale)       // маленька літера «m»
    }.getOrElse { raw }


private val shortTurkishDate =
    DateTimeFormatter.ofPattern("dd.MM.yy")     // 15.01.25

fun String.toShortDate(): String =
    runCatching { LocalDate.parse(this).format(shortTurkishDate) }
        .getOrElse { this }
