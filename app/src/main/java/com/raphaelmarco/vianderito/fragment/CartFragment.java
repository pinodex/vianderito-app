package com.raphaelmarco.vianderito.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.raphaelmarco.vianderito.adapter.CartAdapter;
import com.raphaelmarco.vianderito.databinding.FragmentCartBinding;
import com.raphaelmarco.vianderito.network.RetrofitClient;
import com.raphaelmarco.vianderito.network.model.cart.Transaction;
import com.raphaelmarco.vianderito.network.model.store.Inventory;
import com.raphaelmarco.vianderito.network.service.CartService;
import com.tapadoo.alerter.Alert;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartFragment extends Fragment {

    public static final int STATE_STANDBY = 0;

    public static final int STATE_LOADING = 1;

    public static final int STATE_CART = 5;

    private UiData ui = new UiData();

    private CartService cartService;

    private CartAdapter cartAdapter;

    private CodeScanner codeScanner;

    private String code = "93657630-bff0-11e8-81ae-f31f909b6045";

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

        cartAdapter = new CartAdapter(getActivity());

        cartView.setLayoutManager(new LinearLayoutManager(getActivity()));
        cartView.setAdapter(cartAdapter);

        cartView.addItemDecoration(new SpacesItemDecoration(
                getResources().getDimensionPixelSize(R.dimen.product_spacing)
        ));

        CodeScannerView scannerView = view.findViewById(R.id.scanner_view);
        codeScanner = new CodeScanner(getActivity(), scannerView);

        codeScanner.setDecodeCallback(new Decoder());

        disableBackButton();
        //loadTransaction();
    }

    public void startCamera() {
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

    private void discardCart() {
        code = null;

        ui.state.set(STATE_STANDBY);

        startCamera();

        disableBackButton();
    }

    private void loadTransaction() {
        cartService.get(code).enqueue(new Callback<Transaction>() {
            @Override
            public void onResponse(
                    @NonNull Call<Transaction> call, @NonNull Response<Transaction> response) {

                if (!response.isSuccessful()) {
                    showTransactionNotFoundError();

                    return;
                }

                ui.state.set(STATE_CART);

                Transaction transaction = response.body();

                binding.setTransaction(transaction);

                cartAdapter.setData(transaction.inventories);
                cartAdapter.notifyDataSetChanged();

                enableBackButton();
            }

            @Override
            public void onFailure(@NonNull Call<Transaction> call, @NonNull Throwable t) {

            }
        });
    }

    public class Decoder implements DecodeCallback {
        @Override
        public void onDecoded(final @NonNull Result result) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    codeScanner.stopPreview();;

                    code = result.getText();

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
