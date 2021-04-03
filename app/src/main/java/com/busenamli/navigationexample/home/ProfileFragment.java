package com.busenamli.navigationexample.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.busenamli.navigationexample.ProfileFragmentDirections;
import com.busenamli.navigationexample.R;
import com.busenamli.navigationexample.login.LoginActivity;
import com.busenamli.navigationexample.model.User;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Map;

public class ProfileFragment extends Fragment {

   ShapeableImageView profilePhoto;
   TextView nameText, cityText, descriptionText;
   Button signoutButton, editProfileButton;
   ProgressBar progressbar;
   private FirebaseAuth firebaseAuth;
   private FirebaseFirestore firebaseFirestore;
   User user;

    public ProfileFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        user = new User();
        nameText = view.findViewById(R.id.profile_name_text);
        cityText = view.findViewById(R.id.profile_city_text);
        descriptionText = view.findViewById(R.id.profile_description_text);
        profilePhoto = view.findViewById(R.id.profile_photo);

        progressbar = view.findViewById(R.id.progressbar);
        progressbar.setVisibility(View.VISIBLE);

        signoutButton = view.findViewById(R.id.signout_button);
        editProfileButton = view.findViewById(R.id.edit_profile_button);

        readDataFromFirestore();

        signoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fbSignout();
            }
        });

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileFragmentDirections.ActionProfileFragmentToEditProfileFragment action = ProfileFragmentDirections.actionProfileFragmentToEditProfileFragment(
                        user.getName(),user.getCity(),user.getDescription(),user.getImage());
                Navigation.findNavController(view).navigate(action);
            }
        });
    }

    public void initFBAuthentication(){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public void fbSignout(){
        firebaseAuth.signOut();

        Intent intentToLogin = new Intent(ProfileFragment.this.getActivity(), LoginActivity.class);
        startActivity(intentToLogin);
        getActivity().finish();
    }

    public User readDataFromFirestore(){

        DocumentReference documentReference = firebaseFirestore.collection("Users").document(firebaseAuth.getCurrentUser().getUid());
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error!=null){
                    Toast.makeText(ProfileFragment.this.getActivity(), error.getLocalizedMessage().toString(),Toast.LENGTH_SHORT).show();
                }

                if (value!=null) {

                    Map<String, Object> data = value.getData();
                    String name = (String) data.get("user_name");
                    String description = (String) data.get("user_description");
                    String city = (String) data.get("user_city");
                    String image = (String) data.get("profile_photo_url");

                    user = new User(name,city,description,image);

                    nameText.setText(name);
                    descriptionText.setText(description);
                    cityText.setText(city);
                    if (image!="") {
                        Picasso.get().load(image).fit().centerCrop().error(R.mipmap.ic_launcher_pp).into(profilePhoto, new Callback() {
                            @Override
                            public void onSuccess() {

                                progressbar.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });
                    }
                    else{
                        progressbar.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
        return user;
    }
}