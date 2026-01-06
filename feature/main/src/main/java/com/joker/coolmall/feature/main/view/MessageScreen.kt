package com.joker.coolmall.feature.main.view

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.joker.coolmall.core.designsystem.theme.SpaceHorizontalSmall
import com.joker.coolmall.core.designsystem.theme.SpacePaddingMedium
import com.joker.coolmall.core.network.monitor.NetworkStatus
import com.joker.coolmall.feature.main.R
import com.joker.coolmall.feature.main.state.ConversationItem
import com.joker.coolmall.feature.main.state.MessageUiState
import com.joker.coolmall.feature.main.viewmodel.ConnectionState
import com.joker.coolmall.feature.main.viewmodel.MessageViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import com.joker.coolmall.feature.me.viewmodel.MeDrawerViewModel

/**
 * 消息页（首页）入口（参考 NIA 架构）
 *
 * - 收集 MessageUiState
 * - 处理下拉刷新等副作用
 * - 传递状态和事件回调给 UI 组件
 */
@Composable
internal fun MessageRoute(
    viewModel: MessageViewModel = hiltViewModel(),
    meDrawerViewModel: MeDrawerViewModel = hiltViewModel(),
    onScrollStateChange: (Boolean) -> Unit = {}, // 滚动状态变化回调（true=向下滚动，false=向上滚动或顶部）
    onOpenMeDrawer: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val meDrawerUiState by meDrawerViewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    MessageScreen(
        uiState = uiState,
        avatarUrl = meDrawerUiState.avatarUrl,
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.refresh() },
        onOpenNetworkSettings = {}, // TODO: 如需自定义设置入口，可在此注入
        onOpenMeDrawer = onOpenMeDrawer,
        onScrollStateChange = onScrollStateChange,
    )
}

/**
 * 消息页（好友相关消息）（参考 NIA 架构）
 *
 * 页面结构（从上到下）：
 * - 顶部 AppBar（固定）：根据状态动态切换显示（图标模式/头像模式）
 * - 网络异常提示 Banner（条件显示）：紧贴 AppBar 下方
 * - 会话列表区（可滚动，支持下拉刷新）
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MessageScreen(
    uiState: MessageUiState,
    avatarUrl: String? = null,
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {},
    onOpenNetworkSettings: () -> Unit,
    onOpenMeDrawer: () -> Unit = {},
    onScrollStateChange: (Boolean) -> Unit = {}, // 滚动状态变化回调
) {
    val context = LocalContext.current
    
    when (val state = uiState) {
        is MessageUiState.Loading -> {
            // 加载中状态
            Scaffold(
                topBar = {
                    MessageTopAppBar(
                        conversationCount = 0,
                        showLoadingIcon = false,
                        showAvatar = false,
                        avatarUrl = null,
                        onIconClick = onOpenMeDrawer,
                        onAvatarClick = onOpenMeDrawer,
                        onAddClick = onOpenMeDrawer,
                    )
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center
                ) {
                    // 加载中提示
                }
            }
        }
        
        is MessageUiState.Success -> {
            val showNetworkBanner = state.networkStatus is NetworkStatus.Unavailable

            Scaffold(
                topBar = {
                    MessageTopAppBar(
                        conversationCount = state.conversationCount,
                        showLoadingIcon = state.shouldShowLoadingIcon,
                        showAvatar = state.shouldShowAvatar,
                        avatarUrl = if (state.shouldShowAvatar) avatarUrl else null,
                        onIconClick = onOpenMeDrawer,
                        onAvatarClick = onOpenMeDrawer,
                        onAddClick = onOpenMeDrawer,
                    )
                }
            ) { paddingValues ->
                PullToRefreshBox(
                    isRefreshing = isRefreshing,
                    onRefresh = onRefresh,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        // 网络异常提示 Banner（紧贴 AppBar 下方）
                        if (showNetworkBanner) {
                            NetworkErrorBanner(
                                onOpenSettings = {
                                    runCatching {
                                        context.startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS).apply {
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        })
                                    }.onFailure {
                                        onOpenNetworkSettings()
                                    }
                                }
                            )
                        }

                        // 会话列表区
                        if (state.conversations.isNotEmpty()) {
                            ConversationList(
                                conversations = state.conversations,
                                onScrollStateChange = onScrollStateChange,
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.background),
                                contentAlignment = Alignment.Center
                            ) {
                                // 空状态：不显示任何提示，保持克制
                            }
                        }
                    }
                }
            }
        }
        
        is MessageUiState.Error -> {
            // 错误状态
            Scaffold(
                topBar = {
                    MessageTopAppBar(
                        conversationCount = 0,
                        showLoadingIcon = false,
                        showAvatar = false,
                        avatarUrl = null,
                        onIconClick = onOpenMeDrawer,
                        onAvatarClick = onOpenMeDrawer,
                        onAddClick = onOpenMeDrawer,
                    )
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center
                ) {
                    // 错误提示
                }
            }
        }
    }
}

// ConversationItem 已移至 feature/main/state/MessageUiState.kt

/**
 * 消息页顶部 AppBar（参考 NIA 架构）
 *
 * 根据状态动态切换显示模式：
 *
 * 【模式1：图标模式】（有内容 && 不在下拉刷新 && 没有置顶消息）
 * - 左侧：图标（点击打开 Drawer）
 * - 中间：标题"三条(数字)"
 * - 标题右侧：加载图标（loading/同步中）
 * - 最右侧：+ 按钮
 *
 * 【模式2：头像模式】（有内容 && 有置顶消息）
 * - 左侧：用户头像（点击打开 Drawer）
 * - 中间：标题"三条(数字)"
 * - 标题右侧：加载图标（loading/同步中，可选）
 * - 最右侧：+ 按钮
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MessageTopAppBar(
    conversationCount: Int,
    showLoadingIcon: Boolean,
    showAvatar: Boolean,
    avatarUrl: String? = null,
    onIconClick: () -> Unit,
    onAvatarClick: () -> Unit,
    onAddClick: () -> Unit,
) {
    val context = LocalContext.current
    
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        navigationIcon = {
            if (showAvatar) {
                // 模式2：显示用户头像
                MessageTopAppBarAvatar(
                    avatarUrl = avatarUrl,
                    onClick = onAvatarClick,
                )
            } else {
                // 模式1：显示图标
                MessageTopAppBarIcon(
                    onClick = onIconClick,
                )
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(SpaceHorizontalSmall)
            ) {
                // 标题：三条
                Text(
                    text = stringResource(id = R.string.social_three_lines),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                // 数字（会话数量）
                if (conversationCount > 0) {
                    Text(
                        text = "($conversationCount)",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                // 标题右侧：加载图标（loading/同步中）
                if (showLoadingIcon) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        actions = {
            // 最右侧：+ 按钮（添加/新建）
            IconButton(onClick = onAddClick) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.social_add_button),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    )
}

/**
 * AppBar 左侧图标（模式1：无置顶消息时显示）
 */
@Composable
private fun MessageTopAppBarIcon(
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        // TODO: 使用实际的图标资源
        Icon(
            painter = painterResource(id = R.drawable.ic_home_drawer_toggle_btn),
            contentDescription = stringResource(id = R.string.social_dock_menu),
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(20.dp)
        )
    }
}

/**
 * AppBar 左侧头像（模式2：有置顶消息时显示）
 */
@Composable
private fun MessageTopAppBarAvatar(
    avatarUrl: String?,
    onClick: () -> Unit,
) {
    val context = LocalContext.current
    
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (avatarUrl != null && avatarUrl.isNotBlank()) {
            // 有头像 URL：加载网络图片，失败时显示默认头像
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(avatarUrl)
                    .crossfade(true)
                    .placeholder(com.joker.coolmall.feature.me.R.drawable.avatar_default)
                    .error(com.joker.coolmall.feature.me.R.drawable.avatar_default)
                    .build(),
                contentDescription = stringResource(id = R.string.social_avatar_content_description),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // 无头像 URL：显示默认头像
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(com.joker.coolmall.feature.me.R.drawable.avatar_default)
                    .build(),
                contentDescription = stringResource(id = R.string.social_avatar_content_description),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}


/**
 * 网络异常提示 Banner
 *
 * 位置：紧贴 AppBar 下方
 * 视觉：浅红色背景，左侧警告图标，文本，整体可点击跳转系统网络设置
 */
@Composable
private fun NetworkErrorBanner(
    onOpenSettings: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenSettings() },
        color = MaterialTheme.colorScheme.errorContainer,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = SpacePaddingMedium, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SpaceHorizontalSmall)
        ) {
            // 左侧：警告图标
            Icon(
                painter = painterResource(id = android.R.drawable.ic_dialog_alert),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(20.dp)
            )
            // 文本
            Text(
                text = stringResource(id = R.string.social_network_error_message),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * 会话列表
 */
@Composable
private fun ConversationList(
    conversations: List<ConversationItem>,
    onScrollStateChange: (Boolean) -> Unit = {},
) {
    val listState = rememberLazyListState()
    var previousFirstVisibleItemIndex by remember { mutableIntStateOf(0) }
    var previousScrollOffset by remember { mutableIntStateOf(0) }

    // 监听滚动状态变化
    LaunchedEffect(listState) {
        snapshotFlow {
            Pair(listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset)
        }.collect { (currentIndex, currentOffset) ->
            // 判断滚动方向
            val isScrollingDown = when {
                // 如果 item 索引增加，肯定是向下滚动
                currentIndex > previousFirstVisibleItemIndex -> true
                // 如果 item 索引减少，肯定是向上滚动
                currentIndex < previousFirstVisibleItemIndex -> false
                // 如果 item 索引相同，比较 offset
                else -> currentOffset > previousScrollOffset
            }
            
            // 如果滚动到顶部，显示 Dock
            val isAtTop = currentIndex == 0 && currentOffset == 0
            
            // 向下滚动且不在顶部时隐藏 Dock，否则显示 Dock
            onScrollStateChange(isScrollingDown && !isAtTop)
            
            previousFirstVisibleItemIndex = currentIndex
            previousScrollOffset = currentOffset
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize()
    ) {
        items(conversations) { conversation ->
            ConversationItem(conversation = conversation)
        }
    }
}

/**
 * 单个会话单元
 *
 * 结构（从左到右）：
 * [头像]  会话名称           时间
 *         最近一条消息预览
 */
@Composable
private fun ConversationItem(
    conversation: ConversationItem
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                // TODO: 跳转到聊天页面
            },
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = SpacePaddingMedium, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(SpacePaddingMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左侧头像
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                // TODO: 加载头像图片
                Text(
                    text = conversation.name.take(1),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            // 中间内容
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // 第一行：会话名称（加粗）
                Text(
                    text = conversation.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                // 第二行：最近一条消息摘要（灰色、单行省略）
                Text(
                    text = conversation.lastMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    maxLines = 1
                )
            }

            // 右侧：时间
            Text(
                text = formatTimestamp(conversation.timestamp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

/**
 * 格式化时间戳（临时实现，后续使用 TimeUtils）
 */
private fun formatTimestamp(timestamp: Long): String {
    // TODO: 使用 TimeUtils 格式化时间
    return "12:00"
}


