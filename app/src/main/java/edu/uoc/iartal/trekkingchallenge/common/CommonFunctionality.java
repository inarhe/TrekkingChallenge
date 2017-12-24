package edu.uoc.iartal.trekkingchallenge.common;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.objects.User;


public class CommonFunctionality {

    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";
    private String currentUserName;
    /**
     *  Validate mail format
     * @param email
     * @return if mail is ok
     */
    public boolean validateEmail(String email) {
        Pattern pattern;

        // Define mail pattern
        pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * Validate password length
     * @param password
     * @return if password is ok
     */
    public boolean validatePassword(String password) {
        return password.length() > 5;
    }

    /**
     * Update joins, when a user wants to join a group, trip or challenge
     * @param currentMail
     * @param action
     * @param databaseObject
     * @param id
     * @param objectReference
     */
    public void updateJoins(String currentMail, final String action, final DatabaseReference databaseObject, final String id, final int numberOfMembers, final String objectReference){
        final DatabaseReference databaseUser = FirebaseDatabase.getInstance().getReference(FireBaseReferences.USER_REFERENCE);

        Query query = databaseUser.orderByChild(FireBaseReferences.USER_MAIL_REFERENCE).equalTo(currentMail);

        // Query database to get user information
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);

                if (action.equals("join")) {
                    databaseObject.child(id).child(FireBaseReferences.MEMBERS_REFERENCE).child(user.getIdUser()).setValue("true");
                    databaseUser.child(user.getIdUser()).child(objectReference).child(id).setValue("true");
                    databaseObject.child(id).child(FireBaseReferences.NUMBER_OF_MEMBERS_REFERENCE).setValue(numberOfMembers+1);
                } else {
                    databaseObject.child(id).child(FireBaseReferences.MEMBERS_REFERENCE).child(user.getIdUser()).removeValue();
                    databaseUser.child(user.getIdUser()).child(objectReference).child(id).removeValue();
                    databaseObject.child(id).child(FireBaseReferences.NUMBER_OF_MEMBERS_REFERENCE).setValue(numberOfMembers-1);
                }
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

    /**
     * Update route results and challenge results in all dependencies
     * @param databaseObject
     * @param child
     * @param reference
     * @param result
     * @param context
     */
    public void updateResults (DatabaseReference databaseObject, String child, String reference, String result, final Context context){
        databaseObject.child(child).child(reference).child(result).setValue("true")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(context, context.getResources().getString(R.string.finishedSaved), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, context.getResources().getString(R.string.finishedFailed),Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public interface SimpleCallback<T> {
        void callback(T data);
    }


    public void getCurrentUserName(){//}@NonNull SimpleCallback<String> finishedCallback) {
        // Get current user

        final String currentMail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        DatabaseReference databaseUser = FirebaseDatabase.getInstance().getReference(FireBaseReferences.USER_REFERENCE);
        databaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot :
                        dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (user.getUserMail().equals(currentMail)) {
                        currentUserName = user.getIdUser();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
