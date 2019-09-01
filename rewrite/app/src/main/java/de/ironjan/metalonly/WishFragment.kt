package de.ironjan.metalonly

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.ironjan.metalonly.R

/**
 * A simple [Fragment] subclass.
 * Use the [WishFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WishFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wish, container, false)
    }

}
