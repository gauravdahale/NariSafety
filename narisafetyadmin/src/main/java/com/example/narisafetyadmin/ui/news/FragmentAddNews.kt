package com.example.narisafetyadmin.ui.news

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.narisafetyadmin.R
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class FragmentAddNews : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    val SELECT_PICTURES: Int = 101
    lateinit var mStorageRef: StorageReference
    lateinit var mSharedPreferences: SharedPreferences
    val imagemap = HashMap<String, Uri>()
    var imagesarray = ArrayList<String>()

    val dhavalimages = ArrayList<Uri>()
    val currentuser = FirebaseAuth.getInstance().currentUser?.uid.toString()
    var imagepicked = false
    lateinit var mImageview: ImageView
    lateinit var mHeading: TextInputEditText
    lateinit var mDescription: TextInputEditText
    lateinit var mSubmit: MaterialButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mStorageRef = FirebaseStorage.getInstance().reference.child("news")
        mImageview =view.findViewById(R.id.image)
        mDescription =view.findViewById(R.id.description)
        mHeading =view.findViewById(R.id.heading)
        mSubmit = view.findViewById(R.id.submit)
        mImageview.setOnClickListener { pickImageDhaval(SELECT_PICTURES) }
        mSubmit.setOnClickListener {
            if(mHeading.text.toString().isNotBlank()&& mDescription.text.toString().isNotBlank()) {
                val model = AddNewsModel().apply {
                    heading = mHeading.text.toString()
                    description = mDescription.text.toString()

                }
                val pd = ProgressDialog(requireContext())
                pd.setMessage("Processing please wait..")
                pd.show()
                if (dhavalimages.size != 0) logComplaintWithImage(model, pd)
                else logComplaint(pd,model)
            }
        }
    }

    private fun logComplaint(pd: ProgressDialog, model: AddNewsModel) {
        val key = FirebaseDatabase.getInstance().reference.child("complaints/$currentuser").push().key
        val mHashMap = HashMap<String, Any>()
        mHashMap["complaints/$key"] = model
        mHashMap["usercomplaints/$currentuser/$key"] = model
        Toast.makeText(requireContext(), "Uploading Now", Toast.LENGTH_SHORT).show()
        FirebaseDatabase.getInstance().reference.updateChildren(mHashMap)
            .addOnSuccessListener {
                Toast.makeText(
                    requireContext(),
                    "Complaint submitted successfully",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("Upload", " in  firestore add on firestore listener")

                pd.dismiss()

            }
            .addOnSuccessListener {
                pd.dismiss()
                requireActivity().onBackPressed()
            }
    }

    private fun pickImageDhaval(code: Int) {
        ImagePicker.with(this)
            .galleryMimeTypes(
                mimeTypes = arrayOf(
                    "image/png",
                    "image/jpg",
                    "image/jpeg"
                )
            )
            .crop(2.0f,1.0f)//Crop image(Optional), Check Customization for more option
            .compress(1024)            //Final image size will be less than 1 MB(Optional)
            .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
            .start(code)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, result: Intent?) {
        // The last parameter value of shouldHandleResult() is the value we pass to setRequestCode().
        // If we do not call setRequestCode(), we can ignore the last parameter.
        super.onActivityResult(
            requestCode,
            resultCode,
            result
        )   // This line is REQUIRED in fragment mode

        if (requestCode == SELECT_PICTURES) {
            if (resultCode == Activity.RESULT_OK) {
                dhavalimages.clear()
                val fileUri = result?.data
                dhavalimages.add(fileUri!!)
                imagemap["0"] = fileUri!!
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    imagepicked = true
                    Glide.with(this)
                        .load(fileUri)
                        .into(mImageview)
                } else {
                    Glide.with(this)
                        .load(fileUri)
                        .into(mImageview)
                    imagepicked = true

                }

            }
        }
        else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(requireContext(), ImagePicker.getError(result), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
        }

    }
    private fun logComplaintWithImage(
        model: AddNewsModel,
        pd: ProgressDialog
    ) {
        dhavalimages.clear()
        imagemap.values.forEach { uri ->
            dhavalimages.add(uri)
        }
        dhavalimages.forEachIndexed { it, url ->
            Log.d("Upload", "Image${url.lastPathSegment}")
            val ref = mStorageRef?.child("news/${url.lastPathSegment}")
            Log.d("Upload", "in images loop")
            //                        var imagepath = it.path
            val uploadTask = ref!!.putFile(dhavalimages[it])
            val urlTask = uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    Log.d("Upload", " in  uploadtask with continue loop")
                    task.exception?.let {
                        throw it
                        Log.d("uploadException", "${it.message.toString()}")
                        Log.d("Upload", " Exception in upload task")

                    }

                }
                ref.downloadUrl
            }
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result
                        imagesarray.add(downloadUri.toString())
                        Log.d(
                            "Upload",
                            " addOnComplete listener  : ${downloadUri.toString()}"
                        )
                    } else {
                        Log.e("UPLOADERROR", "UPLOAD FAILED")
                    }
                }
            urlTask.addOnSuccessListener {
                //                    urlTask.addOnSuccessListener {
                model.image = imagesarray[0]
                if (imagesarray.size == dhavalimages.size) {
                    val key = FirebaseDatabase.getInstance().reference.child("news").push().key
                    val mHashMap = HashMap<String, Any>()
                    mHashMap["news/$key"] = model
//                    mHashMap["usercomplaints/$currentuser/$key"] = model
                    Toast.makeText(requireContext(), "Uploading Now", Toast.LENGTH_SHORT).show()
                    FirebaseDatabase.getInstance().reference.updateChildren(mHashMap)
                        .addOnSuccessListener {
                            Toast.makeText(
                                requireContext(),
                                "Complaint submitted successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.d("Upload", " in  firestore add on firestore listener")

                            pd.dismiss()

                        }
                        .addOnSuccessListener {
                            pd.dismiss()
                            requireActivity().onBackPressed()
                        }
                        .addOnFailureListener {
                            //                                        dialog.dismiss()
                            Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
                            pd.dismiss()
                        }

                } else {
                    pd.dismiss()
                }
            }
        }
    }
    }

