package com.feduss.tomato

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavHostController
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.feduss.tomato.enums.Consts
import com.feduss.tomato.enums.PrefParamName
import com.feduss.tomato.enums.Section
import com.feduss.tomato.receivers.TimerReceiver
import com.feduss.tomato.utils.AlarmUtils
import com.feduss.tomato.utils.NotificationUtils
import com.feduss.tomato.utils.PrefsUtils
import com.feduss.tomato.views.MainActivity

class MainViewController : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var navController: NavHostController
    private lateinit var context: Context
    private val timerReceiver = TimerReceiver()

    private val permissionGranted = MutableLiveData(false)
    private var overlayPermissionGranted = false

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle the splash screen transition.
        installSplashScreen()
        super.onCreate(savedInstanceState)

        context = this

        val filter = IntentFilter()
        registerReceiver(timerReceiver, filter)

        overlayPermissionGranted = Settings.canDrawOverlays(this)
        permissionGranted.postValue(overlayPermissionGranted)

        if(intent.getBooleanExtra(Consts.FromOngoingNotification.value, false)) {
            NotificationUtils.restoreTimerSecondsFromOngoingNotification(context)
        }

        setContent {
            MaterialTheme {
                navController = rememberSwipeDismissableNavController()
                val configuration = LocalConfiguration.current
                val screenHeight = configuration.screenHeightDp.dp
                val screenWidth = configuration.screenWidthDp.dp

                val color = Color(("#E3BAFF".toColorInt()))

                val arePermissionsGrantedState by permissionGranted.observeAsState()

                Scaffold(
                    modifier = Modifier
                        .width(screenWidth)
                        .height(screenHeight)) {
                    if (arePermissionsGrantedState == true) {
                        MainActivity(
                            context = context,
                            activity = this,
                            navController = navController,
                            chips = viewModel.loadDataFromPrefs(context),
                            startDestination = Section.Setup.baseRoute
                        )
                    } else if (!overlayPermissionGranted) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black)
                                .verticalScroll(rememberScrollState())
                                .padding(8.dp, 32.dp, 8.dp, 16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Per continuare, devi accettare il permesso di overlay. Clicca continua per proseguire.",
                                    color = Color("#E3BAFF".toColorInt()),
                                    textAlign = TextAlign.Center
                                )
                                Button(
                                    colors = ButtonDefaults.buttonColors(color, color),
                                    content = {
                                        Text(
                                            text = "Continua",
                                            color = Color.Black,
                                            textAlign = TextAlign.Center
                                        )
                                    },
                                    onClick = {
                                        requestOverlayPermission()
                                    }
                                )
                            }
                        }
                    } else {
                        //Placeholder for future usage
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black)
                        ){}
                    }
                }
            }
        }
    }

    private fun requestOverlayPermission() {
        startActivity(
            Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
        )
    }

    override fun onPause() {
        super.onPause()

        val isTimerActive = PrefsUtils.getPref(context, PrefParamName.IsTimerActive.name) == "true"

        if (isTimerActive) {
            AlarmUtils.setBackgroundAlert(context)
            NotificationUtils.setOngoingNotification(context)
        }
    }

    override fun onResume() {
        super.onResume()

        AlarmUtils.removeBackgroundAlert(context)
        NotificationUtils.removeOngoingNotification(context)

        if (!overlayPermissionGranted) {
            overlayPermissionGranted = Settings.canDrawOverlays(this)
            permissionGranted.postValue(overlayPermissionGranted)
        }
    }

    override fun onDestroy() {
        unregisterReceiver(timerReceiver)
        super.onDestroy()
    }
}