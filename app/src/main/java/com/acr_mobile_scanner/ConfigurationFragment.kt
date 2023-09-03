package com.acr_mobile_scanner

import android.icu.util.Calendar
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.acr_mobile_scanner.databinding.FragmentConfigurationBinding
import java.util.Date

class ConfigurationFragment : Fragment() {

    private var _binding: FragmentConfigurationBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val _entityViewModel: EntityViewModel by activityViewModels()
    private val _configurationViewModel: ConfigurationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentConfigurationBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        setFragmentResultListener("key_scan_result") { _, bundle ->
            val result = bundle.getString("public_key")
            val decoded = String(unquoteToBytes(result ?: ""))
            _binding?.editTextPublicKey?.text?.clear()
            _binding?.editTextPublicKey?.text?.append(decoded)
            sharedPreferences.edit().putString("public_key", decoded).apply()
        }
        val publicKey = sharedPreferences.getString("public_key", "")

        val displayName = _configurationViewModel.name ?: ""
        val displayDate = _configurationViewModel.date ?: Calendar.getInstance().time
        val advancedTicked = _configurationViewModel.advancedTicked ?: false

        binding.editTextEventName.text.append(displayName)
        binding.editTextEventDateYear.text.append(
            (displayDate.year + 1900).toString().padStart(4, '0')
        )
        binding.editTextEventDateMonth.text.append(
            (displayDate.month + 1).toString().padStart(2, '0')
        )
        binding.editTextEventDateDay.text.append((displayDate.date).toString().padStart(2, '0'))
        binding.editTextPublicKey.text.append(publicKey)
        binding.viewTextPublicKey.visibility = INVISIBLE
        binding.editTextPublicKey.visibility = INVISIBLE
        binding.scanPublicKeyButton.visibility = INVISIBLE

        binding.advancedSwitch.setOnCheckedChangeListener { _, checked ->
            val visibility = if (checked) VISIBLE else INVISIBLE
            binding.viewTextPublicKey.visibility = visibility
            binding.editTextPublicKey.visibility = visibility
            binding.scanPublicKeyButton.visibility = visibility
            _configurationViewModel.advancedTicked = checked
        }
        binding.advancedSwitch.isChecked = advancedTicked

        binding.scanPublicKeyButton.setOnClickListener {
            findNavController().navigate(R.id.action_ConfigurationFragment_to_KeyScannerFragment)
        }

        binding.configurationOkButton.setOnClickListener {
            val eventName = binding.editTextEventName.text.toString()
            val eventDate = Date(
                binding.editTextEventDateYear.text.toString().toInt() - 1900,
                binding.editTextEventDateMonth.text.toString().toInt() - 1,
                binding.editTextEventDateDay.text.toString().toInt()
            )
            val publicKey = binding.editTextPublicKey.text.toString()
            sharedPreferences.edit().putString("public_key", publicKey).apply()
            _entityViewModel.initializeScanner(eventName, eventDate, publicKey)
            _configurationViewModel.name = eventName
            _configurationViewModel.date = eventDate
            findNavController().navigate(R.id.action_ConfigurationFragment_to_ScannerFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}