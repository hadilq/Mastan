@file:OptIn(ExperimentalComposeUiApi::class, ExperimentalAnimationApi::class)

package com.androiddev.social.timeline.ui

import android.annotation.SuppressLint
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.ExperimentalTextApi
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.VideoFrameDecoder
import com.androiddev.social.AuthOptionalComponent.ParentComponent
import com.androiddev.social.AuthOptionalScope
import com.androiddev.social.AuthRequiredScope
import com.androiddev.social.EbonyApp
import com.androiddev.social.auth.ui.SignInPresenter
import com.androiddev.social.theme.*
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.squareup.anvil.annotations.ContributesTo
import javax.inject.Provider


@OptIn(ExperimentalMaterial3Api::class)
@ContributesTo(AuthOptionalScope::class)
interface AuthOptionalInjector {
    fun signInPresenter(): SignInPresenter
}

@OptIn(ExperimentalMaterial3Api::class)
@ContributesTo(AuthRequiredScope::class)
interface AuthRequiredInjector {
    fun avatarPresenter(): AvatarPresenter
    fun homePresenter(): TimelinePresenter
    fun mentionsPresenter(): MentionsPresenter
    fun notificationPresenter(): NotificationPresenter
    fun submitPresenter(): SubmitPresenter
    fun conversationPresenter(): Provider<ConversationPresenter>
}

@ExperimentalTextApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {

    override fun onStop() {
        super.onStop()
    }

    override fun onPause() {
        super.onPause()
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val loader = ImageLoader.Builder(this)
                .components {
                    if (SDK_INT >= 28) {
                        add(ImageDecoderDecoder.Factory())
                    } else {
                        add(GifDecoder.Factory())
                    }
                    add(VideoFrameDecoder.Factory())
                }
                .build()

            CompositionLocalProvider(LocalImageLoader provides loader) {
                var isDynamicTheme by remember { mutableStateOf(true) }
                EbonyTheme(isDynamicColor = isDynamicTheme) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        val scope = rememberCoroutineScope()
                        val navController = rememberAnimatedNavController()
                        Navigator(navController, scope) {
                            isDynamicTheme = !isDynamicTheme
                        }
                    }
                }
            }
        }
    }


    fun noAuthComponent() =
        ((applicationContext as EbonyApp).component as ParentComponent).createAuthOptionalComponent()

    fun AuthComponent() =
        ((applicationContext as EbonyApp).component as ParentComponent).createAuthOptionalComponent()
}
