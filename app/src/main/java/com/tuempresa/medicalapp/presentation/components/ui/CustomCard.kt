package com.tuempresa.medicalapp.presentation.components.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tuempresa.medicalapp.core.constants.AppConstants

@Composable
fun CustomCard(
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    elevation: Int = AppConstants.CARD_ELEVATION,
    backgroundColor: Color = MaterialTheme.colorScheme.surface
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(AppConstants.DEFAULT_SPACING.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        content()
    }
}

@Composable
fun PatientCard(
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    CustomCard(
        content = content,
        modifier = modifier
    )
}

@Composable
fun ConsultationCard(
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    CustomCard(
        content = content,
        modifier = modifier
    )
}

@Composable
fun SurgeryCard(
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    CustomCard(
        content = content,
        modifier = modifier
    )
} 