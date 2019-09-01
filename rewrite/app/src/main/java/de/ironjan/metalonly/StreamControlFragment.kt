package de.ironjan.metalonly

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


/**
 * A simple [Fragment] subclass.
 * Use the [StreamControlFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StreamControlFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(de.ironjan.metalonly.R.layout.fragment_stream, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            StreamControlFragment().apply {
            }
    }
}
