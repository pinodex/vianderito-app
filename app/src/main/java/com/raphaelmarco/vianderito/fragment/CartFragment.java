package com.raphaelmarco.vianderito.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;
import com.raphaelmarco.vianderito.R;
import com.raphaelmarco.vianderito.activity.HomeActivity;
import com.raphaelmarco.vianderito.activity.billing.PayActivity;
import com.raphaelmarco.vianderito.adapter.CartAdapter;
import com.raphaelmarco.vianderito.databinding.FragmentCartBinding;
import com.raphaelmarco.vianderito.network.RetrofitClient;
import com.raphaelmarco.vianderito.network.model.cart.Transaction;
import com.raphaelmarco.vianderito.network.service.CartService;

import okhttp3.ResponseBody;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartFragment extends Fragment {

    public static final int STATE_STANDBY = 0;

    public static final int STATE_LOADING = 1;

    public static final int STATE_CART = 5;

    public static final int PAY_REQUEST = 2000;

    public static final int CAMERA_REQUEST = 2005;

    private UiData ui = new UiData();

    private CartService cartService;

    private CartAdapter cartAdapter;

    private CodeScanner codeScanner;

    private String transactionId = null;

    FragmentCartBinding binding;

    public CartFragment() {
        cartService = RetrofitClient.getInstance().create(CartService.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_cart, container, false);

        View view = binding.getRoot();

        binding.setUi(ui);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView cartView = view.findViewById(R.id.cart);

        cartAdapter = new CartAdapter();

        cartView.setLayoutManager(new LinearLayoutManager(getActivity()));
        cartView.setAdapter(cartAdapter);

        cartView.addItemDecoration(new SpacesItemDecoration(
                getResources().getDimensionPixelSize(R.dimen.product_spacing)
        ));

        CodeScannerView scannerView = view.findViewById(R.id.scanner_view);
        codeScanner = new CodeScanner(getActivity(), scannerView);

        codeScanner.setDecodeCallback(new Decoder());

        view.findViewById(R.id.pay_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPaymentProcess();
            }
        });

        disableBackButton();
    }

    @AfterPermissionGranted(CAMERA_REQUEST)
    public void startCamera() {
        if (!hasCameraPermissions()) {
            requestCameraPermissions();

            return;
        }

        codeScanner.startPreview();
    }

    public void stopCamera() {
        codeScanner.stopPreview();
    }

    public void enableBackButton() {
        ((HomeActivity) getActivity()).setBackButtonEnable(true);
    }

    public void disableBackButton() {
        ((HomeActivity) getActivity()).setBackButtonEnable(false);
    }

    public void onBackButtonClicked() {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.discard_cart)
                .setMessage(R.string.discard_cart_confirmation)
                .setPositiveButton(R.string.discard, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        discardCart();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .create()
                .show();

    }

    private void showTransactionNotFoundError() {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.error)
                .setMessage(R.string.transaction_not_found)
                .setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        discardCart();
                    }
                })
                .create()
                .show();
    }

    private void showCartEmptyMessage() {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.error)
                .setMessage(R.string.cart_empty_message)
                .setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        resetCart();
                    }
                })
                .create()
                .show();
    }

    private boolean hasCameraPermissions() {
        return EasyPermissions.hasPermissions(getActivity(), Manifest.permission.CAMERA);
    }

    private void requestCameraPermissions() {
        String rationale = getString(R.string.camera_permission_rationale);

        EasyPermissions.requestPermissions(this, rationale,
                CAMERA_REQUEST, Manifest.permission.CAMERA);
    }

    private void discardCart() {
        disableBackButton();

        ui.state.set(STATE_LOADING);

        cartService.delete(transactionId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(
                    @NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                resetCart();
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                resetCart();
            }
        });

        resetCart();
    }

    private void resetCart() {
        transactionId = null;

        ui.state.set(STATE_STANDBY);

        startCamera();
    }

    private void startPaymentProcess() {
        Intent intent = new Intent(getActivity(), PayActivity.class);

        intent.putExtra("transaction_id", transactionId);

        startActivityForResult(intent, PAY_REQUEST);

        getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.zoom_out);
    }

    private void loadTransaction() {
        cartService.get(transactionId).enqueue(new Callback<Transaction>() {
            @Override
            public void onResponse(
                    @NonNull Call<Transaction> call, @NonNull Response<Transaction> response) {

                if (!response.isSuccessful()) {
                    showTransactionNotFoundError();

                    return;
                }

                Transaction transaction = response.body();

                if (transaction.inventories.isEmpty()) {
                    showCartEmptyMessage();

                    return;
                }

                binding.setTransaction(transaction);

                cartAdapter.setData(transaction.inventories);
                cartAdapter.notifyDataSetChanged();

                ui.state.set(STATE_CART);

                enableBackButton();
            }

            @Override
            public void onFailure(@NonNull Call<Transaction> call, @NonNull Throwable t) {
                t.printStackTrace();

                showTransactionNotFoundError();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PAY_REQUEST && resultCode == Activity.RESULT_OK) {
            disableBackButton();

            resetCart();

            ((HomeActivity) getActivity()).gotoHome();
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    public class Decoder implements DecodeCallback {
        @Override
        public void onDecoded(final @NonNull Result result) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    codeScanner.stopPreview();;

                    transactionId = result.getText();

                    ui.state.set(STATE_LOADING);

                    loadTransaction();
                }
            });
        }
    }

    public class UiData extends BaseObservable {

        public ObservableBoolean isLoading = new ObservableBoolean();

        public ObservableInt state = new ObservableInt(STATE_STANDBY);

    }

    private class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.top = space;
        }
    }
}
