package kimsunho.medicalsignalendcoderapplication;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Objects;

/*   파일리스트,블루투스 통신을 할수 있는 액티비티 -> 블루투스 통신시 확인할 수 있는 옵션들이 들어있다.   */
public class FormActivity extends Activity {

    /*   위젯 변수   */
    ListView fileListViewer;                                  // 파일 리스트 뷰어
    Button connectButton;                                     // 블루투스 통신 버튼
    EditText filenameEdit;                                    // 파일 이름 에디트 텍스트
    EditText blocksizeEdit;                                  // 블락 사이즈 에디트 텍스트
    EditText indexnoEdit;                                     // 파일 저장 크기 인덱스 지정 에디트 텍스트
    EditText samplingrateEdit;                               // Samplingrate 측정 에디트 텍스트
    EditText resolutionEdit;                                  // 해상도 측정 에디트 텍스트
    /*   클래스 변수 & 어댑터 변수  */
    static Context context;
    FileListCheck fileList;                                    // 파일의 리스트들을 불러옴
    FileListAdapter adapter;                                   // 파일 리스트 뷰 어댑터
    MFERFileStore mferFileStore;                              // MFER데이터 저장 클래스 변수
    BluetoothService bluetoothService;                        // 블루투스 통신 클래스 변수
    /*   변수 처리 변수  */
    boolean PRESSED;                                    // 블루투스 버튼 눌렀을 시 유지 시켜주는 플래그
    String deviceName;                                   // 디바이스 이름
    String filename;                                     // 파일 이름

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);     //해당 View xml
        context = this;
        PRESSED = false;      //초기상태 -> 눌리지 않음

        /*   위젯 변수 아이디 지정   */
        fileList = new FileListCheck();
        fileListViewer = (ListView) findViewById(R.id.filelistview);
        connectButton = (Button) findViewById(R.id.bluetoothbutton);
        filenameEdit = (EditText) findViewById(R.id.filenameedittext);
        blocksizeEdit = (EditText) findViewById(R.id.blocksizeedittext);
        indexnoEdit = (EditText) findViewById(R.id.indexnumberedittext);
        samplingrateEdit = (EditText) findViewById(R.id.samlingrateedittext);
        resolutionEdit = (EditText) findViewById(R.id.resolutioneedittext);

        /*   위젯 초기화   */
        filenameEdit.setText("");
        blocksizeEdit.setText("0");
        indexnoEdit.setText("0");
        samplingrateEdit.setText("0");
        resolutionEdit.setText("0");

        /*   클래스 변수 초기화   */
        adapter = new FileListAdapter(this);
        mferFileStore = new MFERFileStore();
        bluetoothService = new BluetoothService(context);

        for (int i = 0; i < fileList.files.size(); i++)
            adapter.addItem(fileList.files.get(i));            //파일 목록 저장

        fileListViewer.setAdapter(adapter);         // 어댑터 셋
        /*   파일리스트 뷰 클릭 했을 때 이벤트   */
        fileListViewer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String filename = fileList.dirPath + "/" + fileList.files.get(position);
                ((InfoActivity) InfoActivity.context).InfoList(filename);
            }
        });
                            /*   연결 버튼 활성화/비활성화 이벤트   */
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PRESSED)                                             // 버튼 이 눌려져 있을경우
                {
                    connectButton.setText("Bluetooth Connect");      // 연결 되었다는 표시하고
                    connectButton.setBackground(getResources().getDrawable(R.drawable.button_shape));       // 이미지 변경
                    connectButton.setPressed(false);            // 눌려지지 않았음을 Set
                    PRESSED = false;                            // 눌리지 않았다는 것을 확인 태그 -> 버튼이 눌렸는지 아닌지 확인 하기 위해서

                    /*   스트림이 그대로 있을 경우   */
                    if (bluetoothService.mInputStream != null || bluetoothService.bluetoothSocket != null) {
                        /*   소켓 닫힘 시도   */
                        try {
                            bluetoothService.mInputStream.close();      // 스트림 소켓 닫음
                            bluetoothService.mInputStream = null;      // 스트림 null 값으로 지정
                            bluetoothService.bluetoothSocket.close();   // 블루투스 소켓 닫음
                            bluetoothService.bluetoothSocket = null;    // 블루투스 null 값으로 지정
                        } catch (Exception e) {
                            Log.e("Bluetooth Close", e.getMessage());
                        }

                        /*   위젯 초기화 작업   */
                        ((WaveActivity) WaveActivity.context).REALTIMESTART = false;           // 블루투스 통신 끊기위해 Flag값 지정
                        ((WaveActivity) WaveActivity.context).realtimeindex = 0;               // 실시간 파형데이터 인덱스 초기화
                        filenameEdit.setText("");                           // 파일 이름 초기화
                        blocksizeEdit.setText("0");                         //블락 사이즈 초기화
                        indexnoEdit.setText("0");                           // 샘플 index 초기화
                        samplingrateEdit.setText("0");                      // Sampling Rate 초기화
                        resolutionEdit.setText("0");                        // 해상도 초기화
                    }
                } else                                                    // 버튼이 안눌려져 있을경우 -> 블루투스 통신 시작
                {
                    connectButton.setText("Device is connected");       // 버튼 글자 변경
                    connectButton.setBackgroundColor(Color.parseColor("#1B978F"));      // 버튼 색상 변경
                    connectButton.setPressed(true);             // 버튼 눌려짐
                    PRESSED = true;               // 눌려짐 태그
                    bluetoothService.selectDevice();            // 디바이스 선택 메서드 호출
                    filename = Environment.getExternalStorageDirectory().getAbsoluteFile() + "/files/" + filenameEdit.getText().toString() + ".mwf";      // 파일 경로 -> 절대경로
                }
            }
        });
    }

                        /*   데이터를 실시간으로 확인 하기 위한 메서드   */
    public void DataCheckFunction(int blockindex, int indexno, long samplingrate, int wavedata) {
        double time = samplingrate;

        blocksizeEdit.setText(String.valueOf(blockindex));              // 블락 사이즈 실시간으로 호출
        indexnoEdit.setText(String.valueOf(indexno));
        samplingrateEdit.setText(String.valueOf(time / 1000) + "Sec");      // 표본추출 속도계
        mferFileStore.WaveStore(filename, wavedata);                        // 실시간으로 데이터 를 저장 -> 파형 저장장
    }

    /*   헤더를 저장하기 위한 메서드   */
    void StoreFunction(boolean STORE)
    {
        if(STORE)           // 저장 해도 된다면
        {
            /*   InfoList에 있는 데이터를 저장   */
            for (int i = 0; i < ((InfoActivity) InfoActivity.context).mAdapter.tagstring.size(); i++)
                mferFileStore.DataStore(((InfoActivity) InfoActivity.context).mAdapter.tagstring.get(i), ((InfoActivity) InfoActivity.context).mAdapter.valuestring.get(i));   // listview의 데이터 순차적으로 저장

            mferFileStore.DataStore("Wave Data", "");           // 마지막에 파형자료 저장 -> 파형을 출력하기 위해서
            mferFileStore.HeaderStore(filename);                // 헤더 저장장
       }
        else            // Cancel 버튼을 눌렀을 때
        {
            /*   원상태로 복구   */
            connectButton.setText("Bluetooth Connect");
            connectButton.setBackground(getResources().getDrawable(R.drawable.button_shape));
            connectButton.setPressed(false);
            PRESSED = false;
        }
    }
}