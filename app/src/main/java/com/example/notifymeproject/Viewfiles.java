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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class Viewfiles extends AppCompatActivity {
    String cid,dept,cour,sems,fname,file_uploadedto,searchuploads,Slecteditem,furl,selectedfile;
    ListView myuploads;
    List<String> uploadobjurls;
    ArrayList<String> file_name;
    List<Integer> imgid;
    DatabaseReference ref;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewfiles);

        Toolbar myToolbar =  findViewById(R.id.tb_myfiles);
        myToolbar.setTitle("Files");
        myToolbar.setTitleTextAppearance(this, R.style.CustomText);
        setSupportActionBar(myToolbar);

        mAuth = FirebaseAuth.getInstance();
        cid = mAuth.getCurrentUser().getUid();
        file_name = new ArrayList<>();
        myuploads=findViewById(R.id.lst_viewuploads);
       // uploadobj = new ArrayList<>();
        uploadobjurls =new ArrayList<>();
        imgid =new ArrayList<>();

        myuploads.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedfile=file_name.get(i);
                Slecteditem= file_name.get(i)+"_upload";
                String fileurl= uploadobjurls.get(i);
                //Toast.makeText(Viewfiles.this,Slecteditem+" "+fileurl,Toast.LENGTH_LONG).show();
                downloadFiles(Viewfiles.this,selectedfile,DIRECTORY_DOWNLOADS,fileurl);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        ref= FirebaseDatabase.getInstance().getReference().child("users").child(cid);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dept = dataSnapshot.child("udep").getValue(String.class);
                cour = dataSnapshot.child("ucourse").getValue(String.class);
                sems = dataSnapshot.child("usem").getValue(String.class);
                viewuploads(dept,cour,sems);
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

    private void viewuploads(String dep, String course, String sem){
         searchuploads=dep+"_"+course+"_"+sem;
        DatabaseReference myref=FirebaseDatabase.getInstance().getReference().child("staff_uploads");
       //myref.orderByChild("uploadTo").equalTo(searchuploads).addListenerForSingleValueEvent(new ValueEventListener() {
        myref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                     //fname = dataSnapshot.child("name").getValue(String.class);
                    alluploads(searchuploads,(Map<String,Object>) dataSnapshot.getValue());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void alluploads(String uploadedto,Map<String, Object> files) {
         file_name = new ArrayList<>();
          //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : files.entrySet()){

            //Get user map
            Map singleFile = (Map) entry.getValue();
            //Get phone field and append to list

            file_uploadedto=singleFile.get("uploadTo").toString();
            if(file_uploadedto.equals(uploadedto)){
                fname=singleFile.get("name").toString();
                furl = singleFile.get("url").toString();
                file_name.add(fname);
                uploadobjurls.add(furl);
                //Toast.makeText(this,file_uploadedto+" "+fname+" "+furl,Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(this,"No files.",Toast.LENGTH_LONG).show();
            }
            //Toast.makeText(this,file_uploadedto+fname+uploadedto,Toast.LENGTH_LONG).show();
        }
        String finame[] = new String[file_name.size()];
        Integer fimgid[] = new Integer[file_name.size()];
        for(int i=0;i<file_name.size();i++){
            imgid.add(R.drawable.fileicon);
        }
        for (int j = 0; j < file_name.size(); j++) {
            // Assign each value to String array
            finame[j] = file_name.get(j);
            fimgid[j]=R.drawable.fileicon;
        }
        Customlistadapter adapter=new Customlistadapter(Viewfiles.this, finame, fimgid);
        //ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Upload_files.this,android.R.layout.simple_list_item_1,uploadobj);
        myuploads.setAdapter(adapter);

    }

    public void refresh_content(View view) {

    }
}
