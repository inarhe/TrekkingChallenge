package edu.uoc.iartal.trekkingchallenge.User;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import edu.uoc.iartal.trekkingchallenge.EditProfileActivity;
import edu.uoc.iartal.trekkingchallenge.ObjectsDB.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.ObjectsDB.User;
import edu.uoc.iartal.trekkingchallenge.R;

public class UserAreaActivity extends AppCompatActivity {

    private TextView textViewUserName, textViewUserMail, textViewIdUser;
    private FirebaseAuth firebaseAuth;
    private String userMail, userPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_area);

        // Set toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.userToolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.userAreaActivity));

        // Get Firebase authentication instance
        firebaseAuth = FirebaseAuth.getInstance();

        textViewIdUser = (TextView) findViewById(R.id.textViewIdUser);
        textViewUserName = (TextView) findViewById(R.id.textViewUserName);
        textViewUserMail = (TextView) findViewById(R.id.textViewUserMail);

        if (firebaseAuth.getCurrentUser() == null) {
            // If user isn't logged, start login activity
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        } else {
            // Get database user data with current user mail
            final String mail = firebaseAuth.getCurrentUser().getEmail();
            DatabaseReference databaseUser = FirebaseDatabase.getInstance().getReference(FireBaseReferences.USER_REFERENCE);
            Query query = databaseUser.orderByChild("mailUser").equalTo(mail);

            // Query database to get user information
            query.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    textViewIdUser.setText(dataSnapshot.getValue(User.class).getIdUser());
                    textViewUserName.setText(dataSnapshot.getValue(User.class).getNameUser());
                    userMail = dataSnapshot.getValue(User.class).getMailUser();
                    textViewUserMail.setText(userMail);
                    userPassword = dataSnapshot.getValue(User.class).getPasswordUser();
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    //TO-DO
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    //TO-DO
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    //TO-DO
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //TO-DO
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.user_area_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_editProfile:
                startActivity(new Intent(getApplicationContext(), EditProfileActivity.class));
                return true;
            case R.id.action_deleteProfile:
                deleteUserAccount();
                return true;
            case R.id.action_userHistory:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void deleteUserAccount(){
        // Delete user account when delete button is clicked


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.deleteUserConfirmation));
        builder.setCancelable(true);

        builder.setPositiveButton(
                getString(R.string.acceptButton),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        AuthCredential credential = EmailAuthProvider
                                .getCredential(userMail, userPassword);
                        Log.i("USER", "onClick: " + user.getEmail());
                        if (user != null){
                            user.reauthenticate(credential)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            user.delete()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(getApplicationContext(), getString(R.string.userAccountDeleted), Toast.LENGTH_LONG).show();
                                                                finish();
                                                            }
                                                        }
                                                    });
                                        }
                                    });
                        } else {
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            finish();
                        }
                    }
                });

        builder.setNegativeButton(
                getString(R.string.cancelButton),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder.create();
        alert11.show();

    }

    public void signOut(View view) {
        // User sign out when sign out button is clicked
        finish();
        firebaseAuth.signOut();
    }
}