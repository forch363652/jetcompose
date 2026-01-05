package com.joker.coolmall.feature.main.view

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.joker.coolmall.core.designsystem.theme.AppTheme
import com.joker.coolmall.core.network.monitor.NetworkStatus
import com.joker.coolmall.feature.main.R
import com.joker.coolmall.feature.main.viewmodel.ConnectionState
import com.joker.coolmall.feature.main.viewmodel.MainViewModel
import com.joker.coolmall.feature.main.viewmodel.MessageViewModel
import kotlinx.coroutines.launch
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import com.joker.coolmall.feature.main.viewmodel.MessageViewModel

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

    // Dock 搜索框状态（使用 rememberSaveable 保存状态）
    var dockQuery by rememberSaveable { mutableStateOf("") }
    
    // Dock 显示/隐藏状态（根据滚动状态控制）
    var isDockVisible by remember { mutableStateOf(true) }

    Scaffold(
        // 排除顶部导航栏边距
        contentWindowInsets = ScaffoldDefaults
            .contentWindowInsets
            .exclude(WindowInsets.statusBars),
        containerColor = MaterialTheme.colorScheme.background, // 主界面背景色
        bottomBar = {
            // 收缩态时显示 Dock 栏（根据滚动状态控制显示/隐藏）
            // 展开态时 Dock 栏在 content 中显示，这里隐藏
            AnimatedVisibility(
                visible = (!isDockExpanded) && (isDockVisible || isDockExpanded),
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = androidx.compose.animation.core.tween(300)
                ) + fadeIn(),
                exit = slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = androidx.compose.animation.core.tween(300)
                ) + fadeOut()
            ) {
                SocialDockBar(
                    isExpanded = false, // bottomBar 中只显示收缩态
                    selectedIndex = dockPageIndex,
                    query = dockQuery,
                    onQueryChange = { dockQuery = it },
                    onSearchSubmit = { text ->
                        // TODO: 这里做搜索：例如打开搜索页 / 弹窗 / 列表过滤
                    },
                    badgeCount = 6, // TODO: 接你的未读/提醒数字
                    onToggleExpand = onToggleDock,
                    onCloseExpand = onCloseDock,
                    onContactsClick = { onDockPageSelected(MainViewModel.DockPage.CONTACTS.ordinal) },
                    onGroupChatsClick = { onDockPageSelected(MainViewModel.DockPage.GROUP_CHATS.ordinal) },
                    onGroupsClick = { onDockPageSelected(MainViewModel.DockPage.GROUPS.ordinal) }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            // 消息页（始终在底层）
            MessageRouteWrapper(
                onScrollStateChange = { isScrollingDown ->
                    // 向下滚动时隐藏 Dock，向上滚动或顶部时显示 Dock
                    isDockVisible = !isScrollingDown
                }
            )

            // 展开 dock 时的遮罩（在消息页上方，Pager 下方，可点击关闭）
            // 注意：遮罩不能覆盖 Pager，所以放在 Pager 之前
            AnimatedVisibility(
                visible = isDockExpanded,
                enter = fadeIn(animationSpec = androidx.compose.animation.core.tween(300)),
                exit = fadeOut(animationSpec = androidx.compose.animation.core.tween(300)),
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.25f))
                        .clickable { onCloseDock() }
                )
            }

            // 展开态：Dock 抽屉（Tab 栏 + 内容区，整体在遮罩上方）
            AnimatedVisibility(
                visible = isDockExpanded,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = androidx.compose.animation.core.tween(300)
                ) + fadeIn(animationSpec = androidx.compose.animation.core.tween(300)),
                exit = slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = androidx.compose.animation.core.tween(300)
                ) + fadeOut(animationSpec = androidx.compose.animation.core.tween(300)),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .fillMaxHeight(0.6f) // Dock 抽屉占屏幕高度的 60%
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp
                    ),
                    tonalElevation = 8.dp,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Tab 栏（与 SocialDockBar 展开态一致）
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // 联系人 Tab
                            DockChipButton(
                                selected = dockPageIndex == MainViewModel.DockPage.CONTACTS.ordinal,
                                text = stringResource(id = R.string.social_dock_contacts),
                                leadingIconDef = R.drawable.ic_new_contacts_def,
                                leadingIconAct = R.drawable.ic_new_contacts_act,
                                iconOnTop = true,
                                onClick = { onDockPageSelected(MainViewModel.DockPage.CONTACTS.ordinal) }
                            )
                            // 群聊 Tab
                            DockChipButton(
                                selected = dockPageIndex == MainViewModel.DockPage.GROUP_CHATS.ordinal,
                                text = stringResource(id = R.string.social_dock_group_chats),
                                leadingIconDef = R.drawable.ic_new_group_def,
                                leadingIconAct = R.drawable.ic_new_group_act,
                                iconOnTop = true,
                                onClick = { onDockPageSelected(MainViewModel.DockPage.GROUP_CHATS.ordinal) }
                            )
                            // 分组 Tab
                            DockChipButton(
                                selected = dockPageIndex == MainViewModel.DockPage.GROUPS.ordinal,
                                text = stringResource(id = R.string.social_dock_groups),
                                leadingIconDef = R.drawable.ic_new_cat_def,
                                leadingIconAct = R.drawable.ic_new_cat_act,
                                iconOnTop = true,
                                onClick = { onDockPageSelected(MainViewModel.DockPage.GROUPS.ordinal) }
                            )
                            // 返回按钮
                            DockChipButton(
                                selected = false,
                                text = stringResource(id = R.string.social_dock_back),
                                leadingIcon = Icons.Default.ArrowBack,
                                onClick = { onCloseDock() }
                            )
                        }

                        // 内容区（HorizontalPager，支持左右滑动）
                        HorizontalPager(
                            userScrollEnabled = true,
                            state = dockPagerState,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) { page ->
                            when (page) {
                                MainViewModel.DockPage.CONTACTS.ordinal -> ContactsScreen()
                                MainViewModel.DockPage.GROUP_CHATS.ordinal -> GroupChatsScreen()
                                MainViewModel.DockPage.GROUPS.ordinal -> GroupsScreen()
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * 社交 Dock 栏（iOS 风格悬浮抽屉）
 *
 * 【收缩态（Collapsed）】
 * - 左侧：圆形抽屉按钮（带 Badge）
 * - 右侧：胶囊形搜索框（可输入）
 * - 整体悬浮：左右留白、圆角、轻阴影
 *
 * 【展开态（Expanded）】
 * - 底部弹出抽屉面板（上圆角 16dp）
 * - 顶部 Tab 栏：联系人/群聊/分组
 * - 内容区：HorizontalPager（在 MainScreen 中管理）
 */
@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun SocialDockBar(
    isExpanded: Boolean,
    selectedIndex: Int,
    // 收起态搜索输入
    query: String,
    onQueryChange: (String) -> Unit,
    onSearchSubmit: (String) -> Unit,
    // 左侧抽屉按钮 Badge（0=不显示）
    badgeCount: Int = 0,
    // 展开/收起与分页
    onToggleExpand: () -> Unit,
    onCloseExpand: () -> Unit,
    onContactsClick: () -> Unit,
    onGroupChatsClick: () -> Unit,
    onGroupsClick: () -> Unit,
    // 文案（移除未使用的 textSearch 和 onSearchClick）
    placeholder: String = stringResource(id = R.string.social_dock_placeholder),
    textContacts: String = stringResource(id = R.string.social_dock_contacts),
    textGroupChats: String = stringResource(id = R.string.social_dock_group_chats),
    textGroups: String = stringResource(id = R.string.social_dock_groups),
    textBack: String = stringResource(id = R.string.social_dock_back),
) {
    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current

    fun clearFocusAndKeyboard() {
        keyboard?.hide()
        focusManager.clearFocus()
    }

    // Dock 容器：贴底 + 导航栏 padding
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        AnimatedContent(
            targetState = isExpanded,
            label = "dock_state"
        ) { expanded ->
            if (!expanded) {
                // =========================
                // 收缩态：iOS 风格悬浮胶囊（左右留白、圆角、阴影）
                // =========================
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
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                // =========================
                // 展开态：底部弹出抽屉面板（上圆角 16dp）
                // =========================
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        // Tab 栏
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // 联系人 Tab（图标在上，文案在下）
                            DockChipButton(
                                selected = selectedIndex == MainViewModel.DockPage.CONTACTS.ordinal,
                                text = textContacts,
                                leadingIconDef = R.drawable.ic_new_contacts_def,
                                leadingIconAct = R.drawable.ic_new_contacts_act,
                                iconOnTop = true,
                                onClick = {
                                    clearFocusAndKeyboard()
                                    onContactsClick()
                                }
                            )
                            // 群聊 Tab（图标在上，文案在下）
                            DockChipButton(
                                selected = selectedIndex == MainViewModel.DockPage.GROUP_CHATS.ordinal,
                                text = textGroupChats,
                                leadingIconDef = R.drawable.ic_new_group_def,
                                leadingIconAct = R.drawable.ic_new_group_act,
                                iconOnTop = true,
                                onClick = {
                                    clearFocusAndKeyboard()
                                    onGroupChatsClick()
                                }
                            )
                            // 分组 Tab（图标在上，文案在下）
                            DockChipButton(
                                selected = selectedIndex == MainViewModel.DockPage.GROUPS.ordinal,
                                text = textGroups,
                                leadingIconDef = R.drawable.ic_new_cat_def,
                                leadingIconAct = R.drawable.ic_new_cat_act,
                                iconOnTop = true,
                                onClick = {
                                    clearFocusAndKeyboard()
                                    onGroupsClick()
                                }
                            )
                            // 返回按钮（图标在左，文案在右）
                            DockChipButton(
                                selected = false,
                                text = textBack,
                                leadingIcon = Icons.Default.ArrowBack,
                                onClick = {
                                    clearFocusAndKeyboard()
                                    onCloseExpand()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 抽屉圆形按钮
 *
 * 位置：Dock 最左侧，独立的圆形按钮
 * 内容：抽屉/层叠/菜单类 icon（当前使用 Menu icon，TODO: 替换为实际抽屉图标）
 * 状态提示：按钮右上角可叠加角标 Badge（数字/红点）
 * 用于显示"未读/新朋友/待处理数量"等
 */
@Composable
private fun DrawerCircleButton(
    badgeCount: Int,
    onClick: () -> Unit,
) {
    // 左侧圆形按钮 + 红色 badge
    BadgedBox(
        badge = {
            if (badgeCount > 0) {
                // 你想要红点 or 数字都行：
                // 1) 红点：Box(Modifier.size(8.dp).background(Color.Red, CircleShape))
                // 2) 数字：这里直接用 Text
                Surface(
                    color = Color(0xFFE53935),
                    shape = CircleShape
                ) {
                    Text(
                        text = badgeCount.coerceAtMost(99).toString(),
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                    )
                }
            }
        }
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp, // 轻阴影，iOS 风格
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
 *
 * 位置：在抽屉按钮右侧，占据剩余宽度的胶囊形输入框
 * Icon 放置：放大镜 icon 固定在输入框左侧（在文本前）
 * 文本表现：
 * - 未输入时显示 placeholder："好友、群聊、聊天记录"
 * - 输入后显示用户输入内容（单行省略）
 * - 有内容时显示清除按钮
 * 悬浮效果：圆角、轻阴影
 */
@Composable
private fun SearchPill(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    onSearch: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(20.dp), // 大圆角，iOS 风格
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp, // 轻阴影
        modifier = modifier
            .height(40.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 左侧：放大镜 icon（固定在输入框左侧）
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "search",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp)
            )
            
            // 可输入：BasicTextField 更贴近"原生搜索条"观感
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
                    .padding(vertical = 2.dp),
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
                        contentDescription = "clear",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

/**
 * Dock Tab 按钮（展开态）
 *
 * 表现形式：图标 + 文案（或仅文案）
 * 选中状态：高亮（颜色/背景强调）
 * 用于：联系人 / 群聊 / 分组 / 搜索等 Tab
 *
 * @param selected 是否选中
 * @param text 按钮文本
 * @param leadingIcon ImageVector 图标（用于 Material Icons，与 drawable 资源二选一）
 * @param leadingIconDef 未选中状态的 drawable 资源 ID
 * @param leadingIconAct 选中状态的 drawable 资源 ID
 * @param iconOnTop 图标是否在上方（true=图标在上文字在下，false=图标在左文字在右），默认 false
 * @param onClick 点击回调
 */
@Composable
private fun DockChipButton(
    selected: Boolean,
    text: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    leadingIconDef: Int? = null, // 未选中状态的 drawable 资源 ID
    leadingIconAct: Int? = null, // 选中状态的 drawable 资源 ID
    iconOnTop: Boolean = false, // 图标是否在上方
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
                // 纵向布局：图标在上，文字在下（用于联系人、群聊、分组）
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // 优先使用 drawable 资源（支持选中/未选中状态切换）
                    when {
                        leadingIconDef != null && leadingIconAct != null -> {
                            // 使用 drawable 资源，根据选中状态切换图标
                            Icon(
                                painter = painterResource(
                                    id = if (selected) leadingIconAct else leadingIconDef
                                ),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = Color.Unspecified // 使用图标本身的颜色，不进行着色
                            )
                        }
                        leadingIcon != null -> {
                            // 使用 ImageVector（Material Icons）
                            Icon(
                                imageVector = leadingIcon,
                                contentDescription = null,
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
                // 横向布局：图标在左，文字在右（用于返回按钮）
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
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = Color.Unspecified
                            )
                        }
                        leadingIcon != null -> {
                            Icon(
                                imageVector = leadingIcon,
                                contentDescription = null,
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

/**
 * MessageRoute 包装器（解决 internal 访问问题）
 * 由于 MessageRoute 是 internal 的，在同一包内应该可以访问，但为了确保兼容性，使用包装器
 */
@Composable
private fun MessageRouteWrapper(
    viewModel: MessageViewModel = hiltViewModel(),
    onScrollStateChange: (Boolean) -> Unit = {},
) {
    val networkStatus by viewModel.networkStatus.collectAsStateWithLifecycle()
    val connectionState by viewModel.connectionState.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()

    MessageScreen(
        networkStatus = networkStatus,
        connectionState = connectionState,
        isSyncing = isSyncing,
        onOpenNetworkSettings = {}, // TODO: 如需自定义设置入口，可在此注入
        onScrollStateChange = onScrollStateChange,
    )
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    AppTheme {
        MainScreen()
    }
}
