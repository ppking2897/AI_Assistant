package com.bianca.ai_assistant.ui.bottomNavigation

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.bianca.ai_assistant.ui.article.ArticleDetailScreenWithViewModel
import com.bianca.ai_assistant.ui.article.ArticleEditScreenWithViewModel
import com.bianca.ai_assistant.ui.article.ArticleListScreenWithViewModel
import com.bianca.ai_assistant.ui.home.HomeScreenWithViewModel
import com.bianca.ai_assistant.ui.task.TaskDetailScreenWithViewModel
import com.bianca.ai_assistant.ui.task.TaskListScreenWithViewModel
import com.bianca.ai_assistant.viewModel.article.ArticleViewModel
import com.bianca.ai_assistant.viewModel.home.HomeViewModel
import com.bianca.ai_assistant.viewModel.task.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(
    navController: NavHostController,
    taskViewModel: TaskViewModel,
    articleViewModel: ArticleViewModel,
    homeViewModel: HomeViewModel,
    // ... 其他 ViewModel
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination
    val currentScreen = MainScreen.all.find { screen ->
        currentDestination?.route?.startsWith(screen.route) == true
    } ?: MainScreen.Home

    val mainTabs = MainScreen.all.map { it.route }
    val currentRoute = currentBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute in mainTabs) {
                NavigationBar {
                    MainScreen.all.forEach { screen ->
                        NavigationBarItem(
                            selected = currentRoute == screen.route,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                when (screen) {
                                    MainScreen.Home -> Icon(
                                        Icons.Default.Home,
                                        contentDescription = "首頁"
                                    )

                                    MainScreen.Tasks -> Icon(
                                        Icons.Default.Checklist,
                                        contentDescription = "任務"
                                    )

                                    MainScreen.Articles -> Icon(
                                        Icons.Default.Description,
                                        contentDescription = "記事"
                                    )

                                    MainScreen.AI -> Icon(
                                        Icons.Default.Android,
                                        contentDescription = "AI"
                                    )

                                    MainScreen.Profile -> Icon(
                                        Icons.Default.Person,
                                        contentDescription = "個人"
                                    )
                                }
                            },
                            label = { Text(screen.title) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = MainScreen.Home.route,
            modifier = Modifier
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            // 首頁
            composable(MainScreen.Home.route) {
                HomeScreenWithViewModel(
                    homeViewModel = homeViewModel,
                    onAddTask = {},
                    onAddNote = {},
                    onAskAI = {})
            }

            // 任務清單分頁
            composable(MainScreen.Tasks.route) {
                TaskListScreenWithViewModel(
                    viewModel = taskViewModel,
                    onTaskClick = { task ->
                        navController.navigate("taskDetail/${task.id}")
                    }
                    // 編輯Dialog邏輯不需要在這處理，由 TaskListScreenWithViewModel 內 onEditTask 控制
                )
            }

            // 任務詳情頁（跳頁）
            composable("taskDetail/{taskId}") { backStackEntry ->
                val taskId = backStackEntry.arguments?.getString("taskId")?.toLongOrNull()
                    ?: return@composable
                // 建議提供 ViewModel 取得單筆TaskEntity的方法
                val task by taskViewModel.getTaskByIdState(taskId).collectAsState()
                if (task != null) {
                    TaskDetailScreenWithViewModel(
                        task = task!!,
                        articleViewModel = articleViewModel,
                        onArticleClick = { article ->
                            navController.navigate("articleDetail/${article.id}")
                        },
                        onAddArticle = {
                            navController.navigate("articleEdit?taskId=${task!!.id}")
                        }
                    )
                } else {
                    CircularProgressIndicator()
                }
            }

            // 記事清單分頁
            composable(MainScreen.Articles.route) {
                ArticleListScreenWithViewModel(
                    navController = navController,  // <--- 加這個參數
                    viewModel = articleViewModel,
                    taskViewModel = taskViewModel,
                    onArticleClick = { article ->
                        navController.navigate("articleDetail/${article.id}")
                    },
                    onTaskClick = { task ->
                        navController.navigate("taskDetail/${task.id}")
                    }
                )
            }

            // 記事詳情頁（跳頁）
            composable("articleDetail/{articleId}") { backStackEntry ->
                val articleId = backStackEntry.arguments?.getString("articleId")?.toLongOrNull()
                    ?: return@composable
                ArticleDetailScreenWithViewModel(
                    viewModel = articleViewModel,
                    articleId = articleId,
                    getTaskById = { taskId -> taskViewModel.getTaskById(taskId) },
                    onTaskClick = { task ->
                        navController.navigate("taskDetail/${task.id}")
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            // 其餘分頁/功能頁
            composable(MainScreen.AI.route) {
//                AiScreen()
            }
            composable(MainScreen.Profile.route) {
//                ProfileScreen()
            }

            composable("articleEdit?articleId={articleId}&taskId={taskId}") { backStackEntry ->
                val articleId = backStackEntry.arguments?.getString("articleId")?.toLongOrNull()
                val taskId = backStackEntry.arguments?.getString("taskId")?.toLongOrNull()

                ArticleEditScreenWithViewModel(
                    viewModel = articleViewModel,
                    articleId = articleId,         // 可選編輯或新增
                    initialTaskId = taskId,        // 若是從任務詳情頁新增關聯記事
                    allTasks = taskViewModel.tasks.collectAsState().value,
                    onSaveSuccess = {
                        navController.popBackStack() // 儲存成功返回上一頁
                    },
                    onCancel = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}


sealed class MainScreen(val route: String, val title: String) {
    object Home : MainScreen("home", "首頁")
    object Tasks : MainScreen("tasks", "任務")
    object Articles : MainScreen("articles", "記事")
    object AI : MainScreen("ai", "AI")
    object Profile : MainScreen("profile", "個人")
    companion object {
        val all = listOf(Home, Tasks, Articles, AI, Profile)
    }
}
