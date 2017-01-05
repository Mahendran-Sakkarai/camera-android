package com.mahendran_sakkarai.camera.capture;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mahendran_sakkarai.camera.R;

/**
 * Created by Mahendran Sakkarai on 1/5/2017.
 */
public class CaptureFragment extends Fragment implements CaptureContract.View {
    private TextView mMessageView;
    private CaptureContract.Presenter mPresenter;

    public static CaptureFragment newInstance() {
        return new CaptureFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.capture_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMessageView = (TextView) view.findViewById(R.id.message_view);
    }

    @Override
    public void showMessage(String message) {
        if (message != null)
            mMessageView.setText(message);
    }

    @Override
    public void setPresenter(CaptureContract.Presenter presenter) {
        this.mPresenter = presenter;
    }
}
