package edu.uoc.iartal.trekkingchallenge;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.uoc.iartal.trekkingchallenge.objectsDB.FireBaseReferences;
import edu.uoc.iartal.trekkingchallenge.objectsDB.PhotoAdapter;
import edu.uoc.iartal.trekkingchallenge.objectsDB.Route;
import edu.uoc.iartal.trekkingchallenge.objectsDB.RouteAdapter;

public class PhotoGalleryActivity extends AppCompatActivity {

    private PhotoAdapter photoAdapter;
    private RecyclerView recyclerView;
    private Route route;
    private Toolbar toolbar;
    private DatabaseReference databaseRoutes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_gallery);

        toolbar = (Toolbar) findViewById(R.id.photoGalleryToolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.photoGallery));

        Bundle bundle = getIntent().getExtras();
        route = bundle.getParcelable("route");

        recyclerView = (RecyclerView) findViewById(R.id.rvPhotoGallery);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        photoAdapter = new PhotoAdapter(route, PhotoGalleryActivity.this);
        recyclerView.setAdapter(photoAdapter);

        photoAdapter.notifyDataSetChanged();

    }
}
