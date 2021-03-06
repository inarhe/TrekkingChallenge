/* Copyright 2018 Ingrid Artal Hermoso

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

 ***********************************************************

 Glide framework
 Copyright 2014 Google, Inc. All rights reserved.
 */

package edu.uoc.iartal.trekkingchallenge.route;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.challenge.AddChallengeActivity;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.common.FirebaseController;
import edu.uoc.iartal.trekkingchallenge.interfaces.OnGetDataListener;
import edu.uoc.iartal.trekkingchallenge.interfaces.OnGetPhotoListener;
import edu.uoc.iartal.trekkingchallenge.model.Finished;
import edu.uoc.iartal.trekkingchallenge.model.Route;
import edu.uoc.iartal.trekkingchallenge.model.User;
import edu.uoc.iartal.trekkingchallenge.trip.AddTripActivity;
import edu.uoc.iartal.trekkingchallenge.user.LoginActivity;

public class ShowRouteActivity extends AppCompatActivity {
    private static final int ACTIVITY_CODE = 1;

    private StorageReference storageReference;
    private DatabaseReference databaseUser;
    private ImageView imageViewHeader, imageViewCalendar;
    private TextView textViewDate;
    private RatingBar rbAverage;
    private Route route;
    private FirebaseController controller;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_route);

        // Initialize variables
        controller = new FirebaseController();

        // If user isn't logged, start login activity
        if (controller.getActiveUserSession() == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        // Get data from item clicked on list routes activity
        Bundle bundle = getIntent().getExtras();
        route = bundle.getParcelable("route");

        // Set toolbar and actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.showRouteToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(route.getName());

        // Get storage reference
        storageReference = controller.getStorageReference();
        databaseUser = controller.getDatabaseReference(FireBaseReferences.USER_REFERENCE);

        // Link layout elements with variables
        imageViewHeader = (ImageView) findViewById(R.id.ivRoute);
        imageViewCalendar = (ImageView) findViewById(R.id.icCalendar);
        textViewDate = (TextView) findViewById(R.id.tvCalendar);
        ImageView imageViewSeason = (ImageView) findViewById(R.id.icSeason);
        ImageView imageViewType = (ImageView) findViewById(R.id.icType);
        TextView textViewType = (TextView) findViewById(R.id.tvType);
        TextView textViewTime = (TextView) findViewById(R.id.tvTime);
        TextView textViewAscent = (TextView) findViewById(R.id.tvAscent);
        TextView textViewDecline = (TextView) findViewById(R.id.tvDecline);
        TextView textViewSeason = (TextView) findViewById(R.id.tvSeason);
        TextView textViewDifficult = (TextView) findViewById(R.id.tvDifficult);
        TextView textViewTownship = (TextView) findViewById(R.id.tvTownShip);
        TextView textViewRegion = (TextView) findViewById(R.id.tvRegion);
        rbAverage = (RatingBar) findViewById(R.id.rbAverageRoute);

        // Set season icon according to route season
        if (route.getSeason().equals(getString(R.string.spring))){
            imageViewSeason.setImageResource(R.drawable.ic_spring);
        } else if (route.getSeason().equals(getString(R.string.fall))){
            imageViewSeason.setImageResource(R.drawable.ic_fall);
        } else if (route.getSeason().equals(getString(R.string.summer))) {
            imageViewSeason.setImageResource(R.drawable.ic_summer);
        } else if (route.getSeason().equals(getString(R.string.winter))) {
            imageViewSeason.setImageResource(R.drawable.ic_winter);
        }

        // Set type icon according to route season
        if (route.getType().equals(getString(R.string.circular))){
            imageViewType.setImageResource(R.drawable.ic_circular);
        } else {
            imageViewType.setImageResource(R.drawable.ic_goback);
        }

        // Get header route photo
        getHeaderPhoto();

        // Show selected route information in the layout
        textViewType.setText(route.getDistance());
        textViewTime.setText(route.getTime());
        textViewAscent.setText(Integer.toString(route.getAscent()) + " m");
        textViewDecline.setText(Integer.toString(route.getDecline()) + " m");
        textViewSeason.setText(route.getSeason());
        textViewDifficult.setText(route.getDifficult());
        textViewRegion.setText(getString(R.string.region) + "  " + route.getRegion());
        textViewTownship.setText(getString(R.string.township) + "  " + route.getTownship());

        rbAverage.setRating(route.getSumRatings() / route.getNumRatings());

        getCurrentUser();
    }

    /**
     * Start detail information route activity when button is clicked
     * @param view
     */
    public void showDetails (View view){
        Intent intent = new Intent(this, DetailRouteActivity.class);
        intent.putExtra("route", route);
        startActivity(intent);
    }

    /**
     * Show track and profile images when button is clicked
     * @param view
     */
    public void showTracks (View view) {
        Intent intent = new Intent(this, TrackRouteActivity.class);
        intent.putExtra("route", route);
        startActivity(intent);
    }

    /**
     * Inflate menu with menu layout information
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_show_route, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Define action when menu option is selected
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_finished:
                routeFinished();
                return true;
            case R.id.action_opinion:
                setOpinion();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Start finished route activity when menu option is selected
     */
    public void routeFinished() {
        Intent intent = new Intent(this, FinishedRouteActivity.class);
        intent.putExtra("route", route);
        startActivity(intent);
    }

    /**
     * Starts opinion activity when menu option is selected
     */
    public void setOpinion() {
        Intent intent = new Intent(this, RatingRouteActivity.class);
        intent.putExtra("route", route);
        startActivityForResult(intent, ACTIVITY_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            float sumRating = data.getFloatExtra("sumRating", route.getSumRatings());
            int numRating = data.getIntExtra("numRating", route.getNumRatings());

            rbAverage.setRating(sumRating / numRating);
        }
    }

    /**
     * Check if user has done this route to show its finished date
     */
    private void checkIfUserHasDone(){
        DatabaseReference databaseFinished = controller.getDatabaseReference(FireBaseReferences.FINISHED_REFERENCE);

        controller.readData(databaseFinished, new OnGetDataListener() {
            @Override
            public void onStart() {
                // Nothing to do
            }

            @Override
            public void onSuccess(DataSnapshot data) {
                for (DataSnapshot finishedSnapshot : data.getChildren()){
                    Finished finished = finishedSnapshot.getValue(Finished.class);
                    if ((finished.getRoute().equals(route.getIdRoute())) && (finished.getUser().equals(currentUser.getId()))){
                        textViewDate.setText(finishedSnapshot.getValue(Finished.class).getDate());
                        imageViewCalendar.setImageResource(R.drawable.ic_done);
                        break;
                    } else {
                        textViewDate.setText(R.string.notAlreadyDone);
                        imageViewCalendar.setImageResource(R.drawable.ic_notdone);
                    }
                }
                if (data.getChildren() == null){
                    textViewDate.setText(R.string.notAlreadyDone);
                    imageViewCalendar.setImageResource(R.drawable.ic_notdone);
                }
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                Log.e("ShowRoute ckDone error", databaseError.getMessage());
            }
        });
    }

    /**
     * Get current user information and get his history values
     */
    private void getCurrentUser(){
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
                        checkIfUserHasDone();
                        break;
                    }
                }
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                Log.e("ShowRoute getUsr error", databaseError.getMessage());
            }
        });
    }

    /**
     * Get header photo name, download it and set into show route
     */
    private void getHeaderPhoto(){
        String namePhoto = route.getHeaderPhoto();

        controller.readPhoto(storageReference, FireBaseReferences.HEADERS_STORAGE + namePhoto, new OnGetPhotoListener() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(ShowRouteActivity.this).load(uri).into(imageViewHeader);
            }

            @Override
            public void onFailed(Exception e) {
                e.printStackTrace();
                Toast.makeText(ShowRouteActivity.this, R.string.imageNotDonwloaded, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
