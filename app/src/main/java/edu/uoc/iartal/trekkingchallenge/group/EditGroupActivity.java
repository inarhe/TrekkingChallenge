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

package edu.uoc.iartal.trekkingchallenge.group;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.common.FirebaseController;
import edu.uoc.iartal.trekkingchallenge.interfaces.OnCompleteTaskListener;
import edu.uoc.iartal.trekkingchallenge.interfaces.OnGetDataListener;
import edu.uoc.iartal.trekkingchallenge.model.Group;
import edu.uoc.iartal.trekkingchallenge.user.LoginActivity;

public class EditGroupActivity extends AppCompatActivity {
    private DatabaseReference databaseGroup;
    private Group group;
    private EditText editTextName, editTextDescription;
    private ProgressDialog progressDialog;
    private boolean updateDesc, groupExists;
    private String newDescription;
    private FirebaseController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);

        // Hide keyboard until user select edit text
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Set toolbar and actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.editGroupToolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.editGroupActivityName);

        // Initialize variables
        controller = new FirebaseController();
        progressDialog = new ProgressDialog(this);

        // If user isn't logged, start login activity
        if (controller.getActiveUserSession() == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        // Get database references
        databaseGroup = controller.getDatabaseReference(FireBaseReferences.GROUP_REFERENCE);

        // Link layout elements with variables
        editTextName = (EditText) findViewById(R.id.etGroupName);
        editTextDescription = (EditText) findViewById(R.id.etGroupDesc);

        // Get data from item clicked on list groups activity
        Bundle bundle = getIntent().getExtras();
        group = bundle.getParcelable("group");

        // Show selected group information in the layout
        editTextName.setText(group.getName());
        editTextDescription.setText(group.getDescription());
    }

    /**
     * Executed when accept button is clicked. Verify input parameters and update values
     * @param view
     */
    public void editGroup (View view) {
        //Initialize variables
        Boolean updateName = false;
        updateDesc = false;
        groupExists = false;

        // Get input parameters
        final String newName = editTextName.getText().toString().trim();
        newDescription = editTextDescription.getText().toString().trim();

        // Check input parameters. If some parameter is incorrect or empty, stops the function execution
        if (TextUtils.isEmpty(newName)) {
            Toast.makeText(this, R.string.nameField, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(newDescription)) {
            Toast.makeText(this, R.string.descriptionField, Toast.LENGTH_SHORT).show();
            return;
        }

        // Set progress dialog
        progressDialog.setMessage(getString(R.string.updating));
        progressDialog.show();

        // Check changes on group name parameter
        if (!(group.getName()).equals(newName)) {
            updateName = true;
        }

        // Check changes on group description parameter
        if ((!(group.getDescription()).equals(newDescription))) {
            updateDesc = true;
        }

        // Update database if its necessary
        if (updateName){
            // Execute controller method to check if group name exists and if it doesn't, update database group object.
            controller.readDataOnce(databaseGroup, new OnGetDataListener() {
                @Override
                public void onStart() {
                    //Nothing to do
                }

                @Override
                public void onSuccess(DataSnapshot data) {
                    for (DataSnapshot groupSnapshot : data.getChildren()){
                        Group group = groupSnapshot.getValue(Group.class);
                        if (group.getName().equals(newName)){
                            groupExists = true;
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), R.string.groupAlreadyExists, Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                    // If group doesn't exist, executes update parameter
                    if (!groupExists){
                        controller.updateStringParameter(databaseGroup, group.getId(), FireBaseReferences.GROUP_NAME_REFERENCE, newName);
                        if (updateDesc){
                            updateDescriptionValue();
                        }
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), R.string.editGroupOk, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }

                @Override
                public void onFailed(DatabaseError databaseError) {
                    Log.e("EditGroup error", databaseError.getMessage());
                }
            });
        } else {
            if (updateDesc){
                updateDescriptionValue();
            }
            progressDialog.dismiss();
            finish();
        }
    }

    /**
     * Cancel edit group if cancel button is clicked
     * @param view
     */
    public void cancelEditGroup (View view) {
        finish();
    }

    /**
     * Update description group into database
     */
    private void updateDescriptionValue() {
        controller.updateStringParameter(databaseGroup, group.getId(), FireBaseReferences.GROUP_DESCRIPTION_REFERENCE, newDescription);
        Toast.makeText(getApplicationContext(), R.string.editGroupOk, Toast.LENGTH_SHORT).show();
    }
}
