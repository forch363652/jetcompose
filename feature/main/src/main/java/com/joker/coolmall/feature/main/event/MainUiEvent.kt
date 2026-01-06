package com.joker.coolmall.feature.main.event

/**
 * 主界面 UI 事件（参考 NIA 的 UiEvent 模式）
 *
 * 所有用户交互和系统事件都通过 sealed class 定义，便于类型安全和扩展
 */
sealed interface MainUiEvent {
    /**
     * Dock 相关事件
     */
    sealed interface DockEvent : MainUiEvent {
        /**
         * 切换 Dock 展开/收起
         */
        data object ToggleExpand : DockEvent

        /**
         * 关闭 Dock（返回消息页）
         */
        data object CloseExpand : DockEvent

        /**
         * 选择 Dock 页面（通过索引）
         */
        data class SelectPage(val index: Int) : DockEvent

        /**
         * 通过滑动切换页面
         */
        data class ChangePageBySwipe(val index: Int) : DockEvent
    }

    /**
     * 搜索相关事件
     */
    sealed interface SearchEvent : MainUiEvent {
        /**
         * 搜索框输入内容变化
         */
        data class QueryChange(val query: String) : SearchEvent

        /**
         * 提交搜索
         */
        data class Submit(val query: String) : SearchEvent

        /**
         * 搜索框焦点变化
         */
        data class FocusChange(val isFocused: Boolean) : SearchEvent

        /**
         * 清除搜索内容
         */
        data object Clear : SearchEvent
    }

    /**
     * "我的"抽屉相关事件
     */
    sealed interface MeDrawerEvent : MainUiEvent {
        /**
         * 打开抽屉
         */
        data object Open : MeDrawerEvent

        /**
         * 关闭抽屉
         */
        data object Close : MeDrawerEvent

        /**
         * 点击抽屉项（导航到目标页面）
         */
        data class NavigateToRoute(val route: String) : MeDrawerEvent
    }

    /**
     * 滚动相关事件
     */
    sealed interface ScrollEvent : MainUiEvent {
        /**
         * 滚动状态变化（是否向下滚动）
         */
        data class StateChange(val isScrollingDown: Boolean) : ScrollEvent
    }
}

