package com.joker.coolmall.feature.main.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.joker.coolmall.feature.main.event.MainUiEvent
import com.joker.coolmall.feature.main.viewmodel.MainViewModel
import com.joker.coolmall.feature.me.viewmodel.MeDrawerViewModel
import kotlinx.coroutines.launch

/**
 * 主界面路由入口（参考 NIA 架构）
 *
 * - 负责收集 UiState 和处理副作用（BackHandler、Pager 同步等）
 * - UI 组件只接收 state & callback，不持有业务逻辑
 */
@Composable
internal fun MainRoute(
    viewModel: MainViewModel = hiltViewModel(),
    meDrawerViewModel: MeDrawerViewModel = hiltViewModel(),
    onNavigate: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val meDrawerUiState by meDrawerViewModel.uiState.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    val dockPagerState = rememberPagerState(
        initialPage = uiState.dockPageIndex
    ) {
        MainViewModel.DockPage.entries.size
    }

    // 副作用：Dock 展开时，系统返回键关闭 Dock
    BackHandler(enabled = uiState.isDockExpanded) {
        viewModel.handleEvent(MainUiEvent.DockEvent.CloseExpand)
    }

    // 副作用：点击按钮切换时，同步 pager
    LaunchedEffect(uiState.dockPageIndex, uiState.isDockExpanded) {
        if (uiState.isDockExpanded && dockPagerState.currentPage != uiState.dockPageIndex) {
            scope.launch {
                dockPagerState.animateScrollToPage(uiState.dockPageIndex)
            }
        }
    }

    // 副作用：用户左右滑动时，同步选中状态
    LaunchedEffect(dockPagerState.currentPage, uiState.isDockExpanded) {
        if (uiState.isDockExpanded) {
            viewModel.handleEvent(
                MainUiEvent.DockEvent.ChangePageBySwipe(dockPagerState.currentPage)
            )
        }
    }

    MainScreen(
        uiState = uiState,
        dockPagerState = dockPagerState,
        meDrawerUiState = meDrawerUiState,
        onEvent = viewModel::handleEvent,
        onNavigate = { route ->
            viewModel.handleEvent(MainUiEvent.MeDrawerEvent.NavigateToRoute(route))
            onNavigate(route)
        },
        onMeDrawerItemClick = { route ->
            meDrawerViewModel.updateSelectedRoute(route)
            viewModel.handleEvent(MainUiEvent.MeDrawerEvent.NavigateToRoute(route))
            onNavigate(route)
        },
    )
}

