package com.restaurant;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class QuoteList extends Activity {

    private static int RESULT_LOAD_IMG = 1;
    String imgDecodableString;

    static String s1;
    static String s2;
    static String s;
    ArrayAdapter<String> adapter;
    ModelClass orderModelClass;
    ListView list;
    Button gotomenu, placedorder, button;
    DBHelp orderHelper;
    String cust_name, phone, email, order, address;
    ImageView imagePreView;
    int totalSize = 0;
    int downloadedSize = 0;
    TextView cur_val;

    Intent galleryIntent = new Intent(Intent.ACTION_PICK,
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

    // Start the Intent


    protected void Result(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                ImageView imagePreView = (ImageView) findViewById(R.id.imagePreView);
                // Set the Image in ImageView after decoding the String
                imagePreView.setImageBitmap(BitmapFactory
                        .decodeFile(imgDecodableString));

            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orderlist);
        //initialise list view to dispay data in list

        orderModelClass = new ModelClass();
        list = (ListView) findViewById(R.id.list);
        gotomenu = (Button) findViewById(R.id.gotomenu_btn);
        placedorder = (Button) findViewById(R.id.PlaceOrder_btn);
        button = (Button) findViewById(R.id.button);
        imagePreView = (ImageView) findViewById(R.id.imagePreView);


        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {


                new Thread(new Runnable() {
                    public void run() {
                        downloadFile();
                    }
                }).start();
            }
        });



        //  orderModelClass = orderHelper.getSingleInfo(LoginActivity.KY_PHONE);
            // added code-----------------


        // ---------------------------added code

        //System.out.println(mClass.getPhone());
        cust_name = orderModelClass.getName();
        phone = orderModelClass.getPhone();
        email = orderModelClass.getEmail();
        address = orderModelClass.getAddress();

        adapter = new ArrayAdapter<String>(QuoteList.this,
                android.R.layout.simple_list_item_1,
                ModelClass.al);
        list.setAdapter(adapter);
        if (ModelClass.al.isEmpty()) {
            Toast.makeText(this, "Yet no order Is placed Please go to menu section and add order", Toast.LENGTH_LONG).show();

        } else {
            s2 = ModelClass.al.get(0).toString();
        }
        list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0,
                                    View view, final int position,
                                    long id) {
                // TODO Auto-generated method stub

                //order remove functioanlity on alert box button
                AlertDialog.Builder builder = new AlertDialog.Builder(QuoteList.this);
                builder.setTitle("Order Remove");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        s = (String) list.getItemAtPosition(position);
                        ModelClass.al.remove(s);
                        adapter.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                    }
                });
                AlertDialog alt = builder.create();
                alt.show();
            }
        });
        gotomenu.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent menuIntent = new Intent(QuoteList.this, MenuScreen.class);
                startActivity(menuIntent);
            }
        });
        for (int i = 1; i < ModelClass.al.size(); i++) {
            s1 = ModelClass.al.get(i).toString();
            s2 += "," + s1;
        }


        placedorder.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    DefaultHttpClient hc = new DefaultHttpClient();
                    ResponseHandler<String> res = new BasicResponseHandler();
                    HttpPost postMethod = new HttpPost("http://10.0.2.2/ci_hardik/index.php/welcome/insert_data");

                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                    nameValuePairs.add(new BasicNameValuePair("cust_name", cust_name));
                    nameValuePairs.add(new BasicNameValuePair("phone", phone));
                    nameValuePairs.add(new BasicNameValuePair("email", email));
                    nameValuePairs.add(new BasicNameValuePair("order", s2));
                    nameValuePairs.add(new BasicNameValuePair("address", address));

                    postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    //receive response

                    String response = hc.execute(postMethod, res);
                    Log.e("data is post", response);
                    int r = Integer.parseInt(response);
                    if (r == 1) {
                        Toast.makeText(QuoteList.this, "Your order has been received", Toast.LENGTH_LONG).show();
                        //pd.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    void downloadFile(){
        String dwnload_file_path = "/sdcard/";
        try {
            URL url = new URL(dwnload_file_path);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);

            //connect
            urlConnection.connect();

            //set the path where we want to save the file
            File SDCardRoot = Environment.getExternalStorageDirectory();
            //create a new file, to save the <span id="IL_AD2" class="IL_AD">downloaded</span> file
            File file = new File(SDCardRoot,"downloaded_file.png");

            FileOutputStream fileOutput = new FileOutputStream(file);

            //Stream used for reading the data from the internet
            InputStream inputStream = urlConnection.getInputStream();

            //this is the total size of the file which we are downloading
            totalSize = urlConnection.getContentLength();

            runOnUiThread(new Runnable() {
                public void run() {

                }
            });

            //create a buffer...
            byte[] buffer = new byte[1024];
            int bufferLength = 0;

            while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
                fileOutput.write(buffer, 0, bufferLength);
                downloadedSize += bufferLength;
                // update the progressbar //
                runOnUiThread(new Runnable() {
                    public void run() {

                        float per = ((float)downloadedSize/totalSize) * 100;
                        cur_val.setText("Downloaded " + downloadedSize + "KB / " + totalSize + "KB (" + (int)per + "%)" );
                    }
                });
            }
            //close the output stream when complete //
            fileOutput.close();
            runOnUiThread(new Runnable() {
                public void run() {
                    // pb.dismiss(); // if you want close it..
                }
            });

        }catch(final MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void loadImagefromGallery(View view) {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                ImageView imagePreView = (ImageView) findViewById(R.id.imagePreView);
                // Set the Image in ImageView after decoding the String
                imagePreView.setImageBitmap(BitmapFactory
                        .decodeFile(imgDecodableString));

            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }
}