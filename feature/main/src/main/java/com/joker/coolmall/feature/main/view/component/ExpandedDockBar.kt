package com.joker.coolmall.feature.main.view.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import com.joker.coolmall.feature.main.R
import com.joker.coolmall.feature.main.viewmodel.MainViewModel

/**
 * 展开态底部 Dock
 *
 * - 左侧：一块悬浮胶囊 Tab 区（联系人/群聊/分组，图标在上文字在下）
 * - 右侧：独立圆形返回按钮（收起 Dock 回消息页）
 */
@Composable
fun ExpandedDockBar(
    selectedIndex: Int,
    onContactsClick: () -> Unit,
    onGroupChatsClick: () -> Unit,
    onGroupsClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 左侧：胶囊 Tab 区
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    DockChipButton(
                        selected = selectedIndex == MainViewModel.DockPage.CONTACTS.ordinal,
                        text = stringResource(id = R.string.social_dock_contacts),
                        leadingIconDef = R.drawable.ic_new_contacts_def,
                        leadingIconAct = R.drawable.ic_new_contacts_act,
                        iconOnTop = true,
                        onClick = onContactsClick
                    )
                    DockChipButton(
                        selected = selectedIndex == MainViewModel.DockPage.GROUP_CHATS.ordinal,
                        text = stringResource(id = R.string.social_dock_group_chats),
                        leadingIconDef = R.drawable.ic_new_group_def,
                        leadingIconAct = R.drawable.ic_new_group_act,
                        iconOnTop = true,
                        onClick = onGroupChatsClick
                    )
                    DockChipButton(
                        selected = selectedIndex == MainViewModel.DockPage.GROUPS.ordinal,
                        text = stringResource(id = R.string.social_dock_groups),
                        leadingIconDef = R.drawable.ic_new_cat_def,
                        leadingIconAct = R.drawable.ic_new_cat_act,
                        iconOnTop = true,
                        onClick = onGroupsClick
                    )
                }
            }

            // 右侧：圆形返回按钮
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier.size(44.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { onBackClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.social_dock_back),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

/**
 * Dock Tab 按钮（展开态）
 */
@Composable
private fun DockChipButton(
    selected: Boolean,
    text: String,
    leadingIcon: ImageVector? = null,
    leadingIconDef: Int? = null,
    leadingIconAct: Int? = null,
    iconOnTop: Boolean = false,
    onClick: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
        else Color.Transparent
    ) {
        TextButton(
            onClick = onClick,
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
        ) {
            if (iconOnTop) {
                // 纵向布局：图标在上，文字在下
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    when {
                        leadingIconDef != null && leadingIconAct != null -> {
                            Icon(
                                painter = painterResource(
                                    id = if (selected) leadingIconAct else leadingIconDef
                                ),
                                contentDescription = text,
                                modifier = Modifier.size(20.dp),
                                tint = Color.Unspecified
                            )
                        }
                        leadingIcon != null -> {
                            Icon(
                                imageVector = leadingIcon,
                                contentDescription = text,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Text(
                        text = text,
                        maxLines = 1,
                        color = if (selected) MaterialTheme.colorScheme.primary
                               else MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            } else {
                // 横向布局：图标在左，文字在右
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    when {
                        leadingIconDef != null && leadingIconAct != null -> {
                            Icon(
                                painter = painterResource(
                                    id = if (selected) leadingIconAct else leadingIconDef
                                ),
                                contentDescription = text,
                                modifier = Modifier.size(18.dp),
                                tint = Color.Unspecified
                            )
                        }
                        leadingIcon != null -> {
                            Icon(
                                imageVector = leadingIcon,
                                contentDescription = text,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Text(
                        text = text,
                        maxLines = 1,
                        color = if (selected) MaterialTheme.colorScheme.primary
                               else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

