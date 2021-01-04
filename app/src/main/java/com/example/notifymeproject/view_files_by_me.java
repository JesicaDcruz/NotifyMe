package com.example.notifymeproject;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class view_files_by_me extends AppCompatActivity {
    String cid, Slecteditem,file_name,url,type;
    private FirebaseAuth mAuth;
    ListView myuploads;
    List<String> uploadobj;
    List<Integer> imgid;
    StorageReference mStorageRef,storeref;
    DatabaseReference dbref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_files_by_me);

        Toolbar myToolbar =  findViewById(R.id.tb_myfiles);
        myToolbar.setTitle("My File Uploads");
        myToolbar.setTitleTextAppearance(this, R.style.CustomText);
        setSupportActionBar(myToolbar);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        cid = mAuth.getCurrentUser().getUid();
        myuploads = findViewById(R.id.lst_viewupload);
        uploadobj = new ArrayList<>();
        //uploadobjurls =new ArrayList<>();
        imgid =new ArrayList<>();



        myuploads.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Slecteditem= uploadobj.get(i)+"_upload";
                //String fileurl=uploadobjurls.get(i);
               /* FirebaseStorage.getInstance().getReference().child(cid).child(Slecteditem).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String urls=uri.toString();
                        downloadFiles(Upload_files.this,Slecteditem,DIRECTORY_DOWNLOADS,urls);
                    }
                });*/

                dbref=FirebaseDatabase.getInstance().getReference().child("users").child(cid);
                dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        type = dataSnapshot.child("usertype").getValue(String.class);
                        if(type.equals("Staff")){
                            downloadSelectedFile("staff_uploads",Slecteditem);
                        }
                        else
                            downloadSelectedFile("student_uploads",Slecteditem);
                        //}
                        //Toast.makeText(Upload_files.this,dept+type,Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        viewAllFiles(cid);
    }

    private void downloadSelectedFile(String type,final String mfile){
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child(type).child(mfile);
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                file_name = dataSnapshot.child("name").getValue(String.class);
                url = dataSnapshot.child("url").getValue(String.class);
                //}
                //Toast.makeText(getApplicationContext(), "Downloading file "+file_name+" to device.", Toast.LENGTH_LONG).show();
                downloadFiles(view_files_by_me.this,file_name,DIRECTORY_DOWNLOADS,url);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void downloadFiles(Context context, String filename, String dest_dir, String url){
        Toast.makeText(getApplicationContext(), "Downloading file "+filename+" to device.", Toast.LENGTH_LONG).show();
        DownloadManager downloadManager= (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri myurl=Uri.parse(url);
        DownloadManager.Request req =new DownloadManager.Request(myurl);
        req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        req.setDestinationInExternalFilesDir(context, dest_dir,filename);
        downloadManager.enqueue(req);
    }

    private void viewAllFiles(String cid){
        uploadobj.clear();
        //Toast.makeText(this,cid,Toast.LENGTH_SHORT).show();
        storeref = mStorageRef.child(cid);
        storeref.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult){
                for (StorageReference item : listResult.getItems()) {
                    // All the items under listRef.
                    String files=item.getName();
                    //String furl = item.getDownloadUrl().toString();
                    //String[] file_path_array=files.split("/");
                    uploadobj.add(files);
                    //uploadobjurls.add(furl);
                    //Toast.makeText(Upload_files.this,"files "+files,Toast.LENGTH_LONG).show();
                }

                String fname[] = new String[uploadobj.size()];
                Integer fimgid[] = new Integer[uploadobj.size()];


                for(int i=0;i<uploadobj.size();i++){
                    imgid.add(R.drawable.fileicon);
                }
                for (int j = 0; j < uploadobj.size(); j++) {
                    // Assign each value to String array
                    fname[j] = uploadobj.get(j);
                    fimgid[j]=R.drawable.fileicon;
                }
                Customlistadapter adapter=new Customlistadapter(view_files_by_me.this, fname, fimgid);
                //ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Upload_files.this,android.R.layout.simple_list_item_1,uploadobj);
                myuploads.setAdapter(adapter);
                //adapter.notifyDataSetChanged();
            }
        });


    }

    public void refresh_content(View view) {
        //Toast.makeText(this,"refresh",Toast.LENGTH_SHORT).show();
        viewAllFiles(cid);
    }
}
