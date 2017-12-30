package edu.uoc.iartal.trekkingchallenge.trip;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.common.FirebaseController;
import edu.uoc.iartal.trekkingchallenge.common.OnGetDataListener;
import edu.uoc.iartal.trekkingchallenge.model.History;
import edu.uoc.iartal.trekkingchallenge.model.Route;
import edu.uoc.iartal.trekkingchallenge.model.Trip;
import edu.uoc.iartal.trekkingchallenge.model.TripDone;
import edu.uoc.iartal.trekkingchallenge.model.User;
import edu.uoc.iartal.trekkingchallenge.user.LoginActivity;

public class FinishedTripActivity extends AppCompatActivity {
    private Calendar dateSelected;
    private SimpleDateFormat sdf;
    private DatePickerDialog.OnDateSetListener date;
    private User currentUser;
    private EditText editTextDate, editTextDist, editTextHour;
    private Trip trip;
    private String finishDist, finishHour;
    private DatabaseReference databaseUser, databaseTripDone, databaseTrip, databaseHistory, databaseRoute;
    private Double historyTime, historyDistance;
    private int historySlope, routeSlope;
    private FirebaseController controller;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finished_trip);

        // Set toolbar and actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.finishedTripToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.finishTripActivity);

        // Hide keyboard until user select edit text
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Initialize variables
        controller = new FirebaseController();
        context = this;

        // If user isn't logged, start login activity
        if (controller.getActiveUserSession() == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        // Get data from show challenge activity
        Bundle bundle = getIntent().getExtras();
        trip = bundle.getParcelable("trip");

        // Get database references
        databaseUser = controller.getDatabaseReference(FireBaseReferences.USER_REFERENCE);
        databaseTripDone = controller.getDatabaseReference(FireBaseReferences.TRIPSDONE_REFERENCE);
        databaseTrip = controller.getDatabaseReference(FireBaseReferences.TRIP_REFERENCE);
        databaseHistory = controller.getDatabaseReference(FireBaseReferences.HISTORY_REFERENCE);
        databaseRoute = controller.getDatabaseReference(FireBaseReferences.ROUTE_REFERENCE);

        // Link layout elements with variables
        editTextDate = (EditText) findViewById(R.id.etDateFinish);
        editTextDist = (EditText) findViewById(R.id.etDistFinish);
        editTextHour = (EditText) findViewById(R.id.etHourFinish);

        // Set calendar and date format
        dateSelected = Calendar.getInstance();
        String dateFormat = "dd.MM.yyyy";
        sdf = new SimpleDateFormat(dateFormat, Locale.GERMAN);
        setDate();

        getUserAdmin();

        // Click listener on date edit text to show calendar
        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(context, date, dateSelected
                        .get(Calendar.YEAR), dateSelected.get(Calendar.MONTH),
                        dateSelected.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }


    /**
     * Save user challenge result in database and update dependencies
     * @param view
     */
    public void registerFinish(View view){
        //Initialize variables
        String idDone;
        String finishDate = editTextDate.getText().toString().trim();
        finishDist = editTextDist.getText().toString().trim();
        finishHour = editTextHour.getText().toString().trim();

        // Check input parameters. If some parameter is incorrect or empty, stops the function execution
        if (TextUtils.isEmpty(finishDist)) {
            Toast.makeText(this, getString(R.string.distAdvice), Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(finishHour)) {
            Toast.makeText(this, getString(R.string.timeAdvice), Toast.LENGTH_SHORT).show();
            return;
        }

        // Add trip result to firebase database
        idDone = controller.getFirebaseNewKey(databaseTripDone);

        if (idDone == null){
            Toast.makeText(getApplicationContext(), R.string.finishedFailed, Toast.LENGTH_SHORT).show();
        } else {
            TripDone tripDone = new TripDone(idDone, Double.parseDouble(finishDist), Double.parseDouble(finishHour), currentUser.getId(), trip.getId(), finishDate, trip.getName(), trip.getRoute());
            controller.addNewTripResult(databaseTripDone, tripDone, getApplicationContext());
        }

        // Update result list in user and challenge database nodes
        controller.updateResults (databaseUser, currentUser.getId(), FireBaseReferences.USER_TRIPSDONE_REFERENCE, idDone, this);
        controller.updateResults(databaseTrip, trip.getId(), FireBaseReferences.TRIP_DONE_REFERENCE,idDone, this);

        updateHistory();
        finish();
    }

    /**
     * Cancel result registration when cancel button is clicked
     * @param view
     */
    public void cancelFinish (View view){
        finish();
    }

    /**
     * Set current date format and DatePickerDialog
     */
    private void setDate(){
        long currentDate = System.currentTimeMillis();
        String dateString = sdf.format(currentDate);
        editTextDate.setText(dateString);

        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                dateSelected.set(Calendar.YEAR, year);
                dateSelected.set(Calendar.MONTH, month);
                dateSelected.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDate();
            }
        };
    }

    /**
     * Set current date in layout edit text
     */
    private void updateDate() {
        editTextDate.setText(sdf.format(dateSelected.getTime()));
    }

    /**
     * Get current user information and get his history values
     */
    private void getUserAdmin(){
        // Execute controller method to get database current user object. Use OnGetDataListener interface to know
        // when database data is retrieved
        controller.readDataOnce(databaseUser, new OnGetDataListener() {
            @Override
            public void onStart() {
                //Nothing to do
            }

            @Override
            public void onSuccess(DataSnapshot data) {
                String currentMail = controller.getCurrentUserEmail();

                for (DataSnapshot userSnapshot : data.getChildren()){
                    User user = userSnapshot.getValue(User.class);

                    if (user.getMail().equals(currentMail)){
                        currentUser = user;
                        getHistoryValues();
                    }
                }
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                Log.e("FinishTrip getAdm error", databaseError.getMessage());
            }
        });
    }

    /**
     * Get user history values
     */
    private void getHistoryValues(){

        controller.readDataOnce(databaseHistory, new OnGetDataListener() {
            @Override
            public void onStart() {
                // Nothing to do
            }

            @Override
            public void onSuccess(DataSnapshot data) {
                for (DataSnapshot historySnapshot : data.getChildren()) {
                    History history = historySnapshot.getValue(History.class);
                    if (history.getId().equals(currentUser.getHistory())){
                        historyDistance = history.getTotalDistance();
                        historyTime = history.getTotalTime();
                        historySlope = history.getTotalSlope();
                    }
                }
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                Log.e("FinTrip getHist error", databaseError.getMessage());
            }
        });
    }

    /**
     * Update user history with trip results.
     */
    private void updateHistory(){
        controller.readDataOnce(databaseRoute, new OnGetDataListener() {
            @Override
            public void onStart() {
                // Nothing to do
            }

            @Override
            public void onSuccess(DataSnapshot data) {
                for (DataSnapshot routeSnapshot : data.getChildren()) {
                    Route route = routeSnapshot.getValue(Route.class);
                    if (route.getIdRoute().equals(trip.getRoute())) {
                        routeSlope = route.getAscent() + route.getDecline();
                    }
                }
                controller.updateHistory(currentUser.getHistory(), (historySlope + routeSlope), (historyDistance + Double.parseDouble(finishDist)), sumHours(historyTime, Double.parseDouble(finishHour)));
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                Log.e("UpHistTDone error", databaseError.getMessage());
            }
        });
    }

    /**
     * Sum two hours in double format
     * @param firstHour
     * @param secondHour
     * @return
     */
    private Double sumHours (Double firstHour, Double secondHour){
        String [] firstNum = (Double.toString(firstHour)).split("\\.");
        String [] secondNum = (Double.toString(secondHour)).split("\\.");
        Integer firstInt = Integer.parseInt(firstNum[0]);
        Integer secondInt = Integer.parseInt(secondNum[0]);
        Double totalDecimal = (firstHour - (double)firstInt) + (secondHour - (double)secondInt);

        return (double)firstInt + (double)secondInt + (totalDecimal/60);
    }
}
