package com.joker.coolmall.feature.main.state

/**
 * 主界面 UI 状态（参考 NIA 的 UiState 模式）
 *
 * 遵循 UDF（Unidirectional Data Flow）原则：
 * - UI 只消费 State，不持有业务状态
 * - 所有状态变更通过 ViewModel 管理
 */
data class MainUiState(
    /**
     * Dock 是否展开
     */
    val isDockExpanded: Boolean = false,

    /**
     * Dock 当前选中的页面索引（0=联系人, 1=群聊, 2=分组）
     */
    val dockPageIndex: Int = 0,

    /**
     * Dock 搜索框输入内容
     */
    val dockQuery: String = "",

    /**
     * 搜索框是否聚焦（用于控制键盘和 Dock 显隐）
     */
    val isSearchFocused: Boolean = false,

    /**
     * Dock 是否可见（根据滚动状态控制）
     */
    val isDockVisible: Boolean = true,

    /**
     * "我的"抽屉是否打开
     */
    val isMeDrawerOpen: Boolean = false,

    /**
     * Dock 左侧按钮的未读数/角标（0=不显示）
     */
    val dockBadgeCount: Int = 0,

    /**
     * 是否正在加载
     */
    val isLoading: Boolean = false,
)

