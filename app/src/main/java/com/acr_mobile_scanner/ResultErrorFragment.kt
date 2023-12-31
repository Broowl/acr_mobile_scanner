package com.acr_mobile_scanner

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.acr_mobile_scanner.databinding.FragmentResultErrorBinding


class ResultErrorFragment : Fragment() {

    private var _binding: FragmentResultErrorBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentResultErrorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFragmentResultListener("scan_result") { _, bundle ->
            val result = bundle.getString("message")
            if (result != null){
                _binding?.textViewResultErrorMessage?.text = result
            }
        }

        binding.resultErrorOkButton.setOnClickListener {
            findNavController().navigate(R.id.action_ResultErrorFragment_to_ScannerFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}