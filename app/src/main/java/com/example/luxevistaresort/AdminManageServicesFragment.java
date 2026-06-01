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

public class AdminManageServicesFragment extends Fragment {

    private RecyclerView servicesRecyclerView;
    private ServiceManagementAdapter adapter;
    private UserRepository userRepository;
    private List<Service> serviceList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_manage_services, container, false);

        userRepository = new UserRepository(getContext());
        servicesRecyclerView = view.findViewById(R.id.servicesRecyclerView);
        FloatingActionButton fabAddService = view.findViewById(R.id.fabAddService);

        // Initialize the list and adapter once
        serviceList = new ArrayList<>();
        adapter = new ServiceManagementAdapter(getContext(), serviceList);
        servicesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        servicesRecyclerView.setAdapter(adapter);

        fabAddService.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddEditServiceActivity.class);
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadServices();
    }

    private void loadServices() {
        // Clear the list, get fresh data from the database, and notify the adapter
        serviceList.clear();
        serviceList.addAll(userRepository.getAllServices());
        adapter.notifyDataSetChanged();
    }
}