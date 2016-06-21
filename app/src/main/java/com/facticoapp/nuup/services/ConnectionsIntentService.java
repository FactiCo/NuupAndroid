package com.facticoapp.nuup.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.facticoapp.nuup.dialogues.Dialogues;
import com.facticoapp.nuup.fragments.MainFragment;
import com.facticoapp.nuup.httpconnection.HttpConnection;
import com.facticoapp.nuup.models.Report;
import com.facticoapp.nuup.models.ReportAzure;
import com.facticoapp.nuup.parser.GsonParser;

/**
 * Created by Edgar Z. on 6/21/16.
 */

public class ConnectionsIntentService extends IntentService {
    public static final String TAG = ConnectionsIntentService.class.getName();

    private static final String ACTION_ADD_NEW_REPORT = "com.facticoapp.nuup.services.action.ADD_NEW_REPORT";
    private static final String ACTION_ADD_NEW_REPORT_AZURE = "com.facticoapp.nuup.services.action.ADD_NEW_REPORT_AZURE";

    private static final String EXTRA_REPORT = "com.facticoapp.nuup.services.extra.REPORT";
    private static final String EXTRA_REPORT_AZURE = "com.facticoapp.nuup.services.extra.REPORT_AZURE";
    public static final String EXTRA_RESULT = "com.facticoapp.nuup.services.extra.RESULT";

    public ConnectionsIntentService() {
        super("ConnectionsIntentService");
    }

    public static void startActionAddNewReport(Context context, Report report) {
        Intent intent = new Intent(context, ConnectionsIntentService.class);
        intent.setAction(ACTION_ADD_NEW_REPORT);
        intent.putExtra(EXTRA_REPORT, report);
        context.startService(intent);
    }

    public static void startActionAddNewReportAzure(Context context, ReportAzure report) {
        Intent intent = new Intent(context, ConnectionsIntentService.class);
        intent.setAction(ACTION_ADD_NEW_REPORT_AZURE);
        intent.putExtra(EXTRA_REPORT_AZURE, report);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Dialogues.Log(TAG, "============Entré onHandleIntent", Log.ERROR);
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_ADD_NEW_REPORT.equals(action)) {
                Report report = (Report) intent.getSerializableExtra(EXTRA_REPORT);
                handleActionAddNewReport(report);
            } else if (ACTION_ADD_NEW_REPORT_AZURE.equals(action)) {
                ReportAzure report = (ReportAzure) intent.getSerializableExtra(EXTRA_REPORT_AZURE);
                handleActionAddNewReportAzure(report);
            }
        }
    }

    private void handleActionAddNewReport(Report report) {
        String result = null;

        if (report != null) {
            String jsonToSend = GsonParser.createJsonFromObjectWithExposeAnnotations(report);
            Dialogues.Log(TAG, "Report json: " + jsonToSend, Log.ERROR);
            result = HttpConnection.POST(HttpConnection.REPORTS, jsonToSend);
            Dialogues.Log(TAG, "Report Result: " + result, Log.ERROR);
        }

        sendBroadcast(MainFragment.AddNewReportReceiver.ACTION_ADD_NEW_REPORT, EXTRA_RESULT, result);
    }

    private void handleActionAddNewReportAzure(ReportAzure report) {
        String result = null;

        if (report != null) {
            String jsonToSend = GsonParser.createJsonFromObjectWithExposeAnnotations(report);
            Dialogues.Log(TAG, "Register Azure json: " + jsonToSend, Log.ERROR);
            result = HttpConnection.POST(HttpConnection.REGISTER_AZURE, jsonToSend);
            Dialogues.Log(TAG, "Register Azure Result: " + result, Log.ERROR);
        }

        sendBroadcast(MainFragment.AddNewReportReceiver.ACTION_ADD_NEW_REPORT_AZURE, EXTRA_RESULT, result);
    }

    private void sendBroadcast(String action, String name, String value) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(action);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra(name, value);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
    }
}
