package com.example.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.Time
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.util.*

var audio= 0
lateinit var soundPoolAudio : SoundPool

class MainActivity : AppCompatActivity() , View.OnClickListener{
    val notif: Int =1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var cancelBtn = findViewById<Button>(R.id.cancelBtn)
        var setBtn= findViewById<Button>(R.id.setBtn)

        setBtn.setOnClickListener(this)
        cancelBtn.setOnClickListener(this)

        if (Build.VERSION.SDK_INT >= 21) {
            val audioAttrib = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            val builder = SoundPool.Builder()
            builder.setAudioAttributes(audioAttrib).setMaxStreams(6)  //for audio effects

            soundPoolAudio = builder.build()

        } else {
            soundPoolAudio = SoundPool(6, AudioManager.STREAM_MUSIC, 0)
        }

        audio= soundPoolAudio.load(this, R.raw.idk, 1)

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onClick(v: View) {
        var editText: EditText = findViewById(R.id.editText)
        var timePicker: TimePicker = findViewById(R.id.timePicker)

        val intent: Intent = Intent(this, AlarmReceiver::class.java)
        intent.putExtra("notification",notif)
        intent.putExtra("todo",editText.text.toString())
        val pendingIntent = PendingIntent.getBroadcast(
            this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT
        )

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val alarmManager2= getSystemService(ALARM_SERVICE) as AlarmManager

        when (v.id) {

             R.id.setBtn ->   {
                 Log.d("enter1","entering1")
                val hour = timePicker.currentHour
                val minute = timePicker.currentMinute

                // Create time.
                 val priortime: Calendar = Calendar.getInstance()
                 priortime.set(Calendar.HOUR_OF_DAY, hour)
                 priortime.set(Calendar.MINUTE, minute - 15)
                 priortime.set(Calendar.SECOND, 0)

                val startTime: Calendar = Calendar.getInstance()
                startTime.set(Calendar.HOUR_OF_DAY, hour)
                startTime.set(Calendar.MINUTE, minute)
                startTime.set(Calendar.SECOND, 0)
                val alarmStartTime: Long = startTime.getTimeInMillis()
                 val priorAlarm: Long = priortime.getTimeInMillis()
                 Log.d("alarms","${alarmStartTime},${priorAlarm}")

                      alarmManager.setExactAndAllowWhileIdle(
                          AlarmManager.RTC_WAKEUP,
                          priorAlarm,
                          pendingIntent
                      )

                 alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmStartTime, pendingIntent)
                Toast.makeText(this, "Done!", Toast.LENGTH_SHORT).show()
            }
             R.id.cancelBtn -> {
                alarmManager.cancel(pendingIntent)
                Toast.makeText(this, "Canceled.", Toast.LENGTH_SHORT).show()
            }
        }


    }
}