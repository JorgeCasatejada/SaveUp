package com.example.saveup.view.statistics

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.saveup.R
import com.example.saveup.viewModel.MainViewModel
import com.example.saveup.databinding.FragmentLimitsGoalsBinding
import com.example.saveup.model.Account
import com.example.saveup.model.Notifications
import com.example.saveup.model.firestore.FireGoal
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LimitsGoalsFragment : Fragment() {
    private var _binding: FragmentLimitsGoalsBinding? = null
    private val binding get() = _binding!!

    private var account: Account? = null

    private var viewModel: MainViewModel? = null

    private val ACCOUNT = "Account"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            account = requireArguments().getParcelable(ACCOUNT)
        }

        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        // API 26 o superior.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            val channelId = SIMPLE_CHANNEL
            val channelName = "Canal de notificaciones de SaveUp"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance)

            // Añadimos el canal al servicio de notificaciones.
            val notificationManager = activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLimitsGoalsBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        if (!viewModel!!.monthlyLimit.isInitialized) {
            viewModel!!.getLimit()
        }

        if (!viewModel!!.goal.isInitialized) {
            viewModel!!.getCurrentGoal()
        }

        initializeVariables()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel?.monthlyLimit?.observe(viewLifecycleOwner) {
            setLimit(it)
        }

        viewModel?.goal?.observe(viewLifecycleOwner) {
            if (it != null) {
                setGoal(it)
            }
        }
    }

    private fun initializeVariables() {
        // Valor límite
        binding.etLimit.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.outlinedTextFieldLimit.error = null
        }

        // Nombre meta
        binding.etGoalName.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.outlinedTextFieldGoalName.error = null
        }

        // Fecha meta
        binding.etGoalDate.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.datePickerLayoutGoal.error = null
            } else {
                if (validateDate(binding.datePickerLayoutGoal.editText?.text.toString())) {
                    binding.datePickerLayoutGoal.error = null
                }
                else {
                    binding.datePickerLayoutGoal.error = resources.getString(R.string.errDate)
                }
            }
        }

        // Valor meta
        binding.etGoalValue.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.outlinedTextFieldGoalValue.error = null
        }

        // FAB
        binding.saveFab.setOnClickListener {
            createLimit()
            createGoal()
        }
    }

    private fun createLimit() {
        val newLimit = binding.etLimit.text.toString()
        if (newLimit.isNotBlank()) {
            val limit = newLimit.replace(',', '.').toDouble()
            if (limit > 0.01) {
                viewModel?.updateLimit(limit)

                notifyNewLimit(limit)
            } else {
                binding.outlinedTextFieldLimit.error = resources.getString(R.string.errLowLimit)
            }
        } else {
            viewModel?.updateLimit(Double.MAX_VALUE)
        }
    }

    private fun createGoal() {
        val newGoalName = binding.etGoalName.text.toString()
        val newGoalDate = binding.etGoalDate.text.toString()
        val newGoalValue = binding.etGoalValue.text.toString()

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        try {
            val date = if (newGoalDate.isBlank()) null else sdf.parse(newGoalDate)

            val value = if (newGoalValue.isBlank()) null else newGoalValue.replace(',', '.').toDouble()

            if (value != null && value < 0.01) {
                binding.outlinedTextFieldGoalName.error = resources.getString(R.string.errLowGoal)
            } else {
                viewModel?.updateGoal(FireGoal(newGoalName, Date(), date, viewModel?.balance?.value!!, value))

                notifyNewGoal(newGoalName, date, value)
            }
        } catch (e: ParseException) {
            binding.datePickerLayoutGoal.error = resources.getString(R.string.errDate)
        }
    }

    private fun validateDate(date: String): Boolean {
        if (date.isBlank()) return true
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        try {
            sdf.parse(date)
        } catch (e: ParseException) {
            return false
        }
        return true
    }

    private fun setLimit(limit: Double?) {
        if (limit == null) {
            binding.etLimit.setText("")
        } else {
            binding.etLimit.setText(String.format(Locale.getDefault(), "%.2f", limit))
        }
    }

    private fun notifyNewLimit(limit: Double) {
        Notifications.simpleNotification(requireActivity(),
            "Nuevo Límite Mensual Establecido",
            "Se ha actualizado el límite mensual\nDurante este mes el límite es de $limit €",
            com.google.android.material.R.drawable.navigation_empty_icon, LIMIT_STABLISHED_NOTIFICATION_ID)
    }

    private fun notifyNewGoal(name: String, date: Date?, value: Double?) {
        val builder = StringBuilder("Se ha actualizado la meta $name\n")
        if (value != null) {
            builder.append("La meta es de $value €")
        }
        if (date != null) {
            val dateFormatted = "${date.date}/${date.month + 1}/${date.year + 1900}"
            builder.append(" y termina el día $dateFormatted")
        }
        Notifications.simpleNotification(requireActivity(),
            "Nueva Meta Establecida",
            builder.toString(),
            com.google.android.material.R.drawable.navigation_empty_icon, GOAL_STABLISHED_NOTIFICATION_ID)
    }

    private fun setGoal(goal: FireGoal) {
        binding.etGoalName.setText(goal.name)
        if (goal.finalDate == null) {
            binding.etGoalDate.setText("")
        } else {
            val stringDate = "${goal.finalDate.date}/${goal.finalDate.month + 1}/${goal.finalDate.year + 1900}"
            binding.etGoalDate.setText(stringDate)
        }
        if (goal.objectiveBalance == null) {
            binding.etGoalValue.setText("")
        } else {
            binding.etGoalValue.setText(goal.objectiveBalance.toString())

            val progress = viewModel?.balance?.value?.div(goal.objectiveBalance)?.times(100)
            Log.d("progress", progress.toString())
            if (progress != null) {
                binding.progressBarGoal.setProgress(progress.toInt(), true)
            }

            val remainingBalance = goal.objectiveBalance - viewModel?.balance?.value!!

            binding.textRemainingBalanceGoal.text = resources.getString(
                R.string.ramainingBalanceGoal, String.format(Locale.getDefault(), "%.2f", remainingBalance))

            if (goal.finalDate != null) {
                if (goal.finalDate >= Date()) {
                    val remainingDays = (goal.finalDate.time - Date().time) / 1000 / 60 / 60 / 24
                    binding.textRemainingDaysGoal.text = resources.getString(
                        R.string.ramainingDaysGoal, remainingDays)
                }
            }
        }
    }

    companion object {
        private const val ACCOUNT = "Account"

        const val  SIMPLE_CHANNEL = "Canal simple"

        const val GOAL_REACHED_NOTIFICATION_ID = 1
        const val GOAL_NOT_REACHED_NOTIFICATION_ID = 2
        const val GOAL_STABLISHED_NOTIFICATION_ID = 3
        const val LIMIT_REACHED_NOTIFICATION_ID = 4
        const val LIMIT_STABLISHED_NOTIFICATION_ID = 5

        @JvmStatic
        fun newInstance(account: Account?): LimitsGoalsFragment {
            val fragment = LimitsGoalsFragment()
            val args = Bundle()
            args.putParcelable(ACCOUNT, account)
            fragment.arguments = args
            return fragment
        }
    }
}