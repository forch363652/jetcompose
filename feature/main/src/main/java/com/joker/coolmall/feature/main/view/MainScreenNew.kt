package com.joker.coolmall.feature.main.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.joker.coolmall.core.designsystem.theme.AppTheme
import com.joker.coolmall.feature.contacts.view.ContactsPagerScreen
import com.joker.coolmall.feature.groupchats.view.GroupChatsPagerScreen
import com.joker.coolmall.feature.groups.view.GroupsPagerScreen
import com.joker.coolmall.feature.main.view.component.ExpandedDockBar
import com.joker.coolmall.feature.main.view.component.SocialDockBar
import com.joker.coolmall.feature.main.event.MainUiEvent
import com.joker.coolmall.feature.main.state.MainUiState
import com.joker.coolmall.feature.main.viewmodel.MainViewModel
import com.joker.coolmall.feature.me.state.MeDrawerUiState
import com.joker.coolmall.feature.me.view.MeDrawerContent
import com.joker.coolmall.feature.me.viewmodel.MeDrawerViewModel
import com.joker.coolmall.feature.main.viewmodel.MessageViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * 社交主壳（纯 UI 组件，参考 NIA 架构）
 *
 * - 只接收 UiState 和 Event 回调
 * - 不持有任何业务状态
 * - 所有状态管理在 ViewModel 中
 */
@Composable
internal fun MainScreen(
    uiState: MainUiState,
    dockPagerState: PagerState,
    meDrawerUiState: MeDrawerUiState,
    onEvent: (MainUiEvent) -> Unit,
    onNavigate: (String) -> Unit,
    onMeDrawerItemClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        // 统一 WindowInsets 处理（参考 NIA）
        contentWindowInsets = ScaffoldDefaults
            .contentWindowInsets
            .exclude(WindowInsets.statusBars),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            // 收缩态：悬浮胶囊搜索 Dock
            AnimatedVisibility(
                visible = !uiState.isDockExpanded && (uiState.isDockVisible || uiState.isSearchFocused),
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = androidx.compose.animation.core.tween(260)
                ) + fadeIn(animationSpec = androidx.compose.animation.core.tween(260)),
                exit = slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = androidx.compose.animation.core.tween(220)
                ) + fadeOut(animationSpec = androidx.compose.animation.core.tween(220))
            ) {
                SocialDockBar(
                    query = uiState.dockQuery,
                    onQueryChange = { onEvent(MainUiEvent.SearchEvent.QueryChange(it)) },
                    onSearchSubmit = { onEvent(MainUiEvent.SearchEvent.Submit(it)) },
                    onSearchFocusChange = { onEvent(MainUiEvent.SearchEvent.FocusChange(it)) },
                    badgeCount = uiState.dockBadgeCount,
                    onToggleExpand = { onEvent(MainUiEvent.DockEvent.ToggleExpand) },
                )
            }

            // 展开态：iOS 风格悬浮 Dock
            AnimatedVisibility(
                visible = uiState.isDockExpanded,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = androidx.compose.animation.core.tween(260)
                ) + fadeIn(animationSpec = androidx.compose.animation.core.tween(260)),
                exit = slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = androidx.compose.animation.core.tween(220)
                ) + fadeOut(animationSpec = androidx.compose.animation.core.tween(220))
            ) {
                ExpandedDockBar(
                    selectedIndex = uiState.dockPageIndex,
                    onContactsClick = {
                        onEvent(MainUiEvent.DockEvent.SelectPage(MainViewModel.DockPage.CONTACTS.ordinal))
                    },
                    onGroupChatsClick = {
                        onEvent(MainUiEvent.DockEvent.SelectPage(MainViewModel.DockPage.GROUP_CHATS.ordinal))
                    },
                    onGroupsClick = {
                        onEvent(MainUiEvent.DockEvent.SelectPage(MainViewModel.DockPage.GROUPS.ordinal))
                    },
                    onBackClick = { onEvent(MainUiEvent.DockEvent.CloseExpand) },
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            if (!uiState.isDockExpanded) {
                // 消息页（好友相关消息）
                MessageRouteWrapper(
                    onOpenMeDrawer = { onEvent(MainUiEvent.MeDrawerEvent.Open) },
                    onScrollStateChange = { isScrollingDown ->
                        onEvent(MainUiEvent.ScrollEvent.StateChange(isScrollingDown))
                    }
                )

                // 收起态 + 输入聚焦：弱化背景，点击空白收起键盘
                AnimatedVisibility(
                    visible = uiState.isSearchFocused,
                    enter = fadeIn(animationSpec = androidx.compose.animation.core.tween(160)),
                    exit = fadeOut(animationSpec = androidx.compose.animation.core.tween(160)),
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.10f))
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                onEvent(MainUiEvent.SearchEvent.FocusChange(false))
                            }
                    )
                }
            } else {
                // 展开态：整屏切换到 联系人/群聊/分组（支持左右滑动）
                HorizontalPager(
                    userScrollEnabled = true,
                    state = dockPagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    when (page) {
                        MainViewModel.DockPage.CONTACTS.ordinal -> ContactsPagerScreen()
                        MainViewModel.DockPage.GROUP_CHATS.ordinal -> GroupChatsPagerScreen()
                        MainViewModel.DockPage.GROUPS.ordinal -> GroupsPagerScreen()
                    }
                }
            }
        }
    }

    // 左侧 Drawer：点击头像 / 右上角按钮打开
    MeDrawerSheet(
        isOpen = uiState.isMeDrawerOpen,
        uiState = meDrawerUiState,
        onClose = { onEvent(MainUiEvent.MeDrawerEvent.Close) },
        onItemClick = onMeDrawerItemClick,
    )
}

/**
 * "我的"抽屉 Sheet（左侧滑出）
 */
@Composable
private fun MeDrawerSheet(
    isOpen: Boolean,
    uiState: MeDrawerUiState,
    onClose: () -> Unit,
    onItemClick: (String) -> Unit,
) {
    // 背景遮罩
    AnimatedVisibility(
        visible = isOpen,
        enter = fadeIn(animationSpec = androidx.compose.animation.core.tween(160)),
        exit = fadeOut(animationSpec = androidx.compose.animation.core.tween(160)),
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.18f))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onClose() }
        )
    }

    // 抽屉内容
    AnimatedVisibility(
        visible = isOpen,
        enter = slideInHorizontally(
            initialOffsetX = { -it },
            animationSpec = androidx.compose.animation.core.tween(220)
        ) + fadeIn(animationSpec = androidx.compose.animation.core.tween(160)),
        exit = slideOutHorizontally(
            targetOffsetX = { -it },
            animationSpec = androidx.compose.animation.core.tween(220)
        ) + fadeOut(animationSpec = androidx.compose.animation.core.tween(120)),
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.CenterStart
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp),
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.78f)
            ) {
                MeDrawerContent(
                    uiState = uiState,
                    modifier = Modifier.fillMaxSize(),
                    onItemClick = { route ->
                        onClose()
                        onItemClick(route)
                    }
                )
            }
        }
    }
}

/**
 * MessageRoute 包装器
 */
@Composable
private fun MessageRouteWrapper(
    viewModel: MessageViewModel = hiltViewModel(),
    meDrawerViewModel: MeDrawerViewModel = hiltViewModel(),
    onOpenMeDrawer: () -> Unit = {},
    onScrollStateChange: (Boolean) -> Unit = {},
) {
    MessageRoute(
        viewModel = viewModel,
        meDrawerViewModel = meDrawerViewModel,
        onOpenMeDrawer = onOpenMeDrawer,
        onScrollStateChange = onScrollStateChange,
    )
}

@Preview(showBackground = true)
@Composable
private fun MainScreenNewPreview() {
    AppTheme {
        // Preview 需要提供 mock 数据
        // MainScreen(...)
    }
}

