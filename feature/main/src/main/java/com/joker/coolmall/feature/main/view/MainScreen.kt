package com.joker.coolmall.feature.main.view

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.joker.coolmall.core.designsystem.theme.AppTheme
import com.joker.coolmall.core.designsystem.theme.SpaceHorizontalSmall
import com.joker.coolmall.core.designsystem.theme.SpacePaddingMedium
import com.joker.coolmall.feature.main.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import androidx.activity.compose.BackHandler
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.ui.res.stringResource
import com.joker.coolmall.feature.main.R
import androidx.compose.ui.unit.dp

/**
 * 主界面路由入口
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun MainRoute(
    viewModel: MainViewModel = hiltViewModel()
) {
    val isDockExpanded by viewModel.isDockExpanded.collectAsState()
    val dockPageIndex by viewModel.dockPageIndex.collectAsState()

    MainScreen(
        isDockExpanded = isDockExpanded,
        dockPageIndex = dockPageIndex,
        onToggleDock = viewModel::toggleDock,
        onCloseDock = viewModel::closeDock,
        onDockPageSelected = viewModel::selectDockPage,
        onDockPageChangedBySwipe = viewModel::setDockPageIndex,
    )
}

/**
 * 社交主壳
 *
 * - 默认展示：消息页（好友相关消息）
 * - 展开 dock 后：展示 联系人/群聊/分组，并可左右滑动切换
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun MainScreen(
    isDockExpanded: Boolean = false,
    dockPageIndex: Int = 0,
    onToggleDock: () -> Unit = {},
    onCloseDock: () -> Unit = {},
    onDockPageSelected: (Int) -> Unit = {},
    onDockPageChangedBySwipe: (Int) -> Unit = {},
) {
    // 协程作用域
    val scope = rememberCoroutineScope()

    // dock 展开时，系统返回键回到消息页
    BackHandler(enabled = isDockExpanded) {
        onCloseDock()
    }

    val dockPagerState = rememberPagerState(initialPage = dockPageIndex) {
        MainViewModel.DockPage.entries.size
    }

    // 点击按钮切换时，同步 pager
    LaunchedEffect(dockPageIndex, isDockExpanded) {
        if (isDockExpanded && dockPagerState.currentPage != dockPageIndex) {
            scope.launch {
                dockPagerState.animateScrollToPage(dockPageIndex)
            }
        }
    }

    // 用户左右滑动时，同步选中状态
    LaunchedEffect(dockPagerState.currentPage, isDockExpanded) {
        if (isDockExpanded) {
            onDockPageChangedBySwipe(dockPagerState.currentPage)
        }
    }

    Scaffold(
        // 排除顶部导航栏边距
        contentWindowInsets = ScaffoldDefaults
            .contentWindowInsets
            .exclude(WindowInsets.statusBars),
        bottomBar = {
            SocialDockBar(
                isExpanded = isDockExpanded,
                selectedIndex = dockPageIndex,
                onToggleExpand = onToggleDock,
                onContactsClick = { onDockPageSelected(MainViewModel.DockPage.CONTACTS.ordinal) },
                onGroupChatsClick = { onDockPageSelected(MainViewModel.DockPage.GROUP_CHATS.ordinal) },
                onGroupsClick = { onDockPageSelected(MainViewModel.DockPage.GROUPS.ordinal) },
                onSearchClick = {
                    // TODO: 后续接入搜索页/搜索弹窗
                    onCloseDock()
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (!isDockExpanded) {
                MessageRoute()
            } else {
                HorizontalPager(
                    userScrollEnabled = true,
                    state = dockPagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    when (page) {
                        MainViewModel.DockPage.CONTACTS.ordinal -> ContactsScreen()
                        MainViewModel.DockPage.GROUP_CHATS.ordinal -> GroupChatsScreen()
                        MainViewModel.DockPage.GROUPS.ordinal -> GroupsScreen()
                    }
                }
            }

            // 展开 dock 时的遮罩（半透明/“毛玻璃”效果先用半透明近似）
            AnimatedVisibility(
                visible = isDockExpanded,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.25f))
                        .clickable { onCloseDock() }
                )
            }
        }
    }
}

@Composable
private fun SocialDockBar(
    isExpanded: Boolean,
    selectedIndex: Int,
    onToggleExpand: () -> Unit,
    onContactsClick: () -> Unit,
    onGroupChatsClick: () -> Unit,
    onGroupsClick: () -> Unit,
    onSearchClick: () -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpacePaddingMedium),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(
                SpaceHorizontalSmall
            )
        ) {
            // 左侧抽屉按钮：后续你提供 icon 后，这里换成 painterResource(icon)
            DockTextButton(
                selected = false,
                onClick = onToggleExpand,
                text = if (isExpanded) stringResource(id = R.string.social_dock_back) else stringResource(
                    id = R.string.social_dock_menu
                ),
            )

            if (isExpanded) {
                DockTextButton(
                    selected = selectedIndex == MainViewModel.DockPage.CONTACTS.ordinal,
                    onClick = onContactsClick,
                    text = stringResource(id = R.string.social_dock_contacts),
                )
                DockTextButton(
                    selected = selectedIndex == MainViewModel.DockPage.GROUP_CHATS.ordinal,
                    onClick = onGroupChatsClick,
                    text = stringResource(id = R.string.social_dock_group_chats),
                )
                DockTextButton(
                    selected = selectedIndex == MainViewModel.DockPage.GROUPS.ordinal,
                    onClick = onGroupsClick,
                    text = stringResource(id = R.string.social_dock_groups),
                )
                DockTextButton(
                    selected = false,
                    onClick = onSearchClick,
                    text = stringResource(id = R.string.social_dock_search),
                )
            }
        }
    }
}

@Composable
private fun DockTextButton(
    selected: Boolean,
    onClick: () -> Unit,
    text: String,
) {
    Surface(
        color = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
        else Color.Transparent,
        shape = MaterialTheme.shapes.medium,
    ) {
        TextButton(onClick = onClick) {
            Text(text = text)
        }
    }
}

@Composable
private fun ContactsScreen() {
    PlaceholderScreen(text = stringResource(id = R.string.social_contacts_placeholder))
}

@Composable
private fun GroupChatsScreen() {
    PlaceholderScreen(text = stringResource(id = R.string.social_group_chats_placeholder))
}

@Composable
private fun GroupsScreen() {
    PlaceholderScreen(text = stringResource(id = R.string.social_groups_placeholder))
}

@Composable
private fun PlaceholderScreen(text: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
        androidx.compose.material3.Text(text = text)
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    AppTheme {
        MainScreen()
    }
}
