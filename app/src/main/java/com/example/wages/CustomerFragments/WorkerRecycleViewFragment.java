package com.example.wages.CustomerFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.wages.Database;
import com.example.wages.R;
import com.example.wages.Adapters.WorkerFirebaseAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;


public class WorkerRecycleViewFragment extends Fragment {

    RecyclerView RecycleView;
    SearchView MySearchView;
    WorkerFirebaseAdapter workerFirebaseAdapter;

    public WorkerRecycleViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_worker_recycle_view, container, false);

        RecycleView = view.findViewById(R.id.worker_recycle_view);
        MySearchView = view.findViewById(R.id.search_view);

        RecycleView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        FirebaseRecyclerOptions<Database> options =
                new FirebaseRecyclerOptions.Builder<Database>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Workers"), Database.class)
                        .build();

        workerFirebaseAdapter = new WorkerFirebaseAdapter(options);
        RecycleView.setAdapter(workerFirebaseAdapter);

        MySearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                FirebaseRecyclerOptions<Database> options =
                        new FirebaseRecyclerOptions.Builder<Database>()
                                .setQuery(FirebaseDatabase.getInstance().getReference().child("Workers").orderByChild("profession").equalTo(s), Database.class)
                                .build();

                workerFirebaseAdapter = new WorkerFirebaseAdapter(options);
                workerFirebaseAdapter.startListening();
                RecycleView.setAdapter(workerFirebaseAdapter);

                return false;
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        workerFirebaseAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        workerFirebaseAdapter.startListening();
    }
}