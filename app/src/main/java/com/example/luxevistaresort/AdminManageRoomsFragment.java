package com.example.luxevistaresort;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class AdminManageRoomsFragment extends Fragment {

    private RecyclerView roomsRecyclerView;
    private RoomManagementAdapter adapter;
    private UserRepository userRepository;
    private List<Room> roomList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_manage_rooms, container, false);

        userRepository = new UserRepository(getContext());
        roomsRecyclerView = view.findViewById(R.id.roomsRecyclerView);
        FloatingActionButton fab = view.findViewById(R.id.fabAddRoom);


        roomList = new ArrayList<>();
        adapter = new RoomManagementAdapter(getContext(), roomList);
        roomsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        roomsRecyclerView.setAdapter(adapter);

        fab.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddEditRoomActivity.class);
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadRooms();
    }

    private void loadRooms() {

        // get the fresh data, and notify the existing adapter of the change.
        roomList.clear();
        roomList.addAll(userRepository.getAllRooms());
        adapter.notifyDataSetChanged();
    }
}