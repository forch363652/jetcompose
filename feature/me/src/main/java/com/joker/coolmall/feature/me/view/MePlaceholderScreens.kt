package com.joker.coolmall.feature.me.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.joker.coolmall.feature.me.R

@Composable
fun MomentsScreen() {
    MeSimplePage(
        title = stringResource(id = R.string.me_drawer_moments),
        body = stringResource(id = R.string.me_placeholder_moments)
    )
}

@Composable
fun FavoritesScreen() {
    MeSimplePage(
        title = stringResource(id = R.string.me_drawer_favorites),
        body = stringResource(id = R.string.me_placeholder_favorites)
    )
}

@Composable
fun StatusScreen() {
    MeSimplePage(
        title = stringResource(id = R.string.me_drawer_status),
        body = stringResource(id = R.string.me_placeholder_status)
    )
}

@Composable
fun NotificationsScreen() {
    MeSimplePage(
        title = stringResource(id = R.string.me_drawer_notifications),
        body = stringResource(id = R.string.me_placeholder_notifications)
    )
}

@Composable
fun PrivacyScreen() {
    MeSimplePage(
        title = stringResource(id = R.string.me_drawer_privacy),
        body = stringResource(id = R.string.me_placeholder_privacy)
    )
}

@Composable
fun AccountSecurityScreen() {
    MeSimplePage(
        title = stringResource(id = R.string.me_drawer_account_security),
        body = stringResource(id = R.string.me_placeholder_account_security)
    )
}

@Composable
fun SettingsScreen() {
    MeSimplePage(
        title = stringResource(id = R.string.me_drawer_settings),
        body = stringResource(id = R.string.me_placeholder_settings)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MeSimplePage(
    title: String,
    body: String,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = title) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = body,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}


