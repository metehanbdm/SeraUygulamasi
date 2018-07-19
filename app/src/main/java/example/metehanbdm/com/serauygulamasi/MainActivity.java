package example.metehanbdm.com.serauygulamasi;

import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MainActivity extends AppCompatActivity {
    private static final String DB_NAME = "task";

    private static final String TABLE_NAME = "mts420_results";
    private static String SERVER_IP = "192.168.1.5";
    private static final String SERVER_PORT = "5432";
    private static final String SERVER_USER = "tele";
    private static final String SERVER_PASS = "tiny";
    private String SERVER_URL = String.format("jdbc:postgresql://%s:%s/%s", SERVER_IP, SERVER_PORT, DB_NAME);

    private EditText node1_voltage, node1_light, node1_temp;
    private EditText node2_voltage, node2_light, node2_temp;
    EditText ip_editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ip_editText = (EditText)findViewById(R.id.MAINACTIVITY_EDITTEXT_IP);


        node1_voltage = (EditText) findViewById(R.id.MAINACTIVITY_NODE1_VOLTAGE);
        node1_light = (EditText) findViewById(R.id.MAINACTIVITY_NODE1_LIGHT);
        node1_temp = (EditText) findViewById(R.id.MAINACTIVITY_NODE1_TEMP);

        node2_voltage = (EditText) findViewById(R.id.MAINACTIVITY_NODE2_VOLTAGE);
        node2_light = (EditText) findViewById(R.id.MAINACTIVITY_NODE2_LIGHT);
        node2_temp = (EditText) findViewById(R.id.MAINACTIVITY_NODE2_TEMP);

    }

// Verilerin ekrana gelmesi için
    public void onClickedButton(View view) {
        new SqlNode1AsyncTask().execute();
        new SqlNode2AsyncTask().execute();
    }

    //NODE 1
    class SqlNode1AsyncTask extends AsyncTask<String, String, Void> {
        TableLayout baseLayout;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            baseLayout = (TableLayout) findViewById(R.id.MAINACTIVTY_TABLELAYOUT_NODE1);
            baseLayout.removeAllViews(); // Verileri temizler
            baseLayout.addView(getRowLayout(MainActivity.this, true, "", "INDEX", "TIME", "NODEID", "VOLT", "TEMP" , "LIGHT")); // Veriler gözükür

            SERVER_IP = ip_editText.getText().toString().trim();   // İp metni boş hatası
            if (SERVER_IP.isEmpty())
            {
                ip_editText.setError("Ip hatali");
                this.cancel(true);
                return;
            }


            ip_editText.setVisibility(View.INVISIBLE);
            // İp girildiyse, İp textBox'ı görünmez yapılır. Eğer yanlış İp girilirse bir süre sonra textBox geri gözükecektir.
            SERVER_URL = String.format("jdbc:postgresql://%s:%s/%s", SERVER_IP, SERVER_PORT, DB_NAME);
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                //server baglantisi ve jdbc
                Class.forName("org.postgresql.Driver");
                Connection connection = DriverManager.getConnection(SERVER_URL, SERVER_USER, SERVER_PASS);
                Statement statement = connection.createStatement();

                //baglanti acildi ve sonuc aliniyor
                ResultSet sqlResult = statement.executeQuery("SELECT * FROM " + TABLE_NAME + " WHERE NODEID = 1 ORDER BY RESULT_TIME DESC LIMIT 5");

                int i = 0;
                while (sqlResult.next()) {
                    String time = sqlResult.getString("result_time");
                    String nodeId = String.valueOf(sqlResult.getInt("nodeid"));
                    String voltage = String.valueOf(sqlResult.getInt("voltage"));
                    String temp = String.valueOf(sqlResult.getInt("prtemp"));
                    String light = String.valueOf(sqlResult.getInt("taosch0"));

                    this.publishProgress("ADD", String.valueOf(i++), time, nodeId, voltage, temp ,light);

                }
                statement.close();

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                this.publishProgress("ERROR", e.getMessage());
            } catch (SQLException e) {
                e.printStackTrace();
                this.publishProgress("ERROR", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            String category = values[0];
            switch (category) {

                case "ERROR":
                    Toast.makeText(MainActivity.this, values[1], Toast.LENGTH_LONG).show();
                    ip_editText.setVisibility(View.VISIBLE); // İp textBox 'ı görünür yapılır
                    break;
                case "ADD":
                    if (values[1].contains("0")) {
//                        this.publishProgress( "0ADD" , 1String.valueOf(i++) ,2time , 3nodeId , 4voltage , 5temp);
                        if (values[3].contains("1")) {
                            node1_voltage.setText(values[4]);
                            node1_temp.setText(values[5]);
                            node1_light.setText(values[6]);
                        } else if (values[3].contains("4")) {
                            node2_voltage.setText(values[4]);
                            node2_temp.setText(values[5]);
                            node2_light.setText(values[6]);
                        }
                    }
                    baseLayout.addView(getRowLayout(MainActivity.this, false, values));
                    break;
                case "ADD_TITLE":
                    baseLayout.addView(getRowLayout(MainActivity.this, true, values));
                    break;
            }

            super.onProgressUpdate(values);
        }


        @Override
        protected void onCancelled(Void aVoid) {
            super.onCancelled(aVoid);
        }
    }    //NODE 1


    //NODE 2
    class SqlNode2AsyncTask extends AsyncTask<String, String, Void> {
        TableLayout baseLayout;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            baseLayout = (TableLayout) findViewById(R.id.MAINACTIVTY_TABLELAYOUT_NODE2);
            baseLayout.removeAllViews();
            baseLayout.addView(getRowLayout(MainActivity.this, true, "", "INDEX", "TIME", "NODEID", "VOLT", "TEMP" , "LIGHT"));

           SERVER_IP = ip_editText.getText().toString().trim();
            if (SERVER_IP.isEmpty())
            {
                ip_editText.setError("Ip hatali");
                this.cancel(true);
                return;
            }


            ip_editText.setVisibility(View.INVISIBLE);
            SERVER_URL = String.format("jdbc:postgresql://%s:%s/%s", SERVER_IP, SERVER_PORT, DB_NAME);
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                //server baglantisi ve jdbc
                Class.forName("org.postgresql.Driver");
                Connection connection = DriverManager.getConnection(SERVER_URL, SERVER_USER, SERVER_PASS);
                Statement statement = connection.createStatement();

                //baglanti acildi ve sonuc aliniyor
                ResultSet sqlResult = statement.executeQuery("SELECT * FROM " + TABLE_NAME + " WHERE NODEID = 4 ORDER BY RESULT_TIME DESC LIMIT 5");

                int i = 0;
                while (sqlResult.next()) {
                    String time = sqlResult.getString("result_time");
                    String nodeId = String.valueOf(sqlResult.getInt("nodeid"));
                    String voltage = String.valueOf(sqlResult.getInt("voltage"));
                    String temp = String.valueOf(sqlResult.getInt("prtemp"));
                    String light = String.valueOf(sqlResult.getInt("taosch0"));

                    this.publishProgress("ADD", String.valueOf(i++), time, nodeId, voltage, temp ,light);

                }
                statement.close();

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                this.publishProgress("ERROR", e.getMessage());
            } catch (SQLException e) {
                e.printStackTrace();
                this.publishProgress("ERROR", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            String category = values[0];
            switch (category) {

                case "ERROR":
                    Toast.makeText(MainActivity.this, values[1], Toast.LENGTH_LONG).show();
                    ip_editText.setVisibility(View.VISIBLE);
                    break;
                case "ADD":
                    if (values[1].contains("0")) {
//                        this.publishProgress( "0ADD" , 1String.valueOf(i++) ,2time , 3nodeId , 4voltage , 5temp);
                        if (values[3].contains("1")) {
                            node1_voltage.setText(values[4]);
                            node1_temp.setText(values[5]);
                            node1_light.setText(values[6]);
                        } else if (values[3].contains("4")) {
                            node2_voltage.setText(values[4]);
                            node2_temp.setText(values[5]);
                            node2_light.setText(values[6]);
                        }
                    }
                    baseLayout.addView(getRowLayout(MainActivity.this, false, values));
                    break;
                case "ADD_TITLE":
                    baseLayout.addView(getRowLayout(MainActivity.this, true, values));
                    break;
            }

            super.onProgressUpdate(values);
        }


        @Override
        protected void onCancelled(Void aVoid) {
            super.onCancelled(aVoid);
        }
    }    //NODE 2


    //satirlari doldurmaca
    private TableRow getRowLayout(Context context, boolean isBold, String... values) {
        TableRow row = new TableRow(context);
        TextView textView;
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT);
        lp.setMargins(10, 10, 10, 10);

        for (int i = 1; i < values.length; i++) {
            textView = new TextView(context);
            textView.setLayoutParams(lp);
            textView.setText(values[i]);
            textView.setSingleLine();
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            if (isBold) textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
            row.addView(textView);
        }
        return row;
    }


}
