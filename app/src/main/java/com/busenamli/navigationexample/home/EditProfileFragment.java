package com.busenamli.navigationexample.home;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.busenamli.navigationexample.EditProfileFragmentArgs;
import com.busenamli.navigationexample.EditProfileFragmentDirections;
import com.busenamli.navigationexample.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import static android.app.Activity.RESULT_OK;


public class EditProfileFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    EditText profileName, profileCity, profileDescription;
    ImageView profilePhoto;
    String name, city, description, image;
    Uri imageData;

    public EditProfileFragment() {
        // Required empty public constructor
    }
    // TODO: Rename and change types and number of parameters
    public static EditProfileFragment newInstance(String param1, String param2) {
        EditProfileFragment fragment = new EditProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFBAuthentication();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profilePhoto = view.findViewById(R.id.edit_profile_photo);
        profileName = view.findViewById(R.id.edit_profile_name_text);
        profileCity = view.findViewById(R.id.edit_profile_city_text);
        profileDescription = view.findViewById(R.id.edit_profile_description_text);

        if (getArguments()!=null){
            name = EditProfileFragmentArgs.fromBundle(getArguments()).getName();
            city = EditProfileFragmentArgs.fromBundle(getArguments()).getCity();
            description = EditProfileFragmentArgs.fromBundle(getArguments()).getDescription();
            image = EditProfileFragmentArgs.fromBundle(getArguments()).getİmage();

            profileName.setText(name);
            profileCity.setText(city);
            profileDescription.setText(description);
            if (image!="") {
                Picasso.get().load(image).fit().centerCrop().error(R.mipmap.ic_launcher_pp).into(profilePhoto);
            }
        }

        Button saveButton = view.findViewById(R.id.save_button);
        Button dontSaveButton = view.findViewById(R.id.dont_save_button);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDataFirestore();
                goFragment(view);
            }
        });

        dontSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //goFragment(new ProfileFragment());
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Yaptığınız değişikliler kaydedilmeyecek. Devam etmek istiyor musunuz?").setTitle("Değişikliler kaydedilmeyecek")
                        .setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                goFragment(view);
                            }
                        }).setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }

    public void goFragment(View view){
        NavDirections action = EditProfileFragmentDirections.actionEditProfileFragmentToProfileFragment();
        Navigation.findNavController(view).navigate(action);
    }

    public void initFBAuthentication(){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
    }

    public void selectImage(){
        //İzin yoksa
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(), new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }
        //İzin varsa
        else{
            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intentToGallery,2);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode==1){
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intentToGallery,2);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == 2 && resultCode == RESULT_OK && data != null){
            imageData = data.getData();
            Picasso.get().load(imageData).fit().centerCrop().error(R.mipmap.ic_launcher_pp).into(profilePhoto);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void updateDataFirestore(){
        if (imageData!=null) {

            //UUID uuid = UUID.randomUUID();
            String imageName = "ProfilePhotos/" + firebaseAuth.getCurrentUser().getUid() + ".jpg";

            storageReference.child(imageName).putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    StorageReference newReference = FirebaseStorage.getInstance().getReference(imageName);
                    newReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            String downloadUrl = uri.toString();
                            updateDataFromFirestore(downloadUrl);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditProfileFragment.this.getActivity(), e.getLocalizedMessage().toString(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            updateDataFromFirestore(null);
        }
    }

    private void updateDataFromFirestore(String downloadUrl) {
        HashMap<String, Object> updateData = new HashMap<>();

        updateData.put("user_name", profileName.getText().toString());
        updateData.put("user_description", profileDescription.getText().toString());
        updateData.put("user_city",profileCity.getText().toString());

        if(downloadUrl != null) {
            updateData.put("profile_photo_url", downloadUrl);
        }

        firebaseFirestore.collection("Users").document(firebaseAuth.getCurrentUser().getUid()).update(updateData);

    }
}