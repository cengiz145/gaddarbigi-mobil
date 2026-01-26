package com.example.gaddarquiz.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.EaseInOutCirc
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

import com.example.gaddarquiz.ui.screens.*

const val ANIM_DURATION = 400

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = "home",
        enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Left, 
                animationSpec = tween(ANIM_DURATION, easing = FastOutSlowInEasing)
            ) + fadeIn(animationSpec = tween(ANIM_DURATION))
        },
        exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Left, 
                animationSpec = tween(ANIM_DURATION, easing = FastOutSlowInEasing)
            ) + fadeOut(animationSpec = tween(ANIM_DURATION))
        },
        popEnterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Right, 
                animationSpec = tween(ANIM_DURATION, easing = FastOutSlowInEasing)
            ) + fadeIn(animationSpec = tween(ANIM_DURATION))
        },
        popExitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Right, 
                animationSpec = tween(ANIM_DURATION, easing = FastOutSlowInEasing)
            ) + fadeOut(animationSpec = tween(ANIM_DURATION))
        }
    ) {
        composable(
            "home",
            enterTransition = { fadeIn(tween(600, easing = EaseInOutCirc)) }, 
            exitTransition = { fadeOut(tween(ANIM_DURATION)) },
            popEnterTransition = { fadeIn(tween(ANIM_DURATION)) },
            popExitTransition = { fadeOut(tween(ANIM_DURATION)) }
        ) {
            HomeScreen(
                onNavigateToCategory = { navController.navigate("category") },
                onNavigateToWheel = { navController.navigate("yaramaz_wheel") },
                onNavigateToTower = { 
                    navController.navigate("tower") 
                },
                onReportError = {
                    val intent = android.content.Intent(android.content.Intent.ACTION_SENDTO).apply {
                        data = android.net.Uri.parse("mailto:") 
                        putExtra(android.content.Intent.EXTRA_EMAIL, arrayOf("gaddaraiprojeler@protonmail.com"))
                        putExtra(android.content.Intent.EXTRA_SUBJECT, "Gaddar Bilgi Yarışması Hata Bildirimi")
                    }
                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        android.widget.Toast.makeText(context, "Mail uygulaması bulunamadı", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
        composable(
            "category",
            enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(ANIM_DURATION)) + fadeIn(animationSpec = tween(ANIM_DURATION)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(ANIM_DURATION)) + fadeOut(animationSpec = tween(ANIM_DURATION)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(ANIM_DURATION)) + fadeIn(animationSpec = tween(ANIM_DURATION)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(ANIM_DURATION)) + fadeOut(animationSpec = tween(ANIM_DURATION)) }
        ) {
            CategoryScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToGameMode = { categoryId -> 
                    navController.navigate("game_mode/$categoryId") 
                },
                onNavigateToWheelSelection = {
                    navController.navigate("wheel_selection")
                },
                onReportError = {
                    val intent = android.content.Intent(android.content.Intent.ACTION_SENDTO).apply {
                        data = android.net.Uri.parse("mailto:") 
                        putExtra(android.content.Intent.EXTRA_EMAIL, arrayOf("gaddaraiprojeler@protonmail.com"))
                        putExtra(android.content.Intent.EXTRA_SUBJECT, "Gaddar Bilgi Yarışması Hata Bildirimi")
                    }
                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        android.widget.Toast.makeText(context, "Mail uygulaması bulunamadı", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
        composable(
            "wheel_selection",
            enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(ANIM_DURATION)) + fadeIn(animationSpec = tween(ANIM_DURATION)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(ANIM_DURATION)) + fadeOut(animationSpec = tween(ANIM_DURATION)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(ANIM_DURATION)) + fadeIn(animationSpec = tween(ANIM_DURATION)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(ANIM_DURATION)) + fadeOut(animationSpec = tween(ANIM_DURATION)) }
        ) {
            WheelSelectionScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToQuiz = {
                    navController.navigate("quiz/custom_wheel_selection/rahat/10/0")
                }
            )
        }
        composable(
            "game_mode/{categoryId}",
            enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(ANIM_DURATION)) + fadeIn(animationSpec = tween(ANIM_DURATION)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(ANIM_DURATION)) + fadeOut(animationSpec = tween(ANIM_DURATION)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(ANIM_DURATION)) + fadeIn(animationSpec = tween(ANIM_DURATION)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(ANIM_DURATION)) + fadeOut(animationSpec = tween(ANIM_DURATION)) }
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
            GameModeScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToQuiz = { count, mode, time ->
                    navController.navigate("quiz/$categoryId/$mode/$count/$time")
                },
                onNavigateToConfig = { count ->
                    navController.navigate("gaddar_config/$categoryId/$count")
                }
            )
        }
        composable(
            "gaddar_config/{categoryId}/{questionCount}",
            enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(ANIM_DURATION)) + fadeIn(animationSpec = tween(ANIM_DURATION)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(ANIM_DURATION)) + fadeOut(animationSpec = tween(ANIM_DURATION)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(ANIM_DURATION)) + fadeIn(animationSpec = tween(ANIM_DURATION)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(ANIM_DURATION)) + fadeOut(animationSpec = tween(ANIM_DURATION)) }
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
            val questionCount = backStackEntry.arguments?.getString("questionCount")?.toIntOrNull() ?: 10
            GaddarConfigScreen(
                questionCount = questionCount,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToQuiz = { count, mode, time ->
                    navController.navigate("quiz/$categoryId/$mode/$count/$time")
                }
            )
        }
        composable(
            "quiz/{categoryId}/{mode}/{questionCount}/{timeLimit}?yaramazMode={yaramazMode}",
            arguments = listOf(
                navArgument("categoryId") { type = NavType.StringType },
                navArgument("mode") { type = NavType.StringType },
                navArgument("questionCount") { type = NavType.IntType },
                navArgument("timeLimit") { type = NavType.IntType },
                navArgument("yaramazMode") { type = NavType.StringType; defaultValue = "" ; nullable = true }
            ),
            enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(ANIM_DURATION)) + fadeIn(animationSpec = tween(ANIM_DURATION)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(ANIM_DURATION)) + fadeOut(animationSpec = tween(ANIM_DURATION)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(ANIM_DURATION)) + fadeIn(animationSpec = tween(ANIM_DURATION)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(ANIM_DURATION)) + fadeOut(animationSpec = tween(ANIM_DURATION)) }
        ) { backStackEntry ->
             val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
             val mode = backStackEntry.arguments?.getString("mode") ?: "rahat"
             val questionCount = backStackEntry.arguments?.getInt("questionCount") ?: 10
             val timeLimit = backStackEntry.arguments?.getInt("timeLimit") ?: 0
             val yaramazMode = backStackEntry.arguments?.getString("yaramazMode") ?: ""
             
             QuizScreen(
                 categoryId = categoryId,
                 mode = mode,
                 questionCount = questionCount,
                 timeLimit = timeLimit,
                 yaramazModeStr = yaramazMode, // Pass to screen
                 onNavigateBack = { navController.popBackStack() },
                 onQuizComplete = if (yaramazMode.isNotEmpty()) {
                     { score ->
                         com.example.gaddarquiz.utils.YaramazGameManager.finishRound(score)
                         if (com.example.gaddarquiz.utils.YaramazGameManager.isGameActive.value) {
                             navController.popBackStack("yaramaz_wheel", inclusive = false)
                         } else {
                             navController.navigate("yaramaz_result") {
                                 popUpTo("home") { inclusive = false }
                             }
                         }
                     }
                 } else null
             )
        }
        
        composable("yaramaz_result") {
             YaramazResultScreen(
                 onNavigateHome = {
                     navController.popBackStack("home", inclusive = false)
                 }
             )
        }
        composable(
            "wheel",
            enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(ANIM_DURATION)) + fadeIn(animationSpec = tween(ANIM_DURATION)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(ANIM_DURATION)) + fadeOut(animationSpec = tween(ANIM_DURATION)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(ANIM_DURATION)) + fadeIn(animationSpec = tween(ANIM_DURATION)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(ANIM_DURATION)) + fadeOut(animationSpec = tween(ANIM_DURATION)) }
        ) {
            WheelScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToQuiz = {
                    navController.navigate("quiz/custom_wheel_selection/rahat/10/0")
                }
            )
        }
        
        composable(
            "yaramaz_wheel",
            enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(ANIM_DURATION)) + fadeIn(animationSpec = tween(ANIM_DURATION)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(ANIM_DURATION)) + fadeOut(animationSpec = tween(ANIM_DURATION)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(ANIM_DURATION)) + fadeIn(animationSpec = tween(ANIM_DURATION)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(ANIM_DURATION)) + fadeOut(animationSpec = tween(ANIM_DURATION)) }
        ) {
            YaramazWheelScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToQuiz = { mode ->
                    val timeLimit = if (mode.name == "TIME_BOMB") 5 else 20
                    navController.navigate("quiz/custom_yaramaz/gaddar/1/$timeLimit?yaramazMode=${mode.name}")
                }
            )
        }

        composable(
            "chance_wheel",
            enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(ANIM_DURATION)) + fadeIn(animationSpec = tween(ANIM_DURATION)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(ANIM_DURATION)) + fadeOut(animationSpec = tween(ANIM_DURATION)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(ANIM_DURATION)) + fadeIn(animationSpec = tween(ANIM_DURATION)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(ANIM_DURATION)) + fadeOut(animationSpec = tween(ANIM_DURATION)) }
        ) {
            ChanceWheelScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("tower") {
            com.example.gaddarquiz.feature.tower.TowerScreen(
                onNavigateBack = { navController.popBackStack() },
                onStartQuiz = { categoryId, isBoss, difficulty ->
                     // Use NEW Tower Battle Screen
                     // Need current floor from GameManager?
                     val floor = com.example.gaddarquiz.feature.tower.TowerGameManager.playerState.currentFloor
                     navController.navigate("tower_battle/$categoryId/$floor/$isBoss/$difficulty")
                }
            )
        }
        
        composable(
            "tower_battle/{categoryId}/{floor}/{isBoss}/{difficulty}",
            arguments = listOf(
                navArgument("categoryId") { type = NavType.StringType },
                navArgument("floor") { type = NavType.IntType },
                navArgument("isBoss") { type = NavType.BoolType },
                navArgument("difficulty") { type = NavType.StringType }
            )
        ) { backStackEntry ->
             val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
             val floor = backStackEntry.arguments?.getInt("floor") ?: 1
             val isBoss = backStackEntry.arguments?.getBoolean("isBoss") ?: false
             val difficulty = backStackEntry.arguments?.getString("difficulty") ?: "Normal"
             
             com.example.gaddarquiz.feature.tower.TowerBattleScreen(
                 categoryId = categoryId,
                 floor = floor,
                 isBoss = isBoss,
                 difficulty = difficulty,
                 onNavigateBack = { navController.popBackStack() },
                 onBattleComplete = { isWin ->
                     com.example.gaddarquiz.feature.tower.TowerGameManager.onQuestionResult(isWin, context)
                     navController.popBackStack()
                 }
             )
        }
    }
}
