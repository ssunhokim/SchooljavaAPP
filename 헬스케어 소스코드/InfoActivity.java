package kimsunho.medicalsignalendcoderapplication;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

/*   헤더의 정보를 출력하기위한 액티비티 클래스   */
public class InfoActivity extends Activity {

    /*   위젯 변수 선언   */
    ListView mListView;                                 // MFER INFO 정보
    Button settingButton;                         //Setting 버튼

    static Context context;

    /*   어댑터 변수 및 정의한 클래스   */
    InfoListAdapter mAdapter;                           // 값 저장 하여 리스트 뷰에 보여주기위한 어댑터

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*   윈도우 타이틀지우고, 윈도우창 풀스크린으로 지정   */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_infolist);             // 해당 xml 호출하여 보여줌

        /*   위젯 아이디 지정   */
        mListView=(ListView)findViewById(R.id.infolist);
        settingButton=(Button)findViewById(R.id.settingbutton);

        mAdapter=new InfoListAdapter(this);         // 어댑터 생성자호출
        mListView.setAdapter(mAdapter);             // 어댑터 지정
        context=this;

        /*   세팅 버튼 눌렀을 시 호출   */
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogActivity settingDialog=new DialogActivity(context);           // 해당 다이얼로그 액티비티

                settingDialog.setTitle("Header Setting");           // 다이얼로그 타이틀 바 설정
                settingDialog.CHANGE = false;                       // 변경을 위한 것이 아님
                settingDialog.show();                   // 다이얼로그 호출

            }
        });
        /*   리스트뷰 클릭시 호출 -> 변경용   */
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DialogActivity settingDialog=new DialogActivity(context);           // 해당 다이얼로그 액티비티
                settingDialog.ListViewClickEvent(mAdapter.tagstring.get(position), mAdapter.valuestring.get(position));         // 다이얼로그 변경을 위해 리스트뷰의 아이템들을 전달
                settingDialog.setTitle("Change Data");          // 타이틀 바 설정
                settingDialog.CHANGE = true;            // 변경을 위한 것임
                settingDialog.mpostion = position;          // 눌린위치 저장
                settingDialog.show();           // 다이얼로그 호출
            }
        });
    }
    /*   파일읽고 데이터를 처리하여 보여주기위한 메서드   */
    void InfoList(String filename)
    {
        mAdapter.clear();                                       // 리스트뷰 아이템 초기화
        MFERDataProcedure mfer=new MFERDataProcedure(filename);                 //MFER 데이터 처리 클래스(읽기 전용 클래스)

        for(int i=0;i<mfer.tagstring.size();i++)
        {
            mAdapter.addItem(mfer.tagstring.get(i),mfer.valuestring.get(i));            // 어댑터에 데이터 저장
        }

        mListView.setAdapter(mAdapter);         // 어댑터 지정

        /*   파형 액티비티에 파형을 그리기위한 필요한 값들을 전달   */
        ((WaveActivity)WaveActivity.context).waveViewer(mfer.ENDIAN,mfer.datablock,mfer.channelcount,mfer.sequence,mfer.waveindex,mfer.datatype,mfer.waveatt.toArray(new String[mfer.waveatt.size()]),filename);
    }

    /*   리스트 뷰 갱신을 위한 메서드   */
    void RenewalList(boolean CHANGE,int index,String tag,String value){                                                                 // 헤더 세팅 후 저장

        if(CHANGE) {            // 변경사항이라면
            mAdapter.remove(index);         // 해당위치 제거후
            mAdapter.addIndexItem(index, tag, value);               // 변경된 데이터 저장
        }
        else
            mAdapter.addItem(tag, value);           // 변경 사항이 아니면 리스트 뷰 끝에 저장

        mAdapter.notifyDataSetChanged();                // 데이터가 변경되었다는 것을 알려줌
    }

    void RemoveList(int position)           // 리스트 뷰의 값을 제거 하는 메서드
    {
        mAdapter.remove(position);          // 리스트 뷰 해당 위치 제거
        mAdapter.notifyDataSetChanged();        // 데이터 변경을 알림
    }
}
