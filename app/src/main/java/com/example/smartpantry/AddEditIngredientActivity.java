
package com.example.smartpantry;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class AddEditIngredientActivity extends AppCompatActivity {
    private DatabaseHelper db;
    private EditText name, quantity, unit, expiry;
    private long ingredientId = -1;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state); setContentView(R.layout.activity_add_edit_ingredient);
        db=new DatabaseHelper(this);
        name=findViewById(R.id.nameInput); quantity=findViewById(R.id.quantityInput);
        unit=findViewById(R.id.unitInput); expiry=findViewById(R.id.expiryInput);
        ingredientId=getIntent().getLongExtra("ingredient_id",-1);
        if(ingredientId>=0){
            ((TextView)findViewById(R.id.formTitle)).setText("Edit ingredient");
            Ingredient i=db.getIngredient(ingredientId);
            if(i!=null){name.setText(i.name);quantity.setText(String.valueOf(i.quantity));unit.setText(i.unit);expiry.setText(i.expiryDate);}
        }
        findViewById(R.id.saveIngredient).setOnClickListener(v -> save());
        findViewById(R.id.cancelForm).setOnClickListener(v -> finish());
    }
    private void save(){
        String n=name.getText().toString().trim(), q=quantity.getText().toString().trim(),
            u=unit.getText().toString().trim(), e=expiry.getText().toString().trim();
        name.setError(null);quantity.setError(null);unit.setError(null);expiry.setError(null);
        if(TextUtils.isEmpty(n)){name.setError("Enter an ingredient name");name.requestFocus();return;}
        if(TextUtils.isEmpty(q)){quantity.setError("Enter a quantity");quantity.requestFocus();return;}
        double amount;
        try{amount=Double.parseDouble(q);}catch(NumberFormatException ex){quantity.setError("Enter a valid number");return;}
        if(!Double.isFinite(amount)||amount<=0){quantity.setError("Quantity must be greater than zero");return;}
        if(TextUtils.isEmpty(u)){unit.setError("Enter a unit (e.g. g, ml, piece)");return;}
        if(!e.isEmpty()&&!validDate(e)){expiry.setError("Use YYYY-MM-DD");return;}
        try{
            if(ingredientId>=0)db.updateIngredient(ingredientId,n,amount,u,e);
            else db.addIngredient(n,amount,u,e);
            Toast.makeText(this,"Ingredient saved",Toast.LENGTH_SHORT).show();finish();
        }catch(Exception ex){Toast.makeText(this,"Could not save ingredient. Check the values and try again.",Toast.LENGTH_LONG).show();}
    }
    private boolean validDate(String value){
        SimpleDateFormat f=new SimpleDateFormat("yyyy-MM-dd",Locale.US);f.setLenient(false);
        try{f.parse(value);return value.length()==10;}catch(ParseException e){return false;}
    }
    @Override protected void onDestroy(){if(db!=null)db.close();super.onDestroy();}
}
