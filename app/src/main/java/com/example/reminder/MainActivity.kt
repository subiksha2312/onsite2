package com.example.reminder

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.AlarmClock
import android.text.format.Time
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*


class MainActivity : AppCompatActivity() , View.OnClickListener{
    val notif: Int =1
    private var bAlarmAccess : Boolean = false
    private var _ALARMSETREQUEST = 100
    private lateinit var mReceiver : AlarmReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkAlarmPermission()

        var cancelBtn = findViewById<Button>(R.id.cancelBtn)
        var setBtn= findViewById<Button>(R.id.setBtn)

        setBtn.setOnClickListener(this)
        cancelBtn.setOnClickListener(this)

        mReceiver = AlarmReceiver()
        var filter : IntentFilter = IntentFilter()
        filter.addAction("com.android.deskclock.ALARM_DISMISS")
        filter.addAction("com.android.deskclock.ALARM_DONE")
        registerReceiver(mReceiver, filter)

    }

    private fun checkAlarmPermission() {
        val alarmPermission = ContextCompat.checkSelfPermission(this,
            Manifest.permission.SET_ALARM)
        if (alarmPermission != PackageManager.PERMISSION_GRANTED) {
            Log.d("permission1.1", "Making alarm set request")
            makeAlarmPermRequest()
        }
        else {
            Log.d("permission1.2", "alarm Permission already granted")
            bAlarmAccess = true
        }

    }

    private fun makeAlarmPermRequest() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.SET_ALARM),
            _ALARMSETREQUEST)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            _ALARMSETREQUEST -> {
                Log.d("permission1.3", "Receiving alarm permission")
                bAlarmAccess = !(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED)
                Log.d("permission1.4", "perm granted = $bAlarmAccess")

            }
        }
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

                 val clockIntent = Intent(AlarmClock.ACTION_SET_ALARM)
                 clockIntent.putExtra(AlarmClock.EXTRA_HOUR, hour)
                 clockIntent.putExtra(AlarmClock.EXTRA_MINUTES, (minute-2))
                 clockIntent.putExtra(AlarmClock.EXTRA_SKIP_UI, true)
                 startActivity(clockIntent)

                 /*

                      alarmManager.setExactAndAllowWhileIdle(
                          AlarmManager.RTC_WAKEUP,
                          priorAlarm,
                          pendingIntent
                      )

                  */

                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmStartTime, pendingIntent)
                Toast.makeText(this, "Done!", Toast.LENGTH_SHORT).show()
            }

            R.id.setBtn -> {
                alarmManager.cancel(pendingIntent)
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()

            }
        }
    }
}