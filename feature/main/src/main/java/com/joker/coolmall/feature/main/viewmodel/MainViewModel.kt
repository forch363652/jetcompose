package com.joker.coolmall.feature.main.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * 社交主壳 ViewModel
 *
 * - 默认展示：消息页
 * - 抽屉（dock）展开后：展示「联系人 / 群聊 / 分组」三页，并支持左右滑动切换
 */
@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    enum class DockPage {
        CONTACTS,
        GROUP_CHATS,
        GROUPS,
    }

    private val _isDockExpanded = MutableStateFlow(false)
    val isDockExpanded: StateFlow<Boolean> = _isDockExpanded.asStateFlow()

    private val _dockPageIndex = MutableStateFlow(0)
    val dockPageIndex: StateFlow<Int> = _dockPageIndex.asStateFlow()

    fun toggleDock() {
        _isDockExpanded.value = !_isDockExpanded.value
    }

    fun closeDock() {
        _isDockExpanded.value = false
    }

    fun openDock() {
        _isDockExpanded.value = true
    }

    fun selectDockPage(index: Int) {
        _dockPageIndex.value = index.coerceIn(0, DockPage.entries.size - 1)
        _isDockExpanded.value = true
    }

    fun setDockPageIndex(index: Int) {
        _dockPageIndex.value = index.coerceIn(0, DockPage.entries.size - 1)
    }
}

