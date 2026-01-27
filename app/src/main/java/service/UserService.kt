package service

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

object UserService {

    private const val USERS_COLLECTION = "users"
    private val db by lazy { FirebaseFirestore.getInstance() }

    fun createProfile(uid: String, email: String, username: String, onOk: () -> Unit, onFail: (Exception) -> Unit) {
        val data = hashMapOf(
            "uid" to uid,
            "username" to username,
            "email" to email,
            "photoUrl" to "",
            "createdAt" to FieldValue.serverTimestamp()
        )

        db.collection(USERS_COLLECTION)
            .document(uid)
            .set(data, SetOptions.merge())
            .addOnSuccessListener { onOk() }
            .addOnFailureListener { e -> onFail(e) }
    }
}
