package com.example.esatgozcu.firebaseinstagramclone;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class UploadActivity extends AppCompatActivity {

    Button commentButton;
    EditText commentText;
    ImageView imageView;
    Uri selected;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_activity);

        commentButton =(Button)findViewById(R.id.postButton);
        commentText = (EditText)findViewById(R.id.commentText);
        imageView = (ImageView) findViewById(R.id.imageView);

        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();

    }

    // Post butonuna tıklanırsa..
    public void post (View view)
    {
        // Resimleri her seferinde eşsiz isimlerle kayıt etmek için UUID nesnesi türetiyoruz
        UUID uuidImage = UUID.randomUUID();

        String imageName = uuidImage+".jpg";

        StorageReference storageReference = mStorageRef.child("media").child(imageName);

        storageReference.putFile(selected).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                // Yüklediğimiz resimin url'sini alıyoruz daha sonra bu url üzerinde resimi göstereceğiz.
                String downloadURL = taskSnapshot.getDownloadUrl().toString();

                // Mevcut kullanıcıyı userEmail değişkenine aktarıyoruz daha sonra göstereceğiz.
                FirebaseUser user = mAuth.getCurrentUser();
                String userEmail = user.getEmail().toString();

                String userComment = commentText.getText().toString();


                // Postları her seferinde benzersiz sayılarla kayıt etmek için key üretiyoruz.
                String id = myRef.push().getKey();

                // Veri tabanına kayıt işlemini gerçekleştiriyoruz.
                myRef.child("Posts").child(id).child("useremail").setValue(userEmail);
                myRef.child("Posts").child(id).child("comment").setValue(userComment);
                myRef.child("Posts").child(id).child("downloadurl").setValue(downloadURL);
                myRef.child("Posts").child(id).child("id").setValue(id);
                // Beğenileri göstermek için like child oluşturuyoruz.
                myRef.child("Posts").child(id).child("like").setValue("0");

                Toast.makeText(getApplicationContext(),"Post Başarılı",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(),FeedActivity.class);
                startActivity(intent);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Hata ile karşılaşılırsa hatayı gösteriyoruz.
                Toast.makeText(getApplicationContext(),e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();
            }
        });

    }

    // Resim seçmek için üstene tıklanırsa basılırsa..
    public void choose (View view)
    {
        // İzin verilip verilmediğini kontrol ediyoruz
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            // İzin verimemiş ise izin istiyoruz
            requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},1);
        } else {
            // İzin verilmiş ise galerisine gidiyoruz.
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent,2);
        }
    }

    // Sorulan izinin sonucunu değerlendiriyoruz
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 1) {
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Eğer sorulan izine olumlu yanıt verirse galerisine gidiyoruz.
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,2);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // Galerisine gittikten sonra yapmış olduğu seçimi değerlendiriyoruz.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {

            // Eğer bir resim seçildiyse ve boş değilse..
            selected = data.getData();
            try {
                // imageView' seçtiğimiz resimi gösteriyoruz.
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selected);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                // Bir hata ile karşılaşılırsa..
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
