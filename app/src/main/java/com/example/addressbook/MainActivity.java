package com.example.addressbook;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.addressbook.adapter.ContactAdapter;
import com.example.addressbook.entities.ContactInfo;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView rv_contact;
    private ContactAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        rv_contact = findViewById(R.id.rv_contact);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        rv_contact.setLayoutManager(new LinearLayoutManager(this));
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getPermissions();
    }

    private void setData() {
        List<ContactInfo> contactInfos = getContacts();
        System.out.println("联系人数量: " + contactInfos.size()); // 打印联系人数量
        if (contactInfos.isEmpty()) {
            Toast.makeText(this, "没有联系人可显示", Toast.LENGTH_SHORT).show();
        }
        adapter = new ContactAdapter(this, contactInfos);
        rv_contact.setAdapter(adapter);
    }

    @SuppressLint("Range")
    public List<ContactInfo> getContacts() {
        List<ContactInfo> contactInfos = new ArrayList<>();
        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                int isHas = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));

                // 打印调试信息
                System.out.println("联系人ID: " + id + ", 姓名: " + name + ", 是否有电话号码: " + isHas);

                if (isHas > 0) {
                    // 查询电话号码
                    Cursor phoneCursor = getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id},
                            null);

                    if (phoneCursor != null) {
                        while (phoneCursor.moveToNext()) {
                            ContactInfo info = new ContactInfo();
                            info.setContactName(name);

                            // 获取电话号码
                            String number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).trim();
                            // 清理电话号码
                            number = number.replace(" ", "").replace("-", "").replace("(", "").replace(")", "");

                            // 打印电话号码
                            System.out.println("电话号码: " + number);

                            info.setPhoneNumber(number);
                            contactInfos.add(info);
                        }
                        phoneCursor.close(); // 关闭电话号码的游标
                    }
                }
            }
            cursor.close(); // 关闭联系人游标
        } else {
            System.out.println("查询联系人失败，cursor 为 null");
        }
        return contactInfos;
    }


    String[] permissionList;

    public void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionList = new String[]{"android.permission.READ_CONTACTS"};
            ArrayList<String> list = new ArrayList<>();

            for (String permission : permissionList) {
                if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    list.add(permission);
                }
            }

            if (!list.isEmpty()) {
                ActivityCompat.requestPermissions(this, list.toArray(new String[0]), 1);
            } else {
                setData();
            }
        } else {
            setData();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals("android.permission.READ_CONTACTS")) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "读取通讯录权限申请成功", Toast.LENGTH_SHORT).show();
                        setData();
                    } else {
                        Toast.makeText(this, "读取通讯录权限申请失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }




}
