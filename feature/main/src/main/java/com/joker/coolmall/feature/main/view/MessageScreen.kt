package com.joker.coolmall.feature.main.view

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.joker.coolmall.core.designsystem.theme.SpaceHorizontalSmall
import com.joker.coolmall.core.designsystem.theme.SpacePaddingMedium
import com.joker.coolmall.core.designsystem.theme.SpaceVerticalSmall
import com.joker.coolmall.core.network.monitor.NetworkStatus
import com.joker.coolmall.feature.main.R
import com.joker.coolmall.feature.main.viewmodel.ConnectionState
import com.joker.coolmall.feature.main.viewmodel.MessageViewModel

/**
 * 消息页（首页）入口
 */
@Composable
internal fun MessageRoute(
    viewModel: MessageViewModel = hiltViewModel(),
) {
    val networkStatus by viewModel.networkStatus.collectAsStateWithLifecycle()
    val connectionState by viewModel.connectionState.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()

    MessageScreen(
        networkStatus = networkStatus,
        connectionState = connectionState,
        isSyncing = isSyncing,
        onOpenNetworkSettings = {}, // TODO: 如需自定义设置入口，可在此注入
    )
}

/**
 * 消息页（好友相关消息）
 *
 * 你后续会给“老项目 UI 细节”，这里先把结构搭好：
 * - 顶部：三条标识 + 右侧 loading（收取中/连接中共用）
 * - 顶部下方：网络异常提示条（含“网络设置”）
 */
@Composable
internal fun MessageScreen(
    networkStatus: NetworkStatus,
    connectionState: ConnectionState,
    isSyncing: Boolean,
    onOpenNetworkSettings: () -> Unit,
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
        MessageHeader(
            connectionState = connectionState,
            isSyncing = isSyncing,
        )

        if (networkStatus is NetworkStatus.Unavailable) {
            NetworkWarningBar(
                onOpenSettings = {
                    runCatching {
                        context.startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        })
                    }.onFailure {
                        // 兜底：交给外部处理（或后续接入 Toast）
                        onOpenNetworkSettings()
                    }
                }
            )
        }

        // TODO: 这里后续接入会话列表（Room + paging 或网络拉取）
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpacePaddingMedium),
            shape = RoundedCornerShape(12.dp),
            tonalElevation = 1.dp,
        ) {
            Text(
                text = stringResource(id = R.string.social_message_placeholder),
                modifier = Modifier.padding(SpacePaddingMedium)
            )
        }
    }
}

@Composable
private fun MessageHeader(
    connectionState: ConnectionState,
    isSyncing: Boolean,
) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    val showLoading = isSyncing || connectionState == ConnectionState.Connecting

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = SpacePaddingMedium, vertical = SpaceVerticalSmall),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(id = R.string.social_friends_messages),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f),
            )

            if (showLoading) {
                CircularProgressIndicator(
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(18.dp),
                )
            }
        }

        // 三条标识（先按“标签/筛选”占位，后续你给具体文案和样式再替换）
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = SpacePaddingMedium),
            horizontalArrangement = Arrangement.spacedBy(SpaceHorizontalSmall)
        ) {
            HeaderTag(
                text = stringResource(id = R.string.social_tag_1),
                selected = selectedIndex == 0,
                onClick = { selectedIndex = 0 }
            )
            HeaderTag(
                text = stringResource(id = R.string.social_tag_2),
                selected = selectedIndex == 1,
                onClick = { selectedIndex = 1 }
            )
            HeaderTag(
                text = stringResource(id = R.string.social_tag_3),
                selected = selectedIndex == 2,
                onClick = { selectedIndex = 2 }
            )
        }
    }
}

@Composable
private fun HeaderTag(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        color = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
        else MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(999.dp),
        border = if (selected) null else androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
        )
    ) {
        TextButton(onClick = onClick) {
            Text(
                text = text,
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun NetworkWarningBar(
    onOpenSettings: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SpacePaddingMedium, vertical = SpaceVerticalSmall),
        color = Color(0xFFFFF1F1),
        shape = RoundedCornerShape(10.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = SpacePaddingMedium, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(id = R.string.social_network_bad),
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onOpenSettings) {
                Text(text = stringResource(id = R.string.social_network_settings))
            }
        }
    }
}


