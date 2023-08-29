package com.acr_mobile_scanner

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.acr_mobile_scanner.databinding.FragmentConfigurationBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class ConfigurationFragment : Fragment() {

    private var _binding: FragmentConfigurationBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel: EntityViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentConfigurationBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // todo: remove those lines
        val eventName = "test"
        val eventDate = "2023-08-27"
        val publicKey = "-----BEGIN PUBLIC KEY-----\n" +
                "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDcliT764RZu5Zl0LfGjJeIamdO\n" +
                "WExomreVs8NqbHb2ssFvpRtRZdYOrhLcNXoCggMGjBVzZp6ajdL6SHnKO7UnvTSa\n" +
                "bz/vLuTuqzfOQIhLSkHEz5/O7yPokFldk9pkAvd0pOwwZY1tQxLmQR7Gt0DqNC5K\n" +
                "PR9tEhRRLnARVw9e9wIDAQAB\n" +
                "-----END PUBLIC KEY-----"
        binding.editTextEventName.text.append(eventName)
        binding.editTextEventDate.text.append(eventDate)
        binding.editTextPublicKey.text.append(publicKey)

        binding.configurationOkButton.setOnClickListener {
            viewModel.initializeScanner(
                binding.editTextEventName.text.toString(),
                strToDate(binding.editTextEventDate.text.toString()),
                binding.editTextPublicKey.text.toString()
            )
            findNavController().navigate(R.id.action_ConfigurationFragment_to_ScannerFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}