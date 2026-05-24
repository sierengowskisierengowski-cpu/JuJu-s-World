package com.jujusworld.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jujusworld.screens.*
import com.jujusworld.screens.games.*

object Routes {
    const val SPLASH     = "splash"
    const val HOME       = "home"
    const val SHOWS      = "shows"
    const val GAMES      = "games"
    const val BOOKS      = "books"
    const val MUSIC      = "music"
    const val ART        = "art"
    const val CAMERA     = "camera"
    const val BROWSER    = "browser"
    const val APPSTORE   = "appstore"
    const val SLEEP      = "sleep"
    const val PARENT     = "parent"
    // Original games
    const val LETTERS    = "games/letters"
    const val COUNTING   = "games/counting"
    const val COLORS     = "games/colors"
    const val SHAPES     = "games/shapes"
    // New game stubs
    const val BUBBLE_POP     = "games/bubblepop"
    const val ANIMAL_SOUNDS  = "games/animalsounds"
    const val PUZZLE         = "games/puzzle"
    const val WHACK_MOLE     = "games/whackmole"
    const val BALLOON_POP    = "games/balloonpop"
    const val PIANO          = "games/piano"
    const val DANCE_PARTY    = "games/danceparty"
    const val DRESS_UP       = "games/dressup"
    const val FEED_ANIMAL    = "games/feedanimal"
    const val NURSERY_RHYME  = "games/nurseryrhyme"
}

@Composable
fun JujuNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Routes.SPLASH) {
        composable(Routes.SPLASH)       { SplashScreen(navController) }
        composable(Routes.HOME)         { HomeScreen(navController) }
        composable(Routes.SHOWS)        { ShowsScreen(navController) }
        composable(Routes.GAMES)        { GamesScreen(navController) }
        composable(Routes.BOOKS)        { BooksScreen(navController) }
        composable(Routes.MUSIC)        { MusicScreen(navController) }
        composable(Routes.ART)          { ArtScreen(navController) }
        composable(Routes.CAMERA)       { CameraScreen(navController) }
        composable(Routes.BROWSER)      { BrowserScreen(navController) }
        composable(Routes.APPSTORE)     { AppStoreScreen(navController) }
        composable(Routes.SLEEP)        { SleepScreen(navController) }
        composable(Routes.PARENT)       { ParentScreen(navController) }
        // Original games
        composable(Routes.LETTERS)      { LettersGame(navController) }
        composable(Routes.COUNTING)     { CountingGame(navController) }
        composable(Routes.COLORS)       { ColorsGame(navController) }
        composable(Routes.SHAPES)       { ShapesGame(navController) }
        // New game stubs
        composable(Routes.BUBBLE_POP)   { BubblePopGame(navController) }
        composable(Routes.ANIMAL_SOUNDS){ AnimalSoundsGame(navController) }
        composable(Routes.PUZZLE)       { SimplePuzzleGame(navController) }
        composable(Routes.WHACK_MOLE)   { WhackMoleGame(navController) }
        composable(Routes.BALLOON_POP)  { BalloonPopGame(navController) }
        composable(Routes.PIANO)        { PianoGame(navController) }
        composable(Routes.DANCE_PARTY)  { DancePartyGame(navController) }
        composable(Routes.DRESS_UP)     { DressUpGame(navController) }
        composable(Routes.FEED_ANIMAL)  { FeedAnimalGame(navController) }
        composable(Routes.NURSERY_RHYME){ NurseryRhymeGame(navController) }
    }
}
