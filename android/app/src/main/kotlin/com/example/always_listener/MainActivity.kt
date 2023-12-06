package com.example.always_listener

import android.content.Intent
import androidx.annotation.NonNull
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import io.flutter.app.FlutterApplication
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.PluginRegistry
import android.media.MediaRecorder
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.Timer
import java.util.TimerTask
import java.util.*

import java.util.*

class MyForegroundService : Service() {

    companion object {
        private const val CHANNEL_ID = "my_foreground_service_channel"
        private const val EVENT_CHANNEL = "always_listener_event"
        private const val NOTIFICATION_ID = 1
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private lateinit var audioBuffer: ByteArrayOutputStream
    private lateinit var outputFile: File
    private var mediaRecorder: MediaRecorder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)
        val eventSink = (application as MyApplication).getEventSink()

        // Start the audio recorder
        startRecording()

        // Schedule a task to send audio data every 5 seconds
        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                // Check if eventSink is initialized
                Log.d("MyForegroundService", "check5native")

                    Log.d("MyForegroundService", "check5native2")

                    // Stop recording and send the recorded audio
                    stopRecording()
                    val audioData = audioBuffer.toByteArray()
                    audioBuffer.reset()

                    // Send the audio data to Flutter
                    Handler(mainLooper).post {
                        eventSink.success(audioData)
                    }

                    // Resume recording for the next interval
                    startRecording()
            }
        }, 5000, 5000)

        return START_STICKY
    }
    override fun onCreate() {
        super.onCreate()
        audioBuffer = ByteArrayOutputStream()
        outputFile = File.createTempFile("audio_temp", ".3gp", cacheDir)
    }

    private fun startRecording() {
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(outputFile.absolutePath)
            prepare()
            start()
        }
    }

    private fun stopRecording() {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "My Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("My Foreground Service")
            .setContentText("Running")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
    }

}

class MainActivity : FlutterActivity() {
    private val METHOD_CHANNEL = "always_listener"
    private val EVENT_CHANNEL = "always_listener_event"

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, METHOD_CHANNEL)
            .setMethodCallHandler { call, result ->
                if (call.method == "startForegroundService") {
                    startForegroundService()
                    result.success("SSUCCESS")
                } else {
                    result.notImplemented()
                }
            }
        EventChannel(flutterEngine.dartExecutor.binaryMessenger,
            EVENT_CHANNEL
        )
            .setStreamHandler(object : EventChannel.StreamHandler {
                override fun onListen(arguments: Any?, eventSink: EventChannel.EventSink?) {
                    if (eventSink != null) {
                        // Set the event sink in the application class
                        (application as MyApplication).setEventSink(eventSink)

                    }
                }

                override fun onCancel(arguments: Any?) {
                    // Handle cancellation if needed
                }
            })
    }

    private fun startForegroundService() {
        val intent = Intent(this, MyForegroundService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

}

class MyApplication : FlutterApplication() {
    private lateinit var eventSink: EventChannel.EventSink

    fun setEventSink(eventSink: EventChannel.EventSink) {
        this.eventSink = eventSink
    }
    fun getEventSink(): EventChannel.EventSink {
        return eventSink
    }

}
