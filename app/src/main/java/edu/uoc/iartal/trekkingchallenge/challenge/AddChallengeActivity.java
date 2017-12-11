package edu.uoc.iartal.trekkingchallenge.challenge;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import edu.uoc.iartal.trekkingchallenge.MainActivity;
import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.objectsDB.Challenge;
import edu.uoc.iartal.trekkingchallenge.objectsDB.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.objectsDB.Route;
import edu.uoc.iartal.trekkingchallenge.objectsDB.Trip;
import edu.uoc.iartal.trekkingchallenge.objectsDB.User;
import edu.uoc.iartal.trekkingchallenge.user.ListUsersActivity;
import edu.uoc.iartal.trekkingchallenge.user.LoginActivity;

public class AddChallengeActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private static final int ACTIVITY_CODE = 1;
    private EditText editTextName, editTextDescription, dateEditText;
    private DatabaseReference databaseChallenge, databaseUser;
    private CheckBox checkBox;
    private String userAdmin, name, idChallenge, dateFormat;
    private FirebaseAuth firebaseAuth;
    private Intent intent;
    private Spinner spinner;
    private DatePickerDialog datePickerDialog;
    private Calendar dateSelected;
    private SimpleDateFormat sdf;
    ListUsersActivity listUsersActivity;
    private DatePickerDialog.OnDateSetListener date;
    ArrayList<User> userMembers = new ArrayList<>();
    ArrayList<String> nameRoutes = new ArrayList<>();
    private Context context = this;
    private ArrayAdapter<String> adapter;
    private String route;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_challenge);

        Toolbar toolbar = (Toolbar) findViewById(R.id.addChallengeToolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.addChallengeActivity));

        // Get Firebase authentication instance
        firebaseAuth = FirebaseAuth.getInstance();
        databaseChallenge = FirebaseDatabase.getInstance().getReference(FireBaseReferences.CHALLENGE_REFERENCE);

        dateSelected = Calendar.getInstance();
        dateFormat = "dd.MM.yyyy";
        sdf = new SimpleDateFormat(dateFormat, Locale.GERMAN);

        editTextName = (EditText) findViewById(R.id.etNameChallenge);
        editTextDescription = (EditText) findViewById(R.id.etDescriptionChallenge);
        dateEditText = (EditText) findViewById(R.id.dateEditText);
        checkBox = (CheckBox) findViewById(R.id.cBPublicChallenge);
        spinner = (Spinner) findViewById(R.id.spinnerRoute);

        nameRoutes.clear();
        nameRoutes.add("- Selecciona la ruta -");

        DatabaseReference databaseRoute = FirebaseDatabase.getInstance().getReference(FireBaseReferences.ROUTE_REFERENCE);

        databaseRoute.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot routeSnapshot : dataSnapshot.getChildren()) {
                    String nameRoute = routeSnapshot.getValue(Route.class).getName();
                    nameRoutes.add(nameRoute);
                }
                adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.support_simple_spinner_dropdown_item, nameRoutes);
                //spinner.setPrompt("Selecciona la ruta");
                spinner.setAdapter(adapter);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });


       // adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.support_simple_spinner_dropdown_item, nameRoutes);
        //spinner.setPrompt("Selecciona la ruta");
       // spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);



        long currentDate = System.currentTimeMillis();
        String dateString = sdf.format(currentDate);
        dateEditText.setText(dateString);

        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                // TODO Auto-generated method stub
                dateSelected.set(Calendar.YEAR, year);
                dateSelected.set(Calendar.MONTH, month);
                dateSelected.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDate();
            }
        };

        if (firebaseAuth.getCurrentUser() == null) {
            // If user isn't logged, start login activity
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        } else {
            String mail = firebaseAuth.getCurrentUser().getEmail();
            databaseUser = FirebaseDatabase.getInstance().getReference(FireBaseReferences.USER_REFERENCE);
            Query query = databaseUser.orderByChild(FireBaseReferences.USERMAIL_REFERENCE).equalTo(mail);

            // Query database to get user admin information
            query.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    userAdmin = dataSnapshot.getValue(User.class).getIdUser();
                    //   userKey = dataSnapshot.getValue(User.class).getIdUser();
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






        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.i("date", "date");
                new DatePickerDialog(context, date, dateSelected
                        .get(Calendar.YEAR), dateSelected.get(Calendar.MONTH),
                        dateSelected.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

       // ((TextView) view).setTextColor(Color.BLACK);
        ((TextView) parent.getChildAt(0)).setTextSize(18);

        if (position != 0){
            route = spinner.getSelectedItem().toString();
        } else {
            route = null;
        }


    }


    public void onNothingSelected(AdapterView<?> parent) {

    }



    private void updateDate() {
        dateEditText.setText(sdf.format(dateSelected.getTime()));
    }



    public void addChallenge (View view) {
        // Add group to database when accept button is clicked
        Boolean isPublic = false;
        name = editTextName.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String date = dateEditText.getText().toString().trim();

        // If some of the input parameters are incorrect, stops the function execution further
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, getString(R.string.nameAdvice), Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(description)) {
            Toast.makeText(this, getString(R.string.descAdvice), Toast.LENGTH_LONG).show();
            return;
        }


        if (route==null) {
            Toast.makeText(this, getString(R.string.chooseRoute), Toast.LENGTH_LONG).show();
            return;
        }

        if (checkBox.isChecked()) {
            isPublic = true;
        }

        // Add group to firebase database
      //  idGroup = databaseGroup.push().s.getKey();
        try {
           // idTrip = databaseTrip.push().getKey();
            idChallenge = name;
        } catch (DatabaseException e) {
            e.printStackTrace();
        }



        Challenge challenge = new Challenge(idChallenge, name, description, date, route, userAdmin, isPublic, 1);

        databaseChallenge.child(idChallenge).setValue(challenge).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful()) {
                    Toast.makeText(AddChallengeActivity.this, getString(R.string.failedAddChallenge), Toast.LENGTH_SHORT).show();
                }
            }
        });

        databaseChallenge.child(idChallenge).child(FireBaseReferences.MEMBERSCHALLENGE_REFERENCE).child(userAdmin).setValue("true");

        databaseUser.child(userAdmin).child(FireBaseReferences.USERCHALLENGES_REFERENCE).child(challenge.getIdChallenge()).setValue("true")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), getString(R.string.challengeSaved), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(AddChallengeActivity.this,getString(R.string.failedAddChallenge),Toast.LENGTH_SHORT).show();
                }
            }
        });


        inviteUsers();


    }

    public void cancelChallenge (View view) {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    private void inviteUsers (){
        intent = new Intent(getApplicationContext(), ListUsersActivity.class);
        intent.putExtra("challengeName", name);
        intent.putExtra("idChallenge", idChallenge);
        startActivity(intent);
        finish();
    }
}