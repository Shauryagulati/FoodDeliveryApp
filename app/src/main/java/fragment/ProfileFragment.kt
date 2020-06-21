package fragment

import android.content.Context
import android.content.SharedPreferences
import android.location.Address
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.codiinggeek.food.R
import kotlinx.android.synthetic.main.fragment_profile.view.*

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {

    lateinit var sharedPreferences: SharedPreferences
    lateinit var txtUserName: TextView
    lateinit var txtPhone: TextView
    lateinit var txtEmail: TextView
    lateinit var txtAddress: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        txtUserName = view.findViewById(R.id.txtUserName)
        txtPhone = view.findViewById(R.id.txtPhone)
        txtEmail = view.findViewById(R.id.txtEmail)
        txtAddress = view.findViewById(R.id.txtAddress)

        sharedPreferences =
            this.activity!!.getSharedPreferences("Preference", Context.MODE_PRIVATE)
        val name = sharedPreferences.getString("Name","")
        val mobile = sharedPreferences.getString("Mobile","")
        val email = sharedPreferences.getString("Email","")
        val address = sharedPreferences.getString("Address","")

        txtUserName.text = name
        txtPhone.text = mobile
        txtEmail.text = email
        txtAddress.text = address

        return view
    }

}
