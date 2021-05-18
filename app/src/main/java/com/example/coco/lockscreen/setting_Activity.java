package com.example.coco.lockscreen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.coco.R;
import com.example.coco.lockscreen.util.SMSDBhelper;

import java.util.ArrayList;
import java.util.List;

public class setting_Activity extends AppCompatActivity {
    ListView listView;
    private List<String> list = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_screen);

        Button btnAdd = (Button)findViewById(R.id.btnAdd);
        Button btnDelete = (Button)findViewById(R.id.btnDelete);
        listView = findViewById(R.id.lvPhoneNum);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(intent, 1002);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SMSDBhelper smsDBHelper = new SMSDBhelper(setting_Activity.this);
                smsDBHelper.open();
                smsDBHelper.removeAllContact();
                smsDBHelper.close();
                onUpdateNumberList();
            }
        });

        onUpdateNumberList();
    }

    public void onUpdateNumberList() {
        list.clear();
        final SMSDBhelper smsDBHelper = new SMSDBhelper(setting_Activity.this);
        smsDBHelper.open();

        Cursor cursor = smsDBHelper.getAllContacts();

        if(cursor.moveToFirst()) {
            do {
                String data = cursor.getString(cursor.getColumnIndex(SMSDBhelper.COLUMN_CONTACT));
                list.add(data);
            } while (cursor.moveToNext());
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(setting_Activity.this, R.layout.items, R.id.tvItem);
        arrayAdapter.addAll(list);
        listView.setAdapter(arrayAdapter);
        cursor.close();
        smsDBHelper.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1002) {
            Cursor cursor = getContentResolver().query(data.getData(),
                    new String[] {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER},
                    null, null, null);

            cursor.moveToFirst();
            String receiveName = cursor.getString(0);
            String receivePhone = cursor.getString(1);
            cursor.close();

            final SMSDBhelper smsdBhelper = new SMSDBhelper(setting_Activity.this);
            smsdBhelper.open();
            smsdBhelper.addNewContact("이름 : " + receiveName + ", 전화번호 : " + receivePhone);
            smsdBhelper.close();
            onUpdateNumberList();
        }
    }
}