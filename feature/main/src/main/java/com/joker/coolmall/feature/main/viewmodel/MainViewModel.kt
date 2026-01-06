package com.joker.coolmall.feature.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joker.coolmall.feature.main.event.MainUiEvent
import com.joker.coolmall.feature.main.state.MainUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 社交主壳 ViewModel（参考 NIA 架构）
 *
 * - 管理所有主界面的 UI 状态
 * - 处理所有 UI 事件
 * - 遵循 UDF（Unidirectional Data Flow）原则
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    // TODO: 后续接入未读数数据源（消息 Repository 或数据库）
    // private val messageRepository: MessageRepository,
) : ViewModel() {

    enum class DockPage {
        CONTACTS,
        GROUP_CHATS,
        GROUPS,
    }

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        // TODO: 加载未读数
        loadBadgeCount()
    }

    /**
     * 处理 UI 事件
     */
    fun handleEvent(event: MainUiEvent) {
        when (event) {
            // Dock 事件
            is MainUiEvent.DockEvent.ToggleExpand -> {
                _uiState.update { it.copy(isDockExpanded = !it.isDockExpanded) }
            }
            is MainUiEvent.DockEvent.CloseExpand -> {
                _uiState.update { it.copy(isDockExpanded = false) }
            }
            is MainUiEvent.DockEvent.SelectPage -> {
                _uiState.update { state ->
                    state.copy(
                        dockPageIndex = event.index.coerceIn(0, DockPage.entries.size - 1),
                        isDockExpanded = true
                    )
                }
            }
            is MainUiEvent.DockEvent.ChangePageBySwipe -> {
                _uiState.update { state ->
                    state.copy(
                        dockPageIndex = event.index.coerceIn(0, DockPage.entries.size - 1)
                    )
                }
            }

            // 搜索事件
            is MainUiEvent.SearchEvent.QueryChange -> {
                _uiState.update { it.copy(dockQuery = event.query) }
            }
            is MainUiEvent.SearchEvent.Submit -> {
                // TODO: 处理搜索提交
                handleSearch(event.query)
            }
            is MainUiEvent.SearchEvent.FocusChange -> {
                _uiState.update { state ->
                    state.copy(
                        isSearchFocused = event.isFocused,
                        // 聚焦时强制显示 Dock
                        isDockVisible = if (event.isFocused) true else state.isDockVisible
                    )
                }
            }
            is MainUiEvent.SearchEvent.Clear -> {
                _uiState.update { it.copy(dockQuery = "") }
            }

            // Me 抽屉事件
            is MainUiEvent.MeDrawerEvent.Open -> {
                _uiState.update { it.copy(isMeDrawerOpen = true) }
            }
            is MainUiEvent.MeDrawerEvent.Close -> {
                _uiState.update { it.copy(isMeDrawerOpen = false) }
            }
            is MainUiEvent.MeDrawerEvent.NavigateToRoute -> {
                // 关闭抽屉，导航由外部处理
                _uiState.update { it.copy(isMeDrawerOpen = false) }
        }

            // 滚动事件
            is MainUiEvent.ScrollEvent.StateChange -> {
                _uiState.update { state ->
                    // 搜索聚焦时不受滚动影响
                    if (state.isSearchFocused) {
                        state
                    } else {
                        state.copy(isDockVisible = !event.isScrollingDown)
                    }
                }
            }
        }
    }

    /**
     * 处理搜索提交
     */
    private fun handleSearch(query: String) {
        viewModelScope.launch {
            // TODO: 实现搜索逻辑
        }
    }

    /**
     * 加载未读数/角标
     * 
     * 未读数包括：
     * - 未读消息数（所有会话的未读消息总和）
     * - 新朋友请求数（可选）
     * - 其他通知数（可选）
     */
    private fun loadBadgeCount() {
        viewModelScope.launch {
            // TODO: 从 Repository 获取未读数
            // 方案1：从消息 Repository 获取所有会话的未读消息总数
            // val unreadMessages = messageRepository.getTotalUnreadCount()
            // 
            // 方案2：从会话列表计算未读数
            // val conversations = messageRepository.getAllConversations()
            // val totalUnread = conversations.sumOf { it.unreadCount }
            //
            // 方案3：从网络 API 获取未读数
            // val count = messageNetworkDataSource.getUnreadCount()
            
            // 临时：使用模拟数据（6 条未读消息，如示例图）
            // 后续接入真实数据源后，替换为实际未读数
            val mockUnreadCount = 6 // 模拟数据，后续替换为真实数据
            _uiState.update { it.copy(dockBadgeCount = mockUnreadCount) }
        }
    }

    /**
     * 刷新未读数
     */
    fun refreshBadgeCount() {
        loadBadgeCount()
    }
}

