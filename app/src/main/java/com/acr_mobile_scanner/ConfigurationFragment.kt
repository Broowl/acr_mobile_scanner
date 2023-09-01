package com.acr_mobile_scanner

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.acr_mobile_scanner.databinding.FragmentConfigurationBinding

class ConfigurationFragment : Fragment() {

    private var _binding: FragmentConfigurationBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val _viewModel: EntityViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentConfigurationBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val eventDate = SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().time)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val publicKey = sharedPreferences.getString("public_key", "")
        binding.editTextEventDate.text.append(eventDate)
        binding.editTextPublicKey.text.append(publicKey)

        binding.configurationOkButton.setOnClickListener {
            val eventName = binding.editTextEventName.text.toString()
            val eventDate = binding.editTextEventDate.text.toString()
            val publicKey = binding.editTextPublicKey.text.toString()
            sharedPreferences.edit().putString("public_key", publicKey).apply()
            _viewModel.initializeScanner(eventName, strToDate(eventDate), publicKey)
            findNavController().navigate(R.id.action_ConfigurationFragment_to_ScannerFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}