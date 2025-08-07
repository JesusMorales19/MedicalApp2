package com.tuempresa.medicalapp.presentation.screens.doctor

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tuempresa.medicalapp.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale



@Composable
fun HistorialRadiografia (
    nombre: String = "Ricardo Israel",
    onBack: () -> Unit = {},
    onToraxClick: () -> Unit = {},
    onExtremidadesClick: () -> Unit = {},
    onColumnaClick: () -> Unit = {}
) {
    val blue = Color(0xFF183A6D)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(22.dp))
            //Boton de regreso
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(50.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Regresar",
                        tint = blue,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
            }
            //Titulo
            Text(
                text = "Radiografías",
                color = blue,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 32.sp,
                modifier = Modifier.padding(top = 2.dp, bottom = 2.dp)
            )
            // Radiografía de Tórax
            Text(
                text = "Tórax",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                color = blue,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(top = 12.dp, bottom = 4.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(2.dp)
                    .background(Color(0xFFE0E0E0))
            )
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .clip(RoundedCornerShape(36.dp))
                    .background(Color(0xFFE0E0E0))
            ) {
                Column {
                    Image(
                        painter = painterResource(id = R.drawable.ic_torax),
                        contentDescription = "Radiografía de Tórax",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp))
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                            .background(Color(0xFF183A6D))
                            .clickable { onToraxClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Proyectar",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(28.dp))

            // Radiografía de Extremidades
            Text(
                text = "Extremidades",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                color = blue,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(top = 0.dp, bottom = 4.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(2.dp)
                    .background(Color(0xFFE0E0E0))
            )
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .clip(RoundedCornerShape(36.dp))
                    .background(Color(0xFFE0E0E0))
            ) {
                Column {
                    Image(
                        painter = painterResource(id = R.drawable.ic_extremidades),
                        contentDescription = "Radiografía de Extremidades",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp))
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                            .background(Color(0xFF183A6D))
                            .clickable { onExtremidadesClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Proyectar",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(28.dp))

            // Radiografía de Columna
            Text(
                text = "Columna",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                color = blue,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(top = 0.dp, bottom = 4.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(2.dp)
                    .background(Color(0xFFE0E0E0))
            )
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .clip(RoundedCornerShape(36.dp))
                    .background(Color(0xFFE0E0E0))
            ) {
                Column {
                    Image(
                        painter = painterResource(id = R.drawable.ic_columna),
                        contentDescription = "Radiografía de Columna",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp))
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                            .background(Color(0xFF183A6D))
                            .clickable { onColumnaClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Proyectar",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}