package com.example.always_listener

import android.Manifest
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
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
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
import androidx.core.app.ActivityCompat
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.ByteBuffer
import java.util.Timer
import java.util.TimerTask
import java.util.*

import java.util.*

class MyForegroundService : Service() {

    companion object {
        private const val CHANNEL_ID = "my_foreground_service_channel"
        private const val EVENT_CHANNEL = "always_listener_event"
        private const val NOTIFICATION_ID = 1
        private const val SAMPLE_RATE = 44100 // or another suitable sample rate
    }

    private lateinit var recorder: AudioRecord
    private val handler = Handler()
    private val recordingRunnable = Runnable { sendAudio() }
    private val bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)
    private val audioBuffer = ShortArray(bufferSize / 2)

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()

        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)


        Log.d("MyForegroundService", "Foreground service started")


        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return START_STICKY
        }
        recorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )


        recorder.startRecording()

        handler.post(recordingRunnable)

        return START_STICKY
    }

    private fun sendAudio() {
        val eventSink = (application as MyApplication).getEventSink()
        val readResult = recorder.read(audioBuffer, 0, audioBuffer.size)

        if (readResult > 0) {
            val byteBuffer = ByteBuffer.allocate(readResult * 2)
            byteBuffer.asShortBuffer().put(audioBuffer, 0, readResult)
            val audioData = byteBuffer.array()

            eventSink.success(audioData)
        } else {
            Log.e("MyForegroundService", "Error reading audio data!")
        }

        handler.postDelayed(recordingRunnable, 5000)
    }

    override fun onDestroy() {
        super.onDestroy()
        recorder.release()
        handler.removeCallbacks(recordingRunnable)
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

                        (application as MyApplication).setEventSink(eventSink)

                    }
                }

                override fun onCancel(arguments: Any?) {
             
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
