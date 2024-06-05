package id.pradio.pokeapp.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import id.pradio.pokeapp.databinding.LayoutBaseBottomSheetBinding

class SetNicknameDialog(
    private val defaultName: String
): BottomSheetDialogFragment() {

    var binding: LayoutBaseBottomSheetBinding? = null
    var buttonSaveAction: ((name: String) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutBaseBottomSheetBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            etName.setText(defaultName)
            btnSave.setOnClickListener {
                val name = this.etName.text.toString()
                if (name.isNotBlank()) {
                    buttonSaveAction?.invoke(name)
                    dismiss()
                }
            }
        }
    }
}