package com.restaurant;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CustInfo extends Activity {
    public static String c_name, c_mail, c_addrs, c_phone;
    EditText cust_name, cust_email, cust_phone, cust_address;
    Button save;
    ModelClass mClass;
    DBHelp cust_Helper;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.custinfo);
        try {
            cust_Helper = new DBHelp(CustInfo.this);
            //cust_Helper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //load form widget
        mClass = new ModelClass();
        save = (Button) findViewById(R.id.Save);
        cust_name = (EditText) findViewById(R.id.cust_name);
        cust_email = (EditText) findViewById(R.id.cust_email);
        cust_phone = (EditText) findViewById(R.id.cust__number);
        cust_address = (EditText) findViewById(R.id.cust_adress);


        save.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                c_name = cust_name.getText().toString();
                //System.out.println(c_name);
                c_mail = cust_email.getText().toString();
                c_phone = cust_phone.getText().toString();
                c_addrs = cust_address.getText().toString();
                mClass.setName(c_name);
                mClass.setEmail(c_mail);
                mClass.setPhone(c_phone);
                mClass.setAddress(c_addrs);
                if (c_name.equals("") || c_mail.equals("") || c_phone.equals("") || c_addrs.equals("")) {
                    Toast.makeText(CustInfo.this, "All fields are Mandatory", Toast.LENGTH_LONG).show();
                } else {
                    Intent i = new Intent(CustInfo.this, TheRoofingGuyzActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("cust_name", c_name);
                    i.putExtras(bundle);
                    startActivity(i);
                }
            /*   else{
    			   mClass.setName(c_name);
    			   mClass.setEmail(c_mail);
    			   mClass.setPhone(c_phone);
    			   mClass.setAddress(c_addrs);
    	       
    			   cust_Helper.addCustomer(mClass);
    			   
    		   }*/
            }

        });


    }

    public void onResume() {
        super.onResume();
        cust_Helper = new DBHelp(CustInfo.this);
        //cust_Helper.getWritableDatabase();
    }

    public void onStop() {
        super.onStop();
        cust_Helper.close();

    }

}
