package fragment

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat

import com.codiinggeek.food.R

/**
 * A simple [Fragment] subclass.
 */
class LogOutFragment : Fragment() {

    lateinit var sharedPreferences: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_log_out, container, false)

        sharedPreferences =
            this.activity!!.getSharedPreferences("Preference", Context.MODE_PRIVATE)
        val dialog = AlertDialog.Builder(this.activity as Context)
        dialog.setTitle("Confirmation")
        dialog.setMessage("Are you sure you want to log out?")
        dialog.setPositiveButton("Yes") { text, listener ->
            sharedPreferences.edit().clear().apply()
            activity?.finishAffinity()
        }
        dialog.setNegativeButton("No") { text, listener ->
            activity?.onBackPressed()
        }
        dialog.create()
        dialog.show()

        return view
    }

}
