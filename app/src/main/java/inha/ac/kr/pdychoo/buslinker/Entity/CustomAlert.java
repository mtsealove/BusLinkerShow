package inha.ac.kr.pdychoo.buslinker.Entity;

import android.app.Service;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import inha.ac.kr.pdychoo.buslinker.R;

public class CustomAlert extends AppCompatActivity {
    final Context context;
    AlertDialog dialog;

    public CustomAlert(Context context) {
        this.context = context;
    }

    public void DialogChoice(String title, String msg, View.OnClickListener onClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_confirm, null);
        builder.setView(view);
        Button cancelBtn = view.findViewById(R.id.cancelBtn);
        Button confirmBtn = view.findViewById(R.id.confirmBtn);
        TextView titleTV = view.findViewById(R.id.titleTV);
        TextView msgTV = view.findViewById(R.id.msgTV);

        titleTV.setText(title);
        msgTV.setText(msg);

        dialog = builder.create();
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        confirmBtn.setOnClickListener(onClickListener);
        dialog.show();
    }

    public void cancelDialog() {
        dialog.cancel();
    }
}
