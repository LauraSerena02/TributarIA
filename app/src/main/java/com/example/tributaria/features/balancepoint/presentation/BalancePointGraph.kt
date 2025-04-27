package com.example.tributaria.features.balancepoint.presentation





import android.graphics.Paint
import androidx.compose.foundation.Canvas

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb


@Composable
fun BalancePointGraph(
    puntoEquilibrio: Float,
    costoFijo: Float,
    costoVariable: Float,
    precioUnitario: Float,
    modifier: Modifier = Modifier
) {
    // Cálculos
    val ventas = (0..(puntoEquilibrio * 2).toInt()).map { it.toFloat() }
    val ingresos = ventas.map { it * precioUnitario }
    val costosTotales = ventas.map { costoFijo + (it * costoVariable) }

    // Definir los valores para graficar
    val graphPointsIngresos = ventas.mapIndexed { index, value -> Offset(value, ingresos[index]) }
    val graphPointsCostos = ventas.mapIndexed { index, value -> Offset(value, costosTotales[index]) }

    // Dibuja el gráfico en un Canvas
    Canvas(modifier = modifier) {
        // Definir los colores para las líneas de ingresos y costos
        val ingresosPaint = SolidColor(Color(0xFF1E40AF)) // Azul
        val costosPaint = SolidColor(Color.Red) // Rojo

        // Calcular los rangos para normalizar los puntos en el canvas
        val maxX = ventas.maxOrNull() ?: 0f
        val maxY = maxOf(ingresos.maxOrNull() ?: 0f, costosTotales.maxOrNull() ?: 0f)

        // Margen para los ejes
        val margin = 50f

        // Dibujar el plano cartesiano (Ejes X y Y) extendidos
        val ejeXColor = SolidColor(Color.Black)
        val ejeYColor = SolidColor(Color.Black)

        // Eje Y (vertical)
        drawLine(
            brush = ejeYColor,
            start = Offset(margin, size.height - margin), // Comienza con margen
            end = Offset(margin, margin), // Extiende el eje Y hasta el margen superior
            strokeWidth = 2f
        )

        // Eje X (horizontal)
        drawLine(
            brush = ejeXColor,
            start = Offset(margin, size.height - margin), // Comienza con margen
            end = Offset(size.width - margin, size.height - margin), // Extiende el eje X a la anchura con margen
            strokeWidth = 2f
        )

        // Marcas y números en el eje X (horizontal)
        val stepX = (maxX / 5).toInt() // Calcula el intervalo para las marcas
        for (i in 0..5) {
            val x = margin + (i * (size.width - margin * 2) / 5) // Distribuye las marcas en el eje X
            drawLine(
                brush = ejeXColor,
                start = Offset(x, size.height - margin), // Marca en el eje X
                end = Offset(x, size.height - margin + 10f), // Longitud de la marca
                strokeWidth = 2f
            )
            // Dibujar el número en el eje X
            drawContext.canvas.nativeCanvas.drawText(
                "%.2f".format(i * stepX.toFloat()),
                x - 10f,
                size.height - margin + 30f,
                Paint().apply {
                    color = android.graphics.Color.BLACK
                    textSize = 20f
                    textAlign = Paint.Align.CENTER
                }
            )
        }

        // Marcas y números en el eje Y (vertical)
        val stepY = (maxY / 5).toInt() // Calcula el intervalo para las marcas
        for (i in 0..5) {
            val y = size.height - margin - (i * (size.height - margin * 2) / 5) // Distribuye las marcas en el eje Y
            drawLine(
                brush = ejeYColor,
                start = Offset(margin - 10f, y), // Marca en el eje Y
                end = Offset(margin, y), // Longitud de la marca
                strokeWidth = 2f
            )
            // Dibujar el número en el eje Y
            drawContext.canvas.nativeCanvas.drawText(
                "%.2f".format(i * stepY.toFloat()),
                margin - 30f,
                y + 5f,
                Paint().apply {
                    color = android.graphics.Color.BLACK
                    textSize = 20f
                    textAlign = Paint.Align.RIGHT
                }
            )
        }

        // Dibujar las líneas de ingresos
        for (i in 1 until graphPointsIngresos.size) {
            val start = graphPointsIngresos[i - 1]
            val end = graphPointsIngresos[i]
            drawLine(
                brush = ingresosPaint,
                start = Offset(start.x / maxX * (size.width - margin * 2), size.height - margin - start.y / maxY * (size.height - margin * 2)),
                end = Offset(end.x / maxX * (size.width - margin * 2), size.height - margin - end.y / maxY * (size.height - margin * 2)),
                strokeWidth = 3f
            )
        }

        // Dibujar las líneas de costos
        for (i in 1 until graphPointsCostos.size) {
            val start = graphPointsCostos[i - 1]
            val end = graphPointsCostos[i]
            drawLine(
                brush = costosPaint,
                start = Offset(start.x / maxX * (size.width - margin * 2), size.height - margin - start.y / maxY * (size.height - margin * 2)),
                end = Offset(end.x / maxX * (size.width - margin * 2), size.height - margin - end.y / maxY * (size.height - margin * 2)),
                strokeWidth = 3f
            )
        }

        // Dibujar el valor de Punto de Equilibrio
        val puntoEquilibrioX = puntoEquilibrio / maxX * (size.width - margin * 2)
        val puntoEquilibrioY = (puntoEquilibrio * precioUnitario) / maxY * (size.height - margin * 2)
        drawContext.canvas.nativeCanvas.drawText(
            "PE: %.2f".format(puntoEquilibrio),
            puntoEquilibrioX + margin,
            size.height - puntoEquilibrioY - margin - 10f,
            Paint().apply {
                color = android.graphics.Color.BLACK // Color para el texto
                textSize = 30f
                textAlign = Paint.Align.LEFT
            }
        )

        // Mostrar los valores de los inputs con colores específicos (para el texto)
        drawContext.canvas.nativeCanvas.drawText(
            "Costo Fijo: %.2f".format(costoFijo),
            400f,
            size.height - 140f,
            Paint().apply {
                color = Color.Red.toArgb() // Rojo para Costo Fijo (correspondiente a la línea de costos)
                textSize = 30f
                textAlign = Paint.Align.LEFT
            }
        )

        drawContext.canvas.nativeCanvas.drawText(
            "Costo Variable: %.2f".format(costoVariable),
            400f,
            size.height - 170f,
            Paint().apply {
                color = Color.Red.toArgb() // Rojo para Costo Variable (correspondiente a la línea de costos)
                textSize = 30f
                textAlign = Paint.Align.LEFT
            }
        )

        drawContext.canvas.nativeCanvas.drawText(
            "Precio Unitario: %.2f".format(precioUnitario),
            400f,
            size.height - 200f,
            Paint().apply {
                color = Color(0xFF1E40AF).toArgb() // Azul para Precio Unitario (correspondiente a la línea de ingresos)
                textSize = 30f
                textAlign = Paint.Align.LEFT
            }
        )

        // *** Nombres de los Ejes ***
        // Nombre del eje X
        drawContext.canvas.nativeCanvas.drawText(
            "Ingresos",
            size.width / 2,
            size.height - 10f,
            Paint().apply {
                color = android.graphics.Color.BLACK
                textSize = 30f
                textAlign = Paint.Align.CENTER
            }
        )

        // Nombre del eje Y (requiere rotación)
        drawContext.canvas.nativeCanvas.save()
        drawContext.canvas.nativeCanvas.rotate(-90f, 80f, size.height / 2)
        drawContext.canvas.nativeCanvas.drawText(
            "Costos",
            150f,
            size.height / 2,
            Paint().apply {
                color = android.graphics.Color.BLACK
                textSize = 30f
                textAlign = Paint.Align.CENTER
            }
        )
        drawContext.canvas.nativeCanvas.restore()
    }
}

