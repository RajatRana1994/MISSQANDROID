package com.msq.code.customer.servicebook

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.buuzconnect.uis.addevent.ServiceBookViewModel
import com.msq.BaseFragment
import com.msq.R
import com.msq.UtilFiles.LocationLiveData
import com.msq.base.FragConst
import com.msq.code.ImageVPagerAdapter
import com.msq.rest.entity.MSQService
import com.msq.rest.entity.NearServiceResult
import com.msq.util.CheckPermission
import com.msq.util.SessionManager
import com.msq.util.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_service_book.*


class ServiceBookFragment : BaseFragment(), View.OnClickListener {
    lateinit var viewModel: ServiceBookViewModel
    var page = 0
    lateinit var handler: Handler
    lateinit var runnable: Runnable
    var selectedService: MSQService? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_service_book, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handler = Handler()
        runnable = Runnable {
            vpBanner.currentItem =
                if (vpBanner.currentItem == vpBanner.adapter?.count!! - 1) 0 else vpBanner.currentItem + 1
            handler.removeCallbacks(runnable)
            handler.postDelayed(runnable, 1500)
        }
        viewModel =
            ViewModelProviders.of(this, ViewModelFactory.getInstance(requireActivity()))
                .get(ServiceBookViewModel::class.java).also {
//                    it.getNearServices("${++page}",
//                        pref.getPrefString(SessionManager.LAT) ?: "0",
//                        pref.getPrefString(SessionManager.LNG) ?: "0")
                    it.getServices()
                }
        if (pref.getPrefString(SessionManager.LAT).isNullOrEmpty()){
            CheckPermission(requireActivity()).locationPermission {
                // Check if location settings are available
                LocationLiveData().init(requireActivity()).observe(viewLifecycleOwner) {
                    it?.let {
                        pref.savePrefString(SessionManager.LAT, "${it.latitude}")
                        pref.savePrefString(SessionManager.LNG, "${it.longitude}")
                    }
                }
            }
        }
        observers()
        onClicks()
        initVPager()
    }

    private fun observers() {
        viewModel.mServiceListEntity.observe(viewLifecycleOwner) {
            rvServices.adapter = ServiceBookAdapter(it.data.result) { t, p ->
                showFragment(FragConst.CHECKOUT, bundleOf("msq_service" to it.data.result[p]))
//                selectedService = it.data.result[p]
//                page=0
//                val lat=pref.getPrefString(SessionManager.LAT) ?: "0"
//                val long=pref.getPrefString(SessionManager.LNG) ?: "0"
//                viewModel.getNearServices("${++page}", lat, long)
            }
        }
        viewModel.mNearServiceEntity.observe(viewLifecycleOwner) {
            val providers = it.data.result.filter { it.name == selectedService?.name ?: "" }
            showProviderListDialog(providers as ArrayList<NearServiceResult>)
        }
    }

    private fun showProviderListDialog(data: ArrayList<NearServiceResult>) {
        val dialog = Dialog(requireActivity(), R.style.AppTheme_FullScreenDialog)
        dialog.setContentView(R.layout.dialog_provider_list)
        dialog.setCancelable(true)
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        dialog.findViewById<RecyclerView>(R.id.rvProvider).adapter =
            ServiceProviderAdapter(data) { t, p ->
                dialog.dismiss()
                showFragment(FragConst.CHECKOUT, bundleOf("msq_service" to data[p]))
            }
        dialog.findViewById<ImageView>(R.id.ivBack).setOnClickListener { dialog.dismiss() }
        dialog.show()
    }


    private fun initVPager() {
        arrayListOf<Pair<Int, String>>().apply {
            add(Pair(R.drawable.one, "3"))
            add(Pair(R.drawable.two, "3"))
            add(Pair(R.drawable.three, "3"))
            add(Pair(R.drawable.four, "3"))
            add(Pair(R.drawable.five, "2"))
            add(Pair(R.drawable.six, "2"))
            add(Pair(R.drawable.seven, "2"))
            add(Pair(R.drawable.eight, "2"))
            add(Pair(R.drawable.nine, "1"))
            add(Pair(R.drawable.ten, "1"))
            vpBanner.adapter = ImageVPagerAdapter(this) {}
//            vPager.pageMargin = (resources.displayMetrics.widthPixels * -0.10).toInt()
        }
        handler.removeCallbacks(runnable)
        handler.postDelayed(runnable, 1500)

    }

    private fun onClicks() {
        ivHome.setOnClickListener { showFragment(FragConst.HOME) }
    }

    override fun onClick(v: View?) {
    }


    override fun onResume() {
        super.onResume()
        if (vpBanner.adapter != null) {
            handler.removeCallbacks(runnable)
            handler.postDelayed(runnable, 1500)
        }
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(runnable)
    }
}