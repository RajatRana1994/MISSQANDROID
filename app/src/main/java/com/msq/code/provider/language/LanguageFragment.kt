package com.msq.code.provider.language

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.msq.BaseFragment
import com.msq.R
import com.msq.code.MainActivity
import com.msq.util.LocaleHelper
import com.msq.util.SessionManager
import kotlinx.android.synthetic.main.fragment_language_pro.*


class LanguageFragment : BaseFragment(), View.OnClickListener {
    lateinit var constraintLayout: ConstraintLayout
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_language_pro, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        constraintLayout = view.findViewById(R.id.langConstraint)
        onClick()
        showSavedSelection()
    }

    private fun showSavedSelection() {
        selecctedLangView(pref.getPrefString(SessionManager.LANGUAGE)
            ?: "en").let {v->
            cbENGLISH.isChecked = v == cbENGLISH
            cbSPANISH.isChecked = v == cbSPANISH
            cbCHINESE.isChecked = v == cbCHINESE
            cbINDIAN.isChecked = v == cbINDIAN
            cbARABIC.isChecked = v == cbARABIC
            cbFRENCH.isChecked = v == cbFRENCH
            updateBgContraints(v.id)
        }
    }

    private fun onClick() {

        ivBack.setOnClickListener(this)
        selectedBg.setOnClickListener(this)
        cbENGLISH.setOnClickListener(this)
        cbSPANISH.setOnClickListener(this)
        cbCHINESE.setOnClickListener(this)
        cbINDIAN.setOnClickListener(this)
        cbARABIC.setOnClickListener(this)
        cbFRENCH.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v!!) {
            ivBack -> {
                requireActivity().onBackPressed()
            }
            cbENGLISH, cbSPANISH, cbCHINESE, cbINDIAN, cbARABIC, cbFRENCH -> {
                cbENGLISH.isChecked = v == cbENGLISH
                cbSPANISH.isChecked = v == cbSPANISH
                cbCHINESE.isChecked = v == cbCHINESE
                cbINDIAN.isChecked = v == cbINDIAN
                cbARABIC.isChecked = v == cbARABIC
                cbFRENCH.isChecked = v == cbFRENCH
                updateBgContraints(v.id)
                updateLanguage(getLangLocale(v))

            }
        }
    }

    private fun getLangLocale(v: View): String =
        when (v) {
            cbENGLISH -> "en"
            cbSPANISH -> "es"
            cbCHINESE -> "zh"
            cbINDIAN -> "en"
            cbARABIC -> "en"
            cbFRENCH -> "fr"
            else -> "en"
        }

    private fun selecctedLangView(lang: String): View =
        when (lang) {
            "en" -> cbENGLISH
            "es" -> cbSPANISH
            "zh" -> cbCHINESE
            "fr" -> cbFRENCH
            else -> cbENGLISH
        }

    private fun updateLanguage(lang: String) {
        LocaleHelper.setLocale(requireActivity(), lang);
        pref.savePrefString(SessionManager.LANGUAGE, lang)
        startActivity(Intent(requireActivity(),MainActivity::class.java))
        requireActivity().finish()
        requireActivity().overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
    }

    private fun updateBgContraints(id: Int) {
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)
        constraintSet.connect(selectedBg.id,
            ConstraintSet.TOP,
            id,
            ConstraintSet.TOP,
            0)
        constraintSet.connect(selectedBg.id,
            ConstraintSet.BOTTOM,
            id,
            ConstraintSet.BOTTOM,
            0)
        constraintSet.applyTo(constraintLayout)
    }

}