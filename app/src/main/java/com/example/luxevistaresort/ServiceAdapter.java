package com.example.luxevistaresort;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class ServiceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SERVICE = 0;
    private static final int VIEW_TYPE_FOOTER = 1;

    private final List<Service> serviceList;
    private final Context context;

    public ServiceAdapter(Context context, List<Service> serviceList) {
        this.context = context;
        this.serviceList = serviceList;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == serviceList.size()) ? VIEW_TYPE_FOOTER : VIEW_TYPE_SERVICE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SERVICE) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_service_public, parent, false);
            return new ServiceViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_login_prompt_footer, parent, false);
            return new FooterViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_TYPE_SERVICE) {
            ServiceViewHolder serviceHolder = (ServiceViewHolder) holder;
            Service service = serviceList.get(position);
            serviceHolder.bind(service);
        } else {
            FooterViewHolder footerHolder = (FooterViewHolder) holder;
            setupLoginPrompt(footerHolder.loginPromptText);
        }
    }

    @Override
    public int getItemCount() {
        return serviceList.size() + 1;
    }

    public static class ServiceViewHolder extends RecyclerView.ViewHolder {
        private final ImageView serviceImage;
        private final TextView serviceName, serviceDescription, servicePrice;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceImage = itemView.findViewById(R.id.serviceImage);
            serviceName = itemView.findViewById(R.id.serviceName);
            serviceDescription = itemView.findViewById(R.id.serviceDescription);
            servicePrice = itemView.findViewById(R.id.servicePrice);
        }

        public void bind(Service service) {
            serviceName.setText(service.getName());
            serviceDescription.setText(service.getDescription());
            servicePrice.setText(String.format(Locale.US, "$%d", service.getPrice()));
            if (service.getImageUri() != null && !service.getImageUri().isEmpty()) {
                serviceImage.setImageURI(Uri.parse(service.getImageUri()));
            } else {
                serviceImage.setImageResource(R.drawable.service_spa);
            }
        }
    }

    public static class FooterViewHolder extends RecyclerView.ViewHolder {
        TextView loginPromptText;
        public FooterViewHolder(@NonNull View itemView) {
            super(itemView);
            loginPromptText = itemView.findViewById(R.id.loginPromptText);
        }
    }

    private void setupLoginPrompt(TextView loginPromptText) {
        String fullText = "To make a reservation, please Login or Register";
        SpannableString spannableString = new SpannableString(fullText);
        ClickableSpan loginSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                context.startActivity(new Intent(context, LoginActivity.class));
            }
        };
        ClickableSpan registerSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                context.startActivity(new Intent(context, RegisterActivity.class));
            }
        };
        int loginStart = fullText.indexOf("Login");
        int registerStart = fullText.indexOf("Register");
        spannableString.setSpan(loginSpan, loginStart, loginStart + "Login".length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(registerSpan, registerStart, registerStart + "Register".length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        loginPromptText.setText(spannableString);
        loginPromptText.setMovementMethod(LinkMovementMethod.getInstance());
        loginPromptText.setHighlightColor(ContextCompat.getColor(context, android.R.color.transparent));
    }
}