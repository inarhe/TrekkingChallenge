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

package edu.uoc.iartal.trekkingchallenge.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import edu.uoc.iartal.trekkingchallenge.R;
import edu.uoc.iartal.trekkingchallenge.common.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.common.FirebaseController;
import edu.uoc.iartal.trekkingchallenge.interfaces.OnGetDataListener;
import edu.uoc.iartal.trekkingchallenge.model.Message;
import edu.uoc.iartal.trekkingchallenge.model.User;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private ArrayList<Message> messages = new ArrayList<>();
    private FirebaseController controller = new FirebaseController();
    private DatabaseReference databaseUser;

    // Object which represents a list item and save view references
    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUser, textViewBody;

        // Link layout elements to variables
        public MessageViewHolder(View itemView) {
            super(itemView);
            textViewUser = (TextView) itemView.findViewById(R.id.cvMessageUser);
            textViewBody = (TextView) itemView.findViewById(R.id.cvMessageText);
        }
    }

    public MessageAdapter(ArrayList<Message> messages) {
        this.messages = messages;

        databaseUser = controller.getDatabaseReference(FireBaseReferences.USER_REFERENCE);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public MessageAdapter.MessageViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        // Inflates new list item
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view_message, viewGroup, false);
        return new MessageAdapter.MessageViewHolder(view);
    }

    /**
     * Modify content of each list item
     * @param viewHolder
     * @param position
     */
    @Override
    public void onBindViewHolder(final MessageAdapter.MessageViewHolder viewHolder, final int position) {

        controller.readDataOnce(databaseUser, new OnGetDataListener() {
            @Override
            public void onStart() {
                // Nothing to do
            }

            @Override
            public void onSuccess(DataSnapshot data) {
                for (DataSnapshot userSnapshot : data.getChildren()){
                    User user = userSnapshot.getValue(User.class);
                    if (user.getId().equals(messages.get(position).getUser())) {
                        viewHolder.textViewUser.setText("@" + user.getAlias());
                        break;
                    }
                }
            }

            @Override
            public void onFailed(DatabaseError databaseError) {
                Log.e("Mssg viewHolder error", databaseError.getMessage());
            }
        });

        viewHolder.textViewBody.setText(messages.get(position).getText());
    }
}
