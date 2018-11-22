package capstone.abang.com.Car_Owner.Profile;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;

import capstone.abang.com.R;
import capstone.abang.com.Register.LoginActivity;

/**
 * Created by Pc-user on 16/01/2018.
 */

public class SignOutFragment extends Fragment {
    //widgets
    private Button btnLogout;

    //tag
    private final static String TAG ="SignOutFragment";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_out, container,false);
        //cast widgets
        btnLogout = view.findViewById(R.id.btnsignout);
        //firebase
        mAuth = FirebaseAuth.getInstance();
//        mAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//                if(user != null) {
//                    //user is signed in
//                    Log.d(TAG,"onAuthStateChanged:signed_in" + user.getUid());
//                } else {
//                    //user is signed out
//                    Log.d(TAG,"onAuthStateChanged:signed_out");
//                    Intent intent = new Intent(getActivity(), LoginActivity.class);
//                    startActivity(intent);
//                }
//            }
//        };

        //setup toolbar
        setupToolbar(view);

        //methods
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return view;
    }

    private void setupToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.signoutfragmenttoolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ImageView backImageView = view.findViewById(R.id.backarrow);
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
                Intent intent = new Intent(getActivity(), AccountSettingsActivity.class);
                startActivity(intent);
            }
        });
    }
}
