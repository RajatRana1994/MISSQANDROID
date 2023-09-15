package com.msq


import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.msq.UtilFiles.LocationLiveData
import com.msq.base.MSQApp
import com.msq.util.CheckPermission
import com.msq.util.SessionManager
import java.lang.NullPointerException

abstract class BaseFragment : Fragment() {

    var baseActivity: BaseActivity? = null
    val pref by lazy { MSQApp.session(requireActivity()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BaseActivity)
            baseActivity = context
    }

    public fun showFragment(tag: String, bundle: Bundle? = null) {
        try {
            baseActivity!!.showFragment(tag, bundle)
        }catch (e:NullPointerException){}
    }

    override fun onDetach() {
        baseActivity = null
        super.onDetach()
    }

    override fun onResume() {
        super.onResume()
    }

}
