package com.joker.coolmall.feature.me.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.joker.coolmall.core.designsystem.R
import com.joker.coolmall.core.designsystem.theme.AppTheme
import com.joker.coolmall.feature.me.R as MeR
import com.joker.coolmall.feature.me.state.SantiaoIdDetailUiState
import com.joker.coolmall.feature.me.viewmodel.SantiaoIdDetailViewModel

/**
 * 三条ID详情页面入口
 */
@Composable
fun SantiaoIdDetailRoute(
    onBackClick: () -> Unit,
    onModifyClick: (String) -> Unit,
    viewModel: SantiaoIdDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SantiaoIdDetailScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onModifyClick = { santiaoId ->
            if (uiState.canModify) {
                onModifyClick(santiaoId)
            }
        },
    )
}

/**
 * 三条ID详情页面
 */
@Composable
private fun SantiaoIdDetailScreen(
    uiState: SantiaoIdDetailUiState,
    onBackClick: () -> Unit,
    onModifyClick: (String) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 返回按钮
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .padding(start = 10.dp, top = 10.dp)
                    .statusBarsPadding()
            ) {
                Icon(
                    painter = painterResource(id = com.joker.coolmall.core.designsystem.R.drawable.ic_arrow_left),
                    contentDescription = stringResource(MeR.string.me_profile_back),
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(24.dp)
                )
            }

            // 主要内容区域
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                // 顶部间距
                Box(modifier = Modifier.height(50.dp))

                // ID 图标
                Image(
                    painter = painterResource(id = MeR.drawable.ic_my_profile_id_icon),
                    contentDescription = "santiao_id_icon",
                    modifier = Modifier.size(120.dp),
                    contentScale = ContentScale.Fit
                )

                // 三条ID文本
                Text(
                    text = stringResource(
                        MeR.string.me_santiao_id_detail_format,
                        uiState.displayId
                    ),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 10.dp)
                )

                // 介绍文本
                Text(
                    text = stringResource(MeR.string.me_santiao_id_detail_introduction),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp, vertical = 12.dp),
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.5
                )
            }

            // 修改按钮
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 180.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = MeR.drawable.btn_ok),
                    contentDescription = stringResource(MeR.string.me_santiao_id_detail_modify),
                    modifier = Modifier
                        .width(120.dp)
                        .clickable(
                            enabled = uiState.canModify,
                            onClick = {
                                if (uiState.canModify && uiState.santiaoId != null) {
                                    onModifyClick(uiState.santiaoId)
                                }
                            }
                        ),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SantiaoIdDetailScreenPreview() {
    AppTheme {
        SantiaoIdDetailScreen(
            uiState = SantiaoIdDetailUiState(
                santiaoId = "rexinrengx1553636"
            ),
            onBackClick = {},
            onModifyClick = {},
        )
    }
}

