import android.text.format.DateFormat
import com.google.firebase.database.ServerValue
import java.text.SimpleDateFormat
import java.util.*

class UserModel {
    var key: String? = null
    var name: String? = null
    var mobile: String? = null
    var timestamp: MutableMap<String,String> = ServerValue.TIMESTAMP

}
class GetUserModel {
    var key: String? = null
    var name: String? = null
    var timestamp: Long? = null
    var mobile: String? = null
    fun getdateasformatted(date: Long): String {

        val formatter = SimpleDateFormat("dd-mm-yyyy")
        val cal = Calendar.getInstance(Locale.ENGLISH)
        cal.timeInMillis = date
        val fdate: String = DateFormat.format("hh:mm:aa dd-MMM-yyyy ", cal).toString()
        return fdate
    }
}