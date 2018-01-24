package com.example.esatgozcu.firebaseinstagramclone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


public class FeedActivity extends AppCompatActivity {

    ArrayList<String> useremailsFromFB;
    ArrayList<String> userimageFromFB;
    ArrayList<String> usercommentFromFB;
    ArrayList<String> userlikeFromFB;
    ArrayList<String> userIdFromFB;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference myRef;
    PostClass adapter;
    ListView listView;

    // Menü kullanmak için gerekli methodu ovveride ediyoruz
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_post,menu);

        return super.onCreateOptionsMenu(menu);
    }

    // Menüde item seçtiğimiz zaman ne olacağını yönetiyoruz.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.add_post) {

            // UploadActivity sınıfına geçiş yapıyoruz.
            Intent intent = new Intent(getApplicationContext(),UploadActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        useremailsFromFB = new ArrayList<String>();
        usercommentFromFB = new ArrayList<String>();
        userimageFromFB = new ArrayList<String>();
        userlikeFromFB = new ArrayList<String>();
        userIdFromFB = new ArrayList<String>();

        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();

        adapter = new PostClass(useremailsFromFB,userimageFromFB,usercommentFromFB,userlikeFromFB,userIdFromFB,this);

        listView = (ListView) findViewById(R.id.listView);

        listView.setAdapter(adapter);

        getData();

        // Listede bulunan itemlere uzun basılı tutulduğu zaman ne olacağını yönetiyoruz.
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // Uzun basıldığı zaman beğeni sayısını artırıyoruz.

                // Beğeni sayısını ArrayList içinden çekiyoruz.
                int like = Integer.parseInt(userlikeFromFB.get(position));
                like++;
                String stringLike = String.valueOf(like);
                String saveId =userIdFromFB.get(position);

                // Beğeni sayısını artırdıktan sonra veritabanında güncelliyoruz.
                myRef.child("Posts").child(saveId).child("like").setValue(stringLike);
                return false;
            }
        });
    }

    private void getData() {
        DatabaseReference newReference = firebaseDatabase.getReference("Posts");
        newReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // ArrayListleri temizliyoruz..
                useremailsFromFB.clear();
                userimageFromFB.clear();
                usercommentFromFB.clear();
                userlikeFromFB.clear();
                userIdFromFB.clear();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    // Veritabanından çektiğimiz verileri ArrayListlere aktarıyoruz.
                    HashMap<String, String> hashMap = (HashMap<String, String>) ds.getValue();
                    useremailsFromFB.add(hashMap.get("useremail"));
                    userimageFromFB.add(hashMap.get("downloadurl"));
                    usercommentFromFB.add(hashMap.get("comment"));
                    userlikeFromFB.add(hashMap.get("like"));
                    userIdFromFB.add(hashMap.get("id"));

                    // adapter'e değişiklik olduğunu bildiriyoruz.
                    adapter.notifyDataSetChanged();
                }
                // Son eklenen verileri en üstte gözükmesi için dizileri ters çeviriyoruz.
                Collections.reverse(useremailsFromFB);
                Collections.reverse(userimageFromFB);
                Collections.reverse(usercommentFromFB);
                Collections.reverse(userlikeFromFB);
                Collections.reverse(userIdFromFB);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
