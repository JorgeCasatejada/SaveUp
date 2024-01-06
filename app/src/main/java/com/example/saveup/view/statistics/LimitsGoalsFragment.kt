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
import com.example.saveup.databinding.FragmentLimitsGoalsBinding
import com.example.saveup.model.Account
import com.example.saveup.model.Notifications
import com.example.saveup.model.firestore.FireGoal
import com.example.saveup.view.MainActivity
import com.example.saveup.viewModel.MainViewModel
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = SIMPLE_CHANNEL
            val channelName = "Canal de notificaciones de SaveUp"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance)

            // Añadimos el canal al servicio de notificaciones.
            val notificationManager =
                activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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
        // Desactivar campos de texto
        binding.etLimit.isEnabled = false
        binding.etGoalName.isEnabled = false
        binding.etGoalDate.isEnabled = false
        binding.etGoalValue.isEnabled = false
        binding.btSwitchInitialBalance.isEnabled = false

        // Editar límite
        binding.editButtonLimits.setOnClickListener {
            toggleEditionLimit(true)
        }

        // Eliminar límite
        binding.removeButtonLimits.setOnClickListener {
            deleteLimit()

            setLimit(null)
        }

        // Aceptar límite
        binding.buttonAcceptLimit.setOnClickListener {
            toggleEditionLimit(false)

            createLimit()

            Notifications.checkLimit(requireActivity(), viewModel!!)
        }

        // Cancelar límite
        binding.buttonCancelLimit.setOnClickListener {
            toggleEditionLimit(false)

            resetLimit()
        }

        // Editar meta
        binding.editButtonGoals.setOnClickListener {
            toggleEditionGoal(true)
        }

        // Eliminar meta
        binding.removeButtonGoals.setOnClickListener {
            deleteGoal()

            setGoal(null)
        }

        // Aceptar meta
        binding.buttonAcceptGoal.setOnClickListener {
            toggleEditionGoal(false)

            createGoal()

            Notifications.checkGoal(requireActivity(), viewModel!!)
        }

        // Cancelar meta
        binding.buttonCancelGoal.setOnClickListener {
            toggleEditionGoal(false)

            resetGoal()
        }

        // Nombre meta
        binding.etGoalName.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.outlinedTextFieldGoalName.error = null
            } else {
                if (binding.etGoalName.text.isNullOrBlank()) {
                    binding.outlinedTextFieldGoalName.error =
                        resources.getString(R.string.errBlankGoalName)
                } else {
                    binding.outlinedTextFieldGoalName.error = null
                }
            }
        }

        // Fecha meta
        binding.etGoalDate.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.datePickerLayoutGoal.error = null
            } else {
                if (validateDate(binding.datePickerLayoutGoal.editText?.text.toString())) {
                    binding.datePickerLayoutGoal.error = null
                } else {
                    binding.datePickerLayoutGoal.error = resources.getString(R.string.errDate)
                }
            }
        }

        // Valor meta
        binding.etGoalValue.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.outlinedTextFieldGoalValue.error = null
            } else {
                if (binding.etGoalValue.text.isNullOrBlank()) {
                    binding.outlinedTextFieldGoalValue.error =
                        resources.getString(R.string.errBlankGoalValue)
                } else {
                    binding.outlinedTextFieldGoalValue.error = null
                }
            }
        }

        // Estado Switch
        if (viewModel?.goal?.value != null) {
            binding.btSwitchInitialBalance.isChecked =
                viewModel?.goal?.value!!.initialBalance <= 0.0
        }

        // Texto Switch
        binding.btSwitchInitialBalance.text = resources.getString(
            R.string.infoInitialBalance,
            String.format(
                Locale.getDefault(),
                "%.2f",
                viewModel?.balance?.value!!
            )
        )
    }

    private fun toggleEditionLimit(editing: Boolean) {
        binding.etLimit.isEnabled = editing

        binding.removeButtonLimits.isEnabled = !editing

        if (editing) {
            binding.linearLayoutLimitsButtons.visibility = View.VISIBLE
        } else {
            binding.linearLayoutLimitsButtons.visibility = View.GONE
        }
    }

    private fun toggleEditionGoal(editing: Boolean) {
        binding.etGoalName.isEnabled = editing
        binding.etGoalDate.isEnabled = editing
        binding.etGoalValue.isEnabled = editing
        binding.btSwitchInitialBalance.isEnabled = editing

        binding.removeButtonGoals.isEnabled = !editing

        if (editing) {
            binding.linearLayoutGoalsButtons.visibility = View.VISIBLE
        } else {
            binding.linearLayoutGoalsButtons.visibility = View.GONE
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

        Notifications.checkLimit(requireActivity(), viewModel!!)
    }

    private fun deleteLimit() {
        viewModel?.deleteLimit()

        binding.etLimit.setText("")
    }

    private fun createGoal() {
        val newGoalName = binding.etGoalName.text.toString()
        val newGoalDate = binding.etGoalDate.text.toString()
        val newGoalValue = binding.etGoalValue.text.toString()

        var date: Date? = null
        var value = 0.0

        var valid = true

        // Nombre
        if (newGoalName.isBlank()) {
            binding.outlinedTextFieldGoalName.error = resources.getString(R.string.errBlankGoalName)
            valid = false
        }

        // Fecha
        if (newGoalDate.isBlank()) {
            binding.outlinedTextFieldGoalName.error = resources.getString(R.string.errBlankGoalDate)
            valid = false
        } else {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.isLenient = false
            try {
                date = sdf.parse(newGoalDate)

                if (date == null || date.year >= Date().year + 1000) {
                    binding.datePickerLayoutGoal.error = resources.getString(R.string.errDate)
                    valid = false
                }
            } catch (e: ParseException) {
                binding.datePickerLayoutGoal.error = resources.getString(R.string.errDate)
                valid = false
            }
        }

        // Valor
        if (newGoalValue.isBlank()) {
            binding.outlinedTextFieldGoalValue.error =
                resources.getString(R.string.errBlankGoalValue)
            valid = false
        } else {
            value = newGoalValue.replace(',', '.').toDouble()
            if (value < 0.01) {
                binding.outlinedTextFieldGoalValue.error = resources.getString(R.string.errLowGoal)
            }
        }

        // Creación de la meta
        if (valid) {
            var initialBalance = viewModel?.balance?.value!!

            if (binding.btSwitchInitialBalance.isChecked) {
                initialBalance = 0.0
            }

            val goal = FireGoal(
                newGoalName,
                Date(),
                date,
                initialBalance,
                value
            )

            viewModel?.updateGoal(goal)

            notifyNewGoal(goal.name, goal.finalDate, goal.objectiveBalance)
        }
    }

    private fun deleteGoal() {
        viewModel?.deleteGoal()

        binding.etGoalName.setText("")
        binding.etGoalDate.setText("")
        binding.etGoalValue.setText("")
    }

    private fun validateDate(date: String): Boolean {
        if (date.isBlank()) return true
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        sdf.isLenient = false
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
            binding.progressBarLimit.progress = 0
            binding.textRemainingBalanceLimit.text = ""
        } else {
            binding.etLimit.setText(String.format(Locale.getDefault(), "%.2f", limit))

            val expenses = viewModel?.getMonthlyExpenses()
            if (expenses != null) {
                val progress = expenses / limit * 100
                binding.progressBarLimit.setProgress(progress.toInt(), true)
            }

            val remainingBalance = limit - viewModel?.getMonthlyExpenses()!!
            if (remainingBalance >= 0) {
                binding.textRemainingBalanceLimit.text = resources.getString(
                    R.string.ramainingBalanceLimit,
                    String.format(Locale.getDefault(), "%.2f", remainingBalance)
                )
            }
        }
    }

    private fun notifyNewLimit(limit: Double) {
        Notifications.simpleNotification(
            requireActivity(),
            "Nuevo Límite Mensual Establecido",
            "Se ha actualizado el límite mensual\nDurante este mes el límite es de $limit €",
            com.google.android.material.R.drawable.navigation_empty_icon,
            LIMIT_STABLISHED_NOTIFICATION_ID
        )
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
        Notifications.simpleNotification(
            requireActivity(),
            "Nueva Meta Establecida",
            builder.toString(),
            com.google.android.material.R.drawable.navigation_empty_icon,
            GOAL_STABLISHED_NOTIFICATION_ID
        )
    }

    private fun setGoal(goal: FireGoal?) {
        if (goal == null) {
            binding.progressBarGoal.progress = 0
            binding.textRemainingBalanceGoal.text = ""
            binding.textRemainingDaysGoal.text = ""
            return
        }

        val remainingBalance = calculateRemainingBalanceGoal(goal)
        if (remainingBalance <= 0) { // Ya se ha llegado a la meta
            val excess = if (remainingBalance == 0.0) 0.0 else -1 * remainingBalance

            binding.progressBarGoal.progress = 100
            binding.textRemainingBalanceGoal.text = resources.getString(
                R.string.reachedBalanceGoal,
                String.format(Locale.getDefault(), "%.2f", excess))
            binding.textRemainingDaysGoal.text = ""
            return
        }

        binding.etGoalName.setText(goal.name)
        if (goal.finalDate == null) {
            binding.etGoalDate.setText("")
        } else {
            val stringDate =
                "${goal.finalDate.date}/${goal.finalDate.month + 1}/${goal.finalDate.year + 1900}"
            binding.etGoalDate.setText(stringDate)
        }
        if (goal.objectiveBalance == null) {
            binding.etGoalValue.setText("")
        } else {
            binding.etGoalValue.setText(goal.objectiveBalance.toString())

            val progress = calculateProgress(goal)
            if (progress >= 0) {
                val progressPercent = progress / goal.objectiveBalance * 100
                binding.progressBarGoal.setProgress(progressPercent.toInt(), true)
            }

            binding.textRemainingBalanceGoal.text = resources.getString(
                R.string.ramainingBalanceGoal,
                String.format(Locale.getDefault(), "%.2f", remainingBalance)
            )

            if (goal.finalDate != null) {
                if (goal.finalDate >= Date()) {
                    val remainingDays = (goal.finalDate.time - Date().time) / 1000 / 60 / 60 / 24
                    binding.textRemainingDaysGoal.text = resources.getString(
                        R.string.ramainingDaysGoal, remainingDays
                    )
                }
            }
        }
    }

    private fun resetLimit() {
        viewModel?.monthlyLimit?.value?.let { setLimit(it) }
    }

    private fun resetGoal() {
        viewModel?.goal?.value?.let { setGoal(it) }
    }

    private fun calculateProgress(goal: FireGoal): Double {
        return viewModel?.balance?.value!! - goal.initialBalance
    }

    private fun calculateRemainingBalanceGoal(goal: FireGoal): Double {
        return goal.objectiveBalance!! - calculateProgress(goal)
    }

    companion object {
        private const val ACCOUNT = "Account"

        const val SIMPLE_CHANNEL = "Canal simple"

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