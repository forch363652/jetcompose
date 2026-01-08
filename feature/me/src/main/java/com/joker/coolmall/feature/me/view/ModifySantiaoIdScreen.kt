package com.joker.coolmall.feature.me.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.joker.coolmall.core.designsystem.R
import com.joker.coolmall.core.designsystem.theme.AppTheme
import com.joker.coolmall.feature.me.R as MeR
import com.joker.coolmall.feature.me.state.ModifySantiaoIdUiState
import com.joker.coolmall.feature.me.viewmodel.ModifySantiaoIdViewModel

/**
 * 修改三条ID页面入口
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModifySantiaoIdRoute(
    currentId: String,
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit = {},
    viewModel: ModifySantiaoIdViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // 初始化
    LaunchedEffect(currentId) {
        viewModel.initialize(currentId)
    }

    ModifySantiaoIdScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onInputTextChange = { viewModel.updateInputText(it) },
        onClearClick = { viewModel.clearInput() },
        onSaveClick = {
            viewModel.saveId(
                onSuccess = {
                    onSaveSuccess()
                    onBackClick()
                },
                onError = { /* 错误已在 UI 状态中显示 */ }
            )
        },
    )
}

/**
 * 修改三条ID页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModifySantiaoIdScreen(
    uiState: ModifySantiaoIdUiState,
    onBackClick: () -> Unit,
    onInputTextChange: (String) -> Unit,
    onClearClick: () -> Unit,
    onSaveClick: () -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 顶部导航栏
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(MaterialTheme.colorScheme.surface)
                    .statusBarsPadding()
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 返回按钮
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(35.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_left),
                        contentDescription = stringResource(MeR.string.me_profile_back),
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // 标题
                Text(
                    text = stringResource(MeR.string.me_modify_santiao_id_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )

                // 保存按钮
                IconButton(
                    onClick = onSaveClick,
                    enabled = !uiState.isSaving,
                    modifier = Modifier
                        .size(45.dp)
                        .padding(end = 10.dp)
                ) {
                    Icon(
                        painter = painterResource(id = MeR.drawable.button_save),
                        contentDescription = stringResource(MeR.string.me_modify_santiao_id_save),
                        tint = if (uiState.isSaving) {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                        } else {
                            MaterialTheme.colorScheme.primary
                        },
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // 内容区域
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 提示文本
                Text(
                    text = stringResource(MeR.string.me_modify_santiao_id_tip),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                // 输入框容器
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp)
                        .background(
                            MaterialTheme.colorScheme.surface,
                            RoundedCornerShape(0.dp)
                        )
                        .padding(horizontal = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 输入框
                        BasicTextField(
                            value = uiState.inputText,
                            onValueChange = onInputTextChange,
                            modifier = Modifier
                                .weight(1f)
                                .height(45.dp),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    keyboardController?.hide()
                                    onSaveClick()
                                }
                            ),
                            decorationBox = { innerTextField ->
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    if (uiState.inputText.isEmpty()) {
                                        Text(
                                            text = stringResource(MeR.string.me_modify_santiao_id_input_hint),
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                        )
                                    }
                                    innerTextField()
                                }
                            }
                        )

                        // 清除按钮
                        if (uiState.showClearButton) {
                            IconButton(
                                onClick = onClearClick,
                                modifier = Modifier.size(18.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = stringResource(MeR.string.me_modify_santiao_id_clear),
                                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                // 字符计数
                Text(
                    text = uiState.counterText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    textAlign = TextAlign.End
                )

                // 错误消息
                if (!uiState.errorMessage.isNullOrBlank()) {
                    Text(
                        text = uiState.errorMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ModifySantiaoIdScreenPreview() {
    AppTheme {
        ModifySantiaoIdScreen(
            uiState = ModifySantiaoIdUiState(
                currentId = "rexinrengx1553636",
                inputText = "rexinrengx1553636",
                showClearButton = true,
                counterText = "18 / 18 字"
            ),
            onBackClick = {},
            onInputTextChange = {},
            onClearClick = {},
            onSaveClick = {},
        )
    }
}

