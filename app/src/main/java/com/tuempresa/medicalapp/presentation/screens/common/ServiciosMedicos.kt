package com.tuempresa.medicalapp.presentation.screens.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.tuempresa.medicalapp.data.models.Paciente


@Composable
fun ServiciosMedicos(
    nombre: String = "Ricardo Israel",
    paciente: Paciente? = null,
    onBack: () -> Unit = {},
    onRadiografiaClick: () -> Unit = {},
    onCirugiasClick: () -> Unit = {},
    onUltimasConsultasClick: () -> Unit = {}
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
            Spacer(modifier = Modifier.height(24.dp))
            // Barra superior con botón de regreso y título centrado
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
            Text(
                text = "Servicios Médicos",
                color = blue,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 32.sp,
                modifier = Modifier.padding(top = 2.dp, bottom = 2.dp)
            )
            // Nombre del paciente centrado
            Text(
                text = nombre,
                color = blue,
                fontSize = 17.sp,
                modifier = Modifier.padding(top = 20.dp, bottom = 16.dp)
            )
            // Sección: Radiografía
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(top = 12.dp, bottom = 4.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_icon_radiografia),
                    contentDescription = "Icono Radiografía",
                    tint = blue,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(22.dp))
                Text(
                    text = "Radiografía",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 25.sp,
                    color = blue,
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(2.dp)
                    .background(Color(0xFFE0E0E0))
            )
            Spacer(modifier = Modifier.height(10.dp))
            // Imagen radiografía como botón
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(200.dp)
                    .clip(RoundedCornerShape(36.dp))
                    .clickable { 
                        onRadiografiaClick()
                    }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_radiografia),
                    contentDescription = "Radiografía",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(36.dp))
                )
            }
            // Sección: Cirugías
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(top = 18.dp, bottom = 4.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_icon_cirujia),
                    contentDescription = "Icono Cirugía",
                    tint = blue,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(22.dp))
                Text(
                    text = "Cirugías",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 25.sp,
                    color = blue,
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(2.dp)
                    .background(Color(0xFFE0E0E0))
            )
            Spacer(modifier = Modifier.height(10.dp))
            // Imagen cirugías como botón
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(200.dp)
                    .clip(RoundedCornerShape(36.dp))
                    .clickable { onCirugiasClick() }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_cirujia),
                    contentDescription = "Cirugías",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(36.dp))
                )
            }
            // Sección: Últimas Consultas
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(top = 18.dp, bottom = 4.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_icon_consultas),
                    contentDescription = "Icono Consultas",
                    tint = blue,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(22.dp))
                Text(
                    text = "Ultimas Consultas",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 25.sp,
                    color = blue,
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(2.dp)
                    .background(Color(0xFFE0E0E0))
            )
            Spacer(modifier = Modifier.height(10.dp))
            // Imagen últimas consultas como botón
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(200.dp)
                    .clip(RoundedCornerShape(36.dp))
                    .clickable { onUltimasConsultasClick() }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_ultimas_consultas),
                    contentDescription = "Ultimas Consultas",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(36.dp))
                )
            }
        }
    }
}