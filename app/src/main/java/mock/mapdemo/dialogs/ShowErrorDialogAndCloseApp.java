package mock.mapdemo.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;


public class ShowErrorDialogAndCloseApp {
    private final Context context;

    public ShowErrorDialogAndCloseApp(Context context) {
        this.context = context;
    }

    public AlertDialog.Builder getAlert(String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setMessage(message);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
        return alert;
    }
}