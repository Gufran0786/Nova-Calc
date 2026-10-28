package com.gufran.novacalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.math.BigDecimal
import java.math.MathContext

private val Bg = Color(0xFF0B1020)
private val Panel = Color(0xFF131D32)
private val Cyan = Color(0xFF51E4FF)
private val Violet = Color(0xFF9B7BFF)
private val Muted = Color(0xFF91A0BD)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { NovaCalcApp() }
    }
}

@Composable
private fun NovaCalcApp() {
    var expr by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("0") }
    var message by remember { mutableStateOf("") }
    val history = remember { mutableStateListOf<Pair<String, String>>() }
    var showHistory by remember { mutableStateOf(false) }

    fun evaluate(save: Boolean = false) {
        try {
            val answer = Parser(expr).parse().stripTrailingZeros().toPlainString()
            result = answer
            message = ""
            if (save && expr.isNotBlank()) {
                history.add(0, expr to answer)
                expr = answer
            }
        } catch (e: Exception) { message = e.message ?: "Invalid expression" }
    }

    MaterialTheme(colorScheme = darkColorScheme(background = Bg, surface = Panel, primary = Cyan)) {
        Column(
            Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF182342), Bg)))
                .statusBarsPadding().navigationBarsPadding().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(44.dp).background(Brush.linearGradient(listOf(Cyan, Violet)), RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) {
                    Text("∑", color = Bg, fontSize = 27.sp, fontWeight = FontWeight.Black)
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("NOVA CALC", fontSize = 18.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    Text("SMART CALCULATOR", fontSize = 10.sp, color = Muted, letterSpacing = 1.5.sp)
                }
                TextButton(onClick = { showHistory = !showHistory }) { Text("◷", color = Cyan, fontSize = 24.sp) }
            }

            Column(Modifier.fillMaxWidth().background(Panel, RoundedCornerShape(24.dp)).padding(20.dp), horizontalAlignment = Alignment.End) {
                Text(if (showHistory) "RECENT CALCULATIONS" else "CALCULATION", color = Muted, fontSize = 11.sp)
                Spacer(Modifier.height(18.dp))
                if (showHistory) {
                    if (history.isEmpty()) Text("No calculations yet", color = Muted)
                    history.take(8).forEach { item ->
                        Text("${item.first} = ${item.second}", modifier = Modifier.fillMaxWidth()
                            .clickable { expr = item.first; result = item.second; showHistory = false }.padding(vertical = 5.dp),
                            color = Color.White, textAlign = TextAlign.End)
                    }
                } else {
                    Text(expr.ifBlank { "Ready to calculate" }, color = Muted, fontSize = 22.sp, textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    Text(result, color = Color.White, fontSize = 42.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth(), maxLines = 1)
                    if (message.isNotBlank()) Text(message, color = Color(0xFFFF9CAE), fontSize = 12.sp)
                }
            }

            val rows = listOf(
                listOf("AC", "±", "%", "÷"),
                listOf("7", "8", "9", "×"),
                listOf("4", "5", "6", "−"),
                listOf("1", "2", "3", "+"),
                listOf("⌫", "0", ".", "=")
            )
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                rows.forEach { row ->
                    Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                        row.forEach { key ->
                            val operator = key in listOf("÷", "×", "−", "+")
                            val equals = key == "="
                            val clear = key == "AC"
                            val color = when { equals -> Cyan; operator -> Color(0xFF193247); clear -> Color(0xFF39263B); else -> Color(0xFF202B47) }
                            Box(Modifier.weight(1f).fillMaxHeight().background(color, RoundedCornerShape(17.dp))
                                .clickable {
                                    message = ""
                                    when (key) {
                                        "AC" -> { expr = ""; result = "0" }
                                        "⌫" -> { expr = expr.dropLast(1); if (expr.isBlank()) result = "0" else evaluate() }
                                        "=" -> evaluate(true)
                                        "±" -> { if (expr.isNotBlank()) { expr = "-($expr)"; evaluate() } }
                                        "%" -> { expr += "/100"; evaluate() }
                                        "÷" -> expr += "/"
                                        "×" -> expr += "*"
                                        "−" -> expr += "-"
                                        else -> expr += key
                                    }
                                    if (key !in listOf("AC", "⌫", "=", "±", "%")) evaluate()
                                }, contentAlignment = Alignment.Center) {
                                Text(key, color = when { equals -> Bg; operator -> Cyan; clear -> Color(0xFFFF9CAE); else -> Color.White },
                                    fontSize = if (equals) 27.sp else 21.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Text("⌗ Calculator", color = if (!showHistory) Cyan else Muted, fontSize = 12.sp)
                TextButton(onClick = { showHistory = !showHistory }) { Text("◷ History", color = if (showHistory) Cyan else Muted, fontSize = 12.sp) }
                Text("⇄ Converter", color = Muted, fontSize = 12.sp)
            }
        }
    }
}

private class Parser(private val input: String) {
    private val s = input.replace("×", "*").replace("÷", "/").replace("−", "-").replace(" ", "")
    private var p = 0
    fun parse(): BigDecimal {
        if (s.isBlank()) return BigDecimal.ZERO
        val v = sum()
        if (p != s.length) throw IllegalArgumentException("Invalid expression")
        return v
    }
    private fun sum(): BigDecimal {
        var v = product()
        while (p < s.length && (s[p] == '+' || s[p] == '-')) {
            val op = s[p++]; val b = product()
            v = if (op == '+') v.add(b) else v.subtract(b)
        }
        return v
    }
    private fun product(): BigDecimal {
        var v = atom()
        while (p < s.length && (s[p] == '*' || s[p] == '/')) {
            val op = s[p++]; val b = atom()
            v = if (op == '*') v.multiply(b) else {
                if (b.compareTo(BigDecimal.ZERO) == 0) throw ArithmeticException("Cannot divide by zero")
                v.divide(b, MathContext.DECIMAL64)
            }
        }
        return v
    }
    private fun atom(): BigDecimal {
        if (p >= s.length) throw IllegalArgumentException("Incomplete expression")
        if (s[p] == '+') { p++; return atom() }
        if (s[p] == '-') { p++; return atom().negate() }
        if (s[p] == '(') {
            p++; val v = sum()
            if (p >= s.length || s[p] != ')') throw IllegalArgumentException("Missing )")
            p++; return v
        }
        val start = p
        var dot = false
        while (p < s.length && (s[p].isDigit() || (s[p] == '.' && !dot))) {
            if (s[p] == '.') dot = true
            p++
        }
        if (start == p) throw IllegalArgumentException("Enter a valid number")
        return s.substring(start, p).toBigDecimal()
    }
}
