package com.example.saveup.ui.statistics

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.saveup.MainViewModel
import com.example.saveup.databinding.FragmentLimitsBinding
import com.example.saveup.model.Account
import com.example.saveup.model.Notifications
import java.util.Locale

class LimitsFragment : Fragment() {
    private var _binding: FragmentLimitsBinding? = null
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

    /*
     * Sabes (o deberías saber) lo que es un canal. Al menos, a nivel usuario:
     * https://developer.android.com/static/images/ui/notifications/channel-settings_2x.png
     */
    fun createNotificationChannel() {
        //Si API 26 o superior.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            val idCanal = SIMPLE_CHANNEL
            val nombreCanal = "El nombre del canal"
            val importancia = NotificationManager.IMPORTANCE_DEFAULT
            val canal = NotificationChannel(idCanal,nombreCanal,importancia)

            //Añadimos el canal al servicio de notificaciones.
            val notificationManager = activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(canal)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLimitsBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        if (!viewModel!!.monthlyLimit.isInitialized) {
            viewModel!!.getLimit()
        }

        initializeVariables()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel?.monthlyLimit?.observe(viewLifecycleOwner) {
            setLimit(it)
        }
    }

    private fun initializeVariables() {
        binding.saveFab.setOnClickListener {
            // TODO: validar límite
            val newLimit = binding.etLimit.text.toString()
            if (newLimit.isNotBlank()) {
                val limit = newLimit.replace(',', '.').toDouble()
                if (limit > 0.01) {
                    viewModel?.updateLimit(limit)

                    notifyNewLimit(limit)
                }
            }
        }
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
            com.google.android.material.R.drawable.navigation_empty_icon)
    }

    companion object {
        private const val ACCOUNT = "Account"

        const val  SIMPLE_CHANNEL = "Canal simple"
        const val SIMPLE_NOTIFICATION_ID = 1

        @JvmStatic
        fun newInstance(account: Account?): LimitsFragment {
            val fragment = LimitsFragment()
            val args = Bundle()
            args.putParcelable(ACCOUNT, account)
            fragment.arguments = args
            return fragment
        }
    }
}