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
 */

package edu.uoc.iartal.trekkingchallenge.common;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import edu.uoc.iartal.trekkingchallenge.map.MapActivity;
import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.challenge.ListChallengesActivity;
import edu.uoc.iartal.trekkingchallenge.group.ListGroupsActivity;
import edu.uoc.iartal.trekkingchallenge.route.ListRoutesActivity;
import edu.uoc.iartal.trekkingchallenge.trip.ListTripsActivity;
import edu.uoc.iartal.trekkingchallenge.user.AccessActivity;
import edu.uoc.iartal.trekkingchallenge.user.UserAreaActivity;

public class MainActivity extends AppCompatActivity {

    private FirebaseController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);

        // Initialize variables
        controller = new FirebaseController();
    }

    /**
     * Main map functionality. If user isn't logged, access is forbidden and opens user access activity
     * @param view
     */
    public void mapActivity (View view){
        if (controller.getActiveUserSession() != null) {
            startActivity(new Intent(getApplicationContext(),MapActivity.class));
        } else {
            startActivity(new Intent(getApplicationContext(), AccessActivity.class));
        }
    }

    /**
     * Main list routes functionality. If user isn't logged, access is forbidden and opens user access activity
     * @param view
     */
    public void routesActivity(View view){
        if (controller.getActiveUserSession() != null) {
            startActivity(new Intent(getApplicationContext(),ListRoutesActivity.class));
        } else {
            startActivity(new Intent(getApplicationContext(), AccessActivity.class));
        }
    }

    /**
     * Main trip functionality. If user isn't logged, access is forbidden and opens user access activity
     * @param view
     */
    public void tripActivity (View view){
        if (controller.getActiveUserSession() != null) {
            startActivity(new Intent(getApplicationContext(),ListTripsActivity.class));
        } else {
            startActivity(new Intent(getApplicationContext(), AccessActivity.class));
        }
    }

    /**
     * Main challenge functionality. If user isn't logged, access is forbidden and opens user access activity
     * @param view
     */
    public void challengeActivity (View view){
        if (controller.getActiveUserSession() != null) {
            startActivity(new Intent(getApplicationContext(),ListChallengesActivity.class));
        } else {
            startActivity(new Intent(getApplicationContext(), AccessActivity.class));
        }
    }

    /**
     * Main user area functionality. If user isn't logged, access is forbidden and opens user access activity
     * @param view
     */
    public void userAreaActivity (View view){
        if (controller.getActiveUserSession() != null) {
            startActivity(new Intent(this, UserAreaActivity.class));
        } else {
            startActivity(new Intent(getApplicationContext(), AccessActivity.class));
        }
    }

    /**
     * Main group functionality. If user isn't logged, access is forbidden and opens user access activity
     * @param view
     */
    public void groupActivity (View view){
        if (controller.getActiveUserSession() != null) {
            startActivity(new Intent(getApplicationContext(), ListGroupsActivity.class));
        } else {
            startActivity(new Intent(getApplicationContext(), AccessActivity.class));
        }
    }
}
