package com.bianca.ai_assistant

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bianca.ai_assistant.infrastructure.alarm.PermissionFlow
import com.bianca.ai_assistant.ui.bottomNavigation.MainApp
import com.bianca.ai_assistant.ui.bottomNavigation.MainScreen
import com.bianca.ai_assistant.ui.theme.AI_AssistantTheme
import com.bianca.ai_assistant.viewModel.article.ArticleViewModel
import com.bianca.ai_assistant.viewModel.task.TaskViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            NotificationNavigationHandler(navController, intent)
            AI_AssistantTheme {
//                val context = LocalContext.current
//                var needPermissionDialog by remember { mutableStateOf(false) }
//
//                ExactAlarmPermissionChecker { hasPermission ->
//                    needPermissionDialog = !hasPermission
//                }
//
//                if (needPermissionDialog) {
//                    ExactAlarmPermissionDialog(
//                        show = true,
//                        onDismiss = { /* 或可設為 false，但建議強制引導直到有權限 */ },
//                        onGoToSettings = {
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
//                                    data = Uri.parse("package:" + context.packageName)
//                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                                }
//                                context.startActivity(intent)
//                            }
//                        }
//                    )
//                }

                PermissionFlow()

                val taskViewModel: TaskViewModel = hiltViewModel()
//                TaskListScreenWithViewModel(taskViewModel)

                val articleViewModel: ArticleViewModel = hiltViewModel()
                MainApp(navController , taskViewModel, articleViewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        _lastNotificationIntent.value = intent // 你要加這個 observable state
    }
}

private val _lastNotificationIntent = mutableStateOf<Intent?>(null)

@Composable
fun NotificationNavigationHandler(
    navController: NavHostController,
    intent: Intent?,
) {
    val navIntent = _lastNotificationIntent.value ?: intent
    LaunchedEffect(navIntent) {
        navIntent ?: return@LaunchedEffect
        val jumpTo = navIntent.getStringExtra("jumpTo")
        if (jumpTo == "tasks") {
            navController.navigate(MainScreen.Tasks.route) {
                popUpTo(0) { inclusive = false }
                launchSingleTop = true
            }
        }
        // 可擴充支援更多頁面
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AI_AssistantTheme {
        Greeting("Android")
    }
}