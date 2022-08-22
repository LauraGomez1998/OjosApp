package net.tesa.ojosapp.view

import android.content.*
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.NumberPicker
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import net.tesa.ojosapp.R
import net.tesa.ojosapp.databinding.ActivityMainBinding
import net.tesa.ojosapp.datasource.PracticeDatasourceImpl
import net.tesa.ojosapp.datasource.PracticeDatasourceInterface
import net.tesa.ojosapp.model.BroadcastTimerService
import net.tesa.ojosapp.model.Practice
import net.tesa.ojosapp.model.PracticeType
import net.tesa.ojosapp.util.CalendarUtil
import net.tesa.ojosapp.view.helper.AlarmHelper


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var practiceDatasource: PracticeDatasourceInterface
    private var timeSelected: Long =
        CalendarUtil().getMillsFromTimeFormat(CalendarUtil.defaultStartTimeInFormat)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view: View = binding.root
        setContentView(view)

        practiceDatasource = PracticeDatasourceImpl(applicationContext)
        loadLastPractice()

        val path = Uri.parse("android.resource://$packageName/raw/congrats")
        AlarmHelper.ringTone = RingtoneManager.getRingtone(applicationContext, path)

        setupListeners()
        renderScreen()
    }

    //region Broadcast service and lifecycle
    private val br: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val mills = intent?.extras?.get("countdown").toString()
            val time = CalendarUtil().getTimeWithCountdownFormat(mills.toLong())
            binding.timeText.text = time

            if (time == "00:00") {
                showPracticeFinished()
            }
        }
    }

    private fun showPracticeFinished() {
        val builder = AlertDialog.Builder(this)
        with(builder)
        {
            setTitle(resources.getString(R.string.app_name))
            setMessage(resources.getString(R.string.alert_practice_finished))
            setPositiveButton("Aceptar", null)
            show()
        }
    }

    private fun startServiceWithTime(timeMills: Long) {
        val intent = Intent(this, BroadcastTimerService::class.java)
        intent.putExtra("action", BroadcastTimerService.ACTION_START)
        intent.putExtra("time", timeMills)
        startService(intent)
    }

    private fun cancelService() {
        val intent = Intent(this, BroadcastTimerService::class.java)
        intent.putExtra("action", BroadcastTimerService.ACTION_CANCEL)
        startService(intent)
    }

    private fun stopService() {
        val intent = Intent(this, BroadcastTimerService::class.java)
        intent.putExtra("action", BroadcastTimerService.ACTION_STOP)
        startService(intent)
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(br, IntentFilter(BroadcastTimerService.COUNTDOWN_BR))
        renderScreen()
        if (AlarmHelper.ringTone.isPlaying) {
            binding.timeText.text = "00:00"
        }
    }

    override fun onPause() {
        unregisterReceiver(br);
        super.onPause()
    }

    override fun onStop() {
        try {
            unregisterReceiver(br)
        } catch (e: java.lang.Exception) {
            // Receiver was probably already stopped in onPause()
        }
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
    //endregion

    //region view logic
    private fun loadLastPractice() {
        // Fetch last practice - cargar minutos que faltan
        timeSelected = practiceDatasource.getLastPractice()?.practiceDurationMills
            ?: CalendarUtil().getMillsFromTimeFormat(
                CalendarUtil.defaultStartTimeInFormat
            )
        binding.timeText.text = CalendarUtil().getTimeWithCountdownFormat(timeSelected)
    }

    private fun stopPractice() {
        val practice = practiceDatasource.getLastPractice()
        if (practice != null) {
            if (practice.running) {
                stopService()
            }
        }
    }

    private fun startPractice() {
        practiceDatasource.startPractice(timeSelected)
        startServiceWithTime(timeSelected)
    }

    private fun resumePractice() {
        val practice = practiceDatasource.getLastPractice()
        if (practice != null && !practice.running) {
            practiceDatasource.resumePractice()
            startServiceWithTime(practiceDatasource.getLastPractice()?.practiceDurationMills!!)
        }
    }

    private fun cancelPractice() {
        cancelService()
        practiceDatasource.cancelPractice()
        timeSelected = CalendarUtil().getMillsFromTimeFormat(CalendarUtil.defaultStartTimeInFormat)
        binding.timeText.text = CalendarUtil().getTimeWithCountdownFormat(timeSelected)
    }
    //endregion

    //region listeners
    private val resumePracticeButtonClick = { _: DialogInterface, _: Int ->
        resumePractice()
    }

    private val cancelPractice = { _: DialogInterface, _: Int ->
        cancelPractice()
    }

    private val positiveButtonClick = { _: DialogInterface, _: Int ->
        if (checkSystemWritePermission()) {
            startPractice()
        }
    }

    private fun setupListeners() {
        binding.iconHelpPdf.setOnClickListener {
            val intent = Intent(this, PdfViewActivity::class.java)
            intent.putExtra("ViewType", "assets")
            startActivity(intent)
        }

        binding.timeText.setOnClickListener {
            renderPicker(CalendarUtil().getPickerValues())
        }

        binding.playImage.setOnClickListener {
            if (AlarmHelper.ringTone.isPlaying) {
                AlarmHelper.stopRingtone()
            } else {
                if (Practice.getPracticeType() == PracticeType.REST) {
                    showDayFreeDialog()
                } else {
                    val practice = practiceDatasource.getLastPractice()
                    if (practice == null) {
                        showStartAlertDialog()
                    } else {
                        if (!practice.running) {
                            showRestartAlertDialog()
                        } else {
                            stopPractice()
                        }
                    }
                }
            }
        }
    }
    //endregion

    //region dialogs
    private fun showDayFreeDialog() {
        val builder = AlertDialog.Builder(this)
        with(builder)
        {
            setTitle(resources.getString(R.string.app_name))
            setMessage(resources.getString(R.string.text_help_rest_practice_type))
            setPositiveButton("OK", null)
            show()
        }
    }

    private fun showRestartAlertDialog() {
        val builder = AlertDialog.Builder(this)
        with(builder)
        {
            setTitle(resources.getString(R.string.app_name))
            setMessage(resources.getString(R.string.alert_practice_running))
            setPositiveButton(
                "No hacer nada",
                DialogInterface.OnClickListener(function = resumePracticeButtonClick)
            )
            setNegativeButton("Cancelar", cancelPractice)
            show()
        }
    }

    private fun showStartAlertDialog() {
        val builder = AlertDialog.Builder(this)
        val message = when (Practice.getPracticeType()) {
            PracticeType.GREEN -> resources.getString(R.string.alert_green_practice)
            PracticeType.RED -> resources.getString(R.string.alert_red_practice)
            else -> null
        }
        with(builder)
        {
            setTitle(resources.getString(R.string.app_name))
            setMessage(message)
            setPositiveButton(
                "Comenzar",
                DialogInterface.OnClickListener(function = positiveButtonClick)
            )
            setNegativeButton("Cancelar", null)
            show()
        }
    }
    //endregion

    private fun checkSystemWritePermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(this)) return true
            else {
                showPermissionDialog()
            }
        }
        return false
    }

    private fun showPermissionDialog() {
        val builder = AlertDialog.Builder(this)
        with(builder)
        {
            setTitle(resources.getString(R.string.alert_permission_title))
            setMessage(resources.getString(R.string.alert_permission_message))
            setPositiveButton(
                "OK"
            ) { _: DialogInterface, _: Int ->
                openAndroidPermissionsMenu()
            }
            setNegativeButton("Cancelar", null)
            show()
        }
    }

    private fun openAndroidPermissionsMenu() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
            intent.data = Uri.parse("package:$packageName")
            this.startActivity(intent)
        }
    }

    private fun renderPicker(values: Array<String>) {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
        val btnSetTime = view.findViewById<Button>(R.id.button_programar)
        val picker = view.findViewById<NumberPicker>(R.id.interval_picker)

        picker.minValue = 0
        picker.maxValue = values.size - 1
        picker.value = 5
        picker.displayedValues = values
        picker.wrapSelectorWheel = false

        btnSetTime.setOnClickListener {
            dialog.dismiss()
            val practice = practiceDatasource.getLastPractice()
            if (practice != null) {
                showRestartAlertDialog()
            } else {
                timeSelected = CalendarUtil().getMillsFromTimeFormat(values[picker.value])
                binding.timeText.text = CalendarUtil().getTimeWithCountdownFormat(timeSelected)
            }
        }

        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()
    }

    private fun renderScreen() {
        when (Practice.getPracticeType()) {
            PracticeType.GREEN -> {
                setupScreenInstructions(
                    R.drawable.help_green,
                    R.drawable.oclusor_derecho,
                    true
                )
            }
            PracticeType.RED -> {
                setupScreenInstructions(
                    R.drawable.help_red,
                    R.drawable.oclusor_izquierdo,
                    true
                )
            }
            PracticeType.REST -> {
                setupScreenInstructions(
                    R.drawable.help_rest,
                    R.drawable.oclusor_izquierdo,
                    false
                )
                binding.timeText.text = "00:00"
            }
        }
    }

    private fun setupScreenInstructions(
        background: Int,
        iconOclusor: Int,
        visibility: Boolean
    ) {
        binding.viewHelp.setImageResource(background)
        binding.iconOclusor.setImageResource(iconOclusor)
        if (visibility) {
            binding.iconOclusor.visibility = View.VISIBLE
        } else {
            binding.iconOclusor.visibility = View.GONE
        }
    }
}