package capstone.abang.com.Utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import capstone.abang.com.R;

/**
 * Created by Pc-user on 10/02/2018.
 */

public class FilterDialog extends AppCompatDialogFragment {
    private RadioGroup sortGroup;
    private RadioGroup transmissionGroup;
    private RadioButton likesRadioButton;
    private RadioButton yearRadioButton;
    private RadioButton capacityRadioButton;
    private RadioButton recentRadioButton;
    private RadioButton automaticRadioButton;
    private RadioButton manualRadioButton;
    private String sortString;
    private String transmissionString;
    private FilterDialogListener listener;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog, null);

        builder.setView(view)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Filter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(likesRadioButton.isChecked()) {
                            sortString = "cdlikes";
                        } else if(capacityRadioButton.isChecked()) {
                            sortString = "cdcapacity";
                        } else if(yearRadioButton.isChecked()) {
                            sortString = "cdcaryear";
                        } else if(recentRadioButton.isChecked()) {
                            sortString = "recent";
                        }
                        if(automaticRadioButton.isChecked()) {
                            transmissionString = "automatic";
                        } else if(manualRadioButton.isChecked()) {
                            transmissionString = "manual";
                        }

                        listener.applyText(sortString, transmissionString);

                    }
                });

        sortGroup = view.findViewById(R.id.sortgroup);
        transmissionGroup = view.findViewById(R.id.transmissiongroup);
        likesRadioButton = view.findViewById(R.id.likeRB);
        yearRadioButton = view.findViewById(R.id.yearRB);
        capacityRadioButton = view.findViewById(R.id.capacityRB);
        recentRadioButton = view.findViewById(R.id.recentRB);
        automaticRadioButton = view.findViewById(R.id.automaticRB);
        manualRadioButton = view.findViewById(R.id.manualRB);
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (FilterDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "Must implement filter dialog");
        }
    }

    public interface FilterDialogListener {
        void applyText(String sort, String transmission);
    }
}
