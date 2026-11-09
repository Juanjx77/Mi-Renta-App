package com.mirenta.util

import java.util.Locale

/** 1234567 -> "1.234.567" */
fun miles(n: Long): String = String.format(Locale.US, "%,d", n).replace(',', '.')

/** 1234567.0 -> "$ 1.234.567" */
fun fm(n: Double): String = "$ " + miles(Math.round(n))

fun dec(n: Double, decimales: Int): String = String.format(Locale.US, "%.${decimales}f", n)
