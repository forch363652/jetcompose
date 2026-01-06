package com.joker.coolmall.feature.main.view.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import com.joker.coolmall.feature.main.R

/**
 * 社交 Dock 栏（收缩态）
 *
 * - 左侧：圆形抽屉按钮（带 Badge）
 * - 右侧：胶囊形搜索框（可输入）
 * - 整体悬浮：左右留白、圆角、轻阴影
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SocialDockBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearchSubmit: (String) -> Unit,
    onSearchFocusChange: (Boolean) -> Unit,
    badgeCount: Int = 0,
    onToggleExpand: () -> Unit,
    placeholder: String = stringResource(id = R.string.social_dock_placeholder),
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current

    fun clearFocusAndKeyboard() {
        keyboard?.hide()
        focusManager.clearFocus()
    }

    // Dock 容器：贴底 + 导航栏 padding + IME padding
    Box(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.ime) // 键盘弹出时把 Dock 顶到键盘上方
            .windowInsetsPadding(WindowInsets.navigationBars) // 适配导航栏
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp), // 左右留白，悬浮感
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DrawerCircleButton(
                badgeCount = badgeCount,
                onClick = {
                    clearFocusAndKeyboard()
                    onToggleExpand()
                }
            )

            SearchPill(
                query = query,
                onQueryChange = onQueryChange,
                placeholder = placeholder,
                onSearch = {
                    onSearchSubmit(query)
                    clearFocusAndKeyboard()
                },
                onClear = { onQueryChange("") },
                onFocusChange = onSearchFocusChange,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * 抽屉圆形按钮
 */
/**
 * 抽屉圆形按钮（带未读数角标）
 *
 * 角标显示规则：
 * - badgeCount > 0 时显示红色圆圈角标
 * - 角标显示数字（最多 99，超过显示 "99+"）
 * - 角标位于按钮右上角
 */
@Composable
private fun DrawerCircleButton(
    badgeCount: Int,
    onClick: () -> Unit,
) {
    BadgedBox(
        badge = {
            if (badgeCount > 0) {
                // 红色圆圈角标，白色数字（与图片样式一致）
                Surface(
                    color = Color(0xFFE53935), // 红色背景
                    shape = CircleShape,
                    modifier = Modifier.size(18.dp) // 角标大小
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (badgeCount > 99) "99+" else badgeCount.toString(),
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp,
            modifier = Modifier.size(40.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_home_drawer_toggle_btn),
                    contentDescription = stringResource(id = R.string.social_dock_menu),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

/**
 * 搜索栏（胶囊形，iOS 风格悬浮）
 */
@Composable
private fun SearchPill(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    onSearch: () -> Unit,
    onClear: () -> Unit,
    onFocusChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp,
        modifier = modifier.height(40.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 左侧：放大镜 icon
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(R.string.social_dock_search),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp)
            )

            // 可输入：BasicTextField
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onSearch() }),
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 2.dp)
                    .onFocusChanged { onFocusChange(it.isFocused) },
                decorationBox = { innerTextField ->
                    if (query.isBlank()) {
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    innerTextField()
                }
            )

            // 右侧：有内容时显示清除按钮
            if (query.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .clickable { onClear() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.social_dock_clear),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

